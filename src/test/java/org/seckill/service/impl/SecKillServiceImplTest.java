package org.seckill.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SecKillExecution;
import org.seckill.entities.SecKill;
import org.seckill.exception.RepeatSecKillException;
import org.seckill.exception.SecKillCloseException;
import org.seckill.service.SecKillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"
})
public class SecKillServiceImplTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SecKillService secKillService;

    @Test
    public void getSecKillList() throws Exception {
        List<SecKill> list = secKillService.getSecKillList();
        logger.info("list={}", list);
    }

    @Test
    public void queryById() throws Exception {
        long id = 1000;
        SecKill secKill = secKillService.queryById(id);
        logger.info("seckill={}", secKill);
    }

    @Test
    public void exportSecKillLogic() throws Exception {
        long id = 1001;
        Exposer exposer = secKillService.exportSecKillUrl(id);
        if (exposer.isExposed()){
            logger.info("exposer={}", exposer);
            long phone = 13839202392L;
            String md5 = exposer.getMd5();

            try {
                SecKillExecution secKillExecution = secKillService.executeSecKill(id,
                        phone, md5);
                logger.info("secKillExecution={}", secKillExecution);
            }catch (RepeatSecKillException e){
                logger.error(e.getMessage());
            } catch (SecKillCloseException e){
                logger.error(e.getMessage());
            }
        } else {
            //seckill hasn't started or is closed.
            logger.warn("exposer={}", exposer);
        }

    }

    @Test
    public void executeSecKillByProcedure(){
        long seckillId = 1001;
        long phone = 13210001824L;

        Exposer exposer = secKillService.exportSecKillUrl(seckillId);

        if (exposer.isExposed()){
            String md5 = exposer.getMd5();
            SecKillExecution execution = secKillService.executeSecKillByProcedure(seckillId, phone, md5);
            logger.info(execution.getStateInfo());
        }
    }

}