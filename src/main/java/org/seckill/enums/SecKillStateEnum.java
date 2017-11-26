package org.seckill.enums;

/**
 * Use enum to express seckill state.
 */
public enum SecKillStateEnum {

    SUCCESS(1, "Seckill successfully."),
    END(0, "Seckill Ended."),
    REPEAT_KILL(-1, "Seckill repeated."),
    INNER_ERROR(-2, "System error."),
    DATA_REWRITE(-3, "Data was falsified.");

    private int state;

    private String stateInfo;

    SecKillStateEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    public int getState() {
        return state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public static SecKillStateEnum stateOf(int index){
        for (SecKillStateEnum state: values()){
            if (state.getState() == index){
                return state;
            }
        }
        return null;
    }
}
