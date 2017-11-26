package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entities.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {

    @Resource
    private SuccessKilledDao successKilledDao;

    @Test
    public void insertSuccessKilled() throws Exception {

        long id = 1001L;
        long userPhone = 12345678900L;
        int rowCount = successKilledDao.insertSuccessKilled(id, userPhone);
        System.out.println(rowCount);
    }

    @Test
    public void queryByIdWithSecKill() throws Exception {
        long id = 1001L;
        long userPhone = 12345678900L;
        SuccessKilled successKilled = successKilledDao.queryByIdWithSecKill(id, userPhone);
        System.out.println(successKilled);
        System.out.println(successKilled.getSecKill());
    }

}