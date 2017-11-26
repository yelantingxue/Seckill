package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SecKillExecution;
import org.seckill.entities.SecKill;
import org.seckill.exception.RepeatSecKillException;
import org.seckill.exception.SecKillCloseException;
import org.seckill.exception.SecKillException;

import java.util.List;

/**
 * Service interface.
 * Three aspect: method, parameters and return value.
 */
public interface SecKillService {

    /**
     * Get all seckill records.
     * @return
     */
    public List<SecKill> getSecKillList();

    /**
     * Query a record of seckill by id.
     * @param secKillId
     * @return
     */
    public SecKill queryById(long secKillId);

    /**
     * Export the url of secKill when the seckill starts.
     * Export the system time and seckill time if the seckill doesn't start.
     * To prevent users get the seckill url before the seckill starts.
     * @param secKillId
     */
    public Exposer exportSecKillUrl(long secKillId);

    /**
     * Execute seckill.
     * @param secKillId
     * @param userPhone
     * @param md5
     */
    public SecKillExecution executeSecKill(long secKillId, long userPhone, String md5)
            throws SecKillCloseException, RepeatSecKillException, SecKillException;

    /**
     * Execute seckill by procedure.
     * @param secKillId
     * @param userPhone
     * @param md5
     */
    public SecKillExecution executeSecKillByProcedure(long secKillId, long userPhone, String md5);
}
