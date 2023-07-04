package com.bazinga.enums;

public enum GearLevelEnum {
    BUY(1,"买"),
    SELL(-1,"卖");


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
