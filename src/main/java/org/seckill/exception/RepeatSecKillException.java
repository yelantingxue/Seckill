package org.seckill.exception;

/**
 * The Exeception of repeated seckill.
 */
public class RepeatSecKillException extends SecKillException{

    public RepeatSecKillException(String message) {
        super(message);
    }

    public RepeatSecKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
