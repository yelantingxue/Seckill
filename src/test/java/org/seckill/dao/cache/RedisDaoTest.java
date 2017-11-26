package org.seckill.dao.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.SecKillDao;
import org.seckill.entities.SecKill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/spring-dao.xml"})
public class RedisDaoTest {

    private long id = 1001;

    @Autowired
    private RedisDao redisDao;

    @Autowired
    private SecKillDao secKillDao;

    @Test
    public void testSeckill() throws Exception {

        SecKill secKill = redisDao.getSeckill(id);

        if (secKill == null){
            secKill = secKillDao.queryById(id);

            if (secKill != null){
                String result = redisDao.putSeckill(secKill);
                System.out.println(result);

                secKill = redisDao.getSeckill(id);
                System.out.println(secKill);
            }
        }
    }

}