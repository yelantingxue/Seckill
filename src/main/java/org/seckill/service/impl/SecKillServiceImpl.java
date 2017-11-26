package org.seckill.service.impl;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SecKillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SecKillExecution;
import org.seckill.entities.SecKill;
import org.seckill.entities.SuccessKilled;
import org.seckill.enums.SecKillStateEnum;
import org.seckill.exception.RepeatSecKillException;
import org.seckill.exception.SecKillCloseException;
import org.seckill.exception.SecKillException;
import org.seckill.service.SecKillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SecKillServiceImpl implements SecKillService{

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SecKillDao secKillDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    @Autowired
    private RedisDao redisDao;

    private static final String salt = "dhfwnofowedsfo*Y(TV%^)OBIUG*";

    public List<SecKill> getSecKillList() {
        return secKillDao.queryAll(0, 4);
    }

    public SecKill queryById(long secKillId) {
        return secKillDao.queryById(secKillId);
    }

    public Exposer exportSecKillUrl(long secKillId) {

        //Optimize: use redis cache
        //maintain consistency based on timeout.
        //1. Get seckill from redis.
        SecKill secKill = redisDao.getSeckill(secKillId);

        if (secKill == null){
            //2. Failed to get seckill from redis,
            //so we get seckill from MySQL.
            secKill = secKillDao.queryById(secKillId);

            if (secKill == null){
                //There is no merchandise of this seckillId.
                return new Exposer(false, secKillId);
            }else {

                //3. Put seckill to redis.
                redisDao.putSeckill(secKill);
            }
        }


        Date startTime = secKill.getStartTime();
        Date endTime = secKill.getEndTime();
        Date nowTime = new Date();
        if (nowTime.getTime() < startTime.getTime() ||
                nowTime.getTime() > endTime.getTime()){
            //Seckill doesn't start or is closed.
            return new Exposer(false, secKillId, nowTime.getTime(),
                    startTime.getTime(), endTime.getTime());
        }

        String md5 = generateMD5(secKillId);
        //Export the url of this seckill.
        return new Exposer(true, secKillId, md5);
    }


    /**
     * Generate md5.
     * @param secKillId
     * @return
     */
    private String generateMD5(long secKillId){
        String base = secKillId + "/" + salt;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    /**
     * 使用注解式声明式事务的优点：
     * 1. 开发团队达成一致，明确标注事务方法的编程风格；
     * 2. 保证事务方法的执行时间尽可能短，中间尽量不要穿插RPC/HTTP之类的操作；
     * 3. 不是所有的方法都需要事务控制，例如只读等操作不需要事务。
     * @param secKillId
     * @param userPhone
     * @param md5
     * @return
     * @throws SecKillCloseException
     * @throws RepeatSecKillException
     * @throws SecKillException
     */
    public SecKillExecution executeSecKill(long secKillId, long userPhone, String md5)
            throws SecKillCloseException, RepeatSecKillException, SecKillException {

        //validate the md5 from user.
        if (md5 == null || !md5.equals(generateMD5(secKillId))){
            throw new SecKillException("seckill data rewrite");
        }

        //business logic: reduce the number of stock and insert purchase details.
        Date nowTime = new Date();
        try {
            //record the purchase behavior.
            int insertCount = successKilledDao.insertSuccessKilled(secKillId, userPhone);

            //repeated seckill
            if (insertCount <= 0){
                throw new RepeatSecKillException("repeated seckill.");
            }else {
                int count = secKillDao.reduceNumber(secKillId, nowTime);
                //Don't influence the record.
                if (count <= 0){
                    throw new SecKillCloseException("seckill closed");
                }else {
                    //seckill successfully
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSecKill(secKillId, userPhone);
                    return new SecKillExecution(secKillId, SecKillStateEnum.SUCCESS, successKilled);
                }

            }

        }catch (SecKillCloseException e1){
            throw e1;
        }catch (RepeatSecKillException e2){
            throw e2;
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            //convert all exception to runtime exception
            throw new SecKillException("seckill inner error" + e.getMessage());
        }

    }

    public SecKillExecution executeSecKillByProcedure(long secKillId, long userPhone, String md5) {

        if (md5 == null || !md5.equals(generateMD5(secKillId))){
            return new SecKillExecution(secKillId, SecKillStateEnum.DATA_REWRITE);
        }

        Date killTime = new Date();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("seckillId", secKillId);
        map.put("phone", userPhone);
        map.put("killTime", killTime);
        map.put("result", null);
        try {
            //execute stored procedure, "result" is assigned.
            secKillDao.killByProcedure(map);

            int result = MapUtils.getInteger(map, "result", -2);

            if (result == 1){
                SuccessKilled sk = successKilledDao.queryByIdWithSecKill(secKillId, userPhone);
                return new SecKillExecution(secKillId, SecKillStateEnum.SUCCESS, sk);
            }else {
                return new SecKillExecution(secKillId, SecKillStateEnum.stateOf(result));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new SecKillExecution(secKillId, SecKillStateEnum.INNER_ERROR);
        }
    }
}
