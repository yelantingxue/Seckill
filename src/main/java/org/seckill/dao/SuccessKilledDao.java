package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entities.SuccessKilled;

public interface SuccessKilledDao {

    /**
     * insert the details of the goods that were killed successfully
     * @param seckillId
     * @param userPhone
     * @return the number of insert rows
     */
    public int insertSuccessKilled(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);

    /**
     * query goods that were killed successfully using the id of secKill class
     * @param seckillId
     * @return
     */
    public SuccessKilled queryByIdWithSecKill(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);
}
