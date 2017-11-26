package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entities.SecKill;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Integrate Spring and JUnit to make JUnit load Spring container
 * when it starts.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/spring-dao.xml"})
public class SecKillDaoTest {

    @Resource
    private SecKillDao secKillDao;

    @Test
    public void queryById() throws Exception {

        long id = 1000;

        SecKill secKill = secKillDao.queryById(id);
        System.out.println(secKill.getName());
        System.out.println(secKill);
    }

    @Test
    public void queryAll() throws Exception {

        List<SecKill> secKills = secKillDao.queryAll(0, 100);
        for (SecKill secKill : secKills){
            System.out.println(secKill);
        }

    }

    @Test
    public void reduceNumber() throws Exception {
        Date killTime = new Date();
        int updataCount = secKillDao.reduceNumber(1000L, killTime);
        System.out.println(updataCount);
    }





}