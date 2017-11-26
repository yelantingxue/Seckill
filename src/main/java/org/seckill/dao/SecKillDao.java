package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entities.SecKill;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SecKillDao {

    /**
     * reduce the number of the goods
     * @param seckillId
     * @param killTime
     * @return the number of rows that are influenced.
     * if the return value <= 0, this execution may not execute successfully
     */
    int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

    /**
     * query a piece of data from database
     * @param seckillId
     * @return
     */
    SecKill queryById(long seckillId);

    /**
     * query records according to the offset. Pay attention to the @Param annotation
     * @param offset: the offset
     * @param limit: the number of records to get
     * @return
     */
    List<SecKill> queryAll(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * execution seckill using stored precedure.
     * @param paraMap
     */
    void killByProcedure(Map<String, Object> paraMap);
}
