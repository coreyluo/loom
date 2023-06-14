package com.bazinga.enums;

public enum GearLevelEnum {
    BUY_ONE(1,"买一"),
    BUY_TWO(2,"买一"),
    SELL_ONE(-1,"卖一"),
    SELL_TWO(-2,"卖二");


    private Integer code;

    private String desc;

    GearLevelEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
