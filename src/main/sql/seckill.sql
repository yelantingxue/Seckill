-- Stored Procedure of seckill execution

-- transfer delimiter in console to $$
DELIMITER $$

-- Define stored procedure
-- Parameters:
-- IN: input parameters;
-- OUT: output parameters.

-- row_count(): return the number of rows affected by modiffing type sql statement
-- return 0: not modify;
-- return >0: the number of rows modified;
-- return <0: sql error/not execute modify sql.
CREATE PROCEDURE `seckill`.`execute_seckill`
  (IN v_seckill_id BIGINT, IN v_phone BIGINT,
  IN v_kill_time TIMESTAMP, OUT r_result INT)
  BEGIN
    DECLARE insert_count INT DEFAULT 0;
    START TRANSACTION;
    INSERT IGNORE INTO success_killed
    (seckill_id, user_phone, create_time)
      VALUES (v_seckill_id, v_phone, v_kill_time);
    SELECT row_count() INTO insert_count;

    IF (insert_count = 0) THEN
      ROLLBACK;
      SET r_result = -1;
    ELSEIF(insert_count < 0) THEN
      ROLLBACK;
      SET r_result = -2;
    ELSE
      UPDATE seckill
        SET number = number - 1
      WHERE seckill_id = v_seckill_id
        AND end_time > v_kill_time
        AND start_time < v_kill_time
        AND number > 0;

      SELECT row_count() INTO insert_count;

      IF (insert_count = 0) THEN
        ROLLBACK;
        SET r_result = 0;
      ELSEIF (insert_count < 0) THEN
        ROLLBACK;
        SET r_result = -2;
      ELSE
        COMMIT;
        SET r_result = 1;
      END IF;

    END IF;
  END;
$$
-- Stored procedure definition ends.

DELIMITER ;

SET @r_result = -3;
-- execute stored procedure
CALL execute_seckill(1003, 18600468294, now(), @r_result);
-- get result
SELECT @r_result;