package org.seckill.dto;

import org.seckill.entities.SuccessKilled;
import org.seckill.enums.SecKillStateEnum;

/**
 * Encapsulation the result of seckill.
 */
public class SecKillExecution {

    private long secKillId;

    //The state of seckill.
    private int state;

    //Explanation of state.
    private String stateInfo;

    //Merchandise that was secKilled successfully.
    private SuccessKilled successKilled;

    /**
     * The merchandise was seckilled successfully.
     * @param secKillId
     * @param stateEnum
     * @param successKilled
     */
    public SecKillExecution(long secKillId, SecKillStateEnum stateEnum, SuccessKilled successKilled) {
        this.secKillId = secKillId;
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.successKilled = successKilled;
    }

    /**
     * Failed to seckill.
     * @param secKillId
     * @param stateEnum
     */
    public SecKillExecution(long secKillId, SecKillStateEnum stateEnum) {
        this.secKillId = secKillId;
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
    }

    public long getSecKillId() {
        return secKillId;
    }

    public void setSecKillId(long secKillId) {
        this.secKillId = secKillId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public SuccessKilled getSuccessKilled() {
        return successKilled;
    }

    public void setSuccessKilled(SuccessKilled successKilled) {
        this.successKilled = successKilled;
    }

    @Override
    public String toString() {
        return "SecKillExecution{" +
                "secKillId=" + secKillId +
                ", state=" + state +
                ", stateInfo='" + stateInfo + '\'' +
                ", successKilled=" + successKilled +
                '}';
    }
}
