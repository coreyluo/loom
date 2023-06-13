package com.bazinga.enums;

/**
 * 禁止下单操作状态
 * @author yunshan
 */
public enum OperateStatusEnum {
    SYSTEM(0, "系统操作"),
    INSERT_ORDER(1,"下单"),
    MANUAL(2, "人工操作");


    OperateStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private Integer code;

    private String desc;

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
