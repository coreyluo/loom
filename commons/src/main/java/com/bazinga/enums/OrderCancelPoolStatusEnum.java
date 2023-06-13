package com.bazinga.enums;

public enum OrderCancelPoolStatusEnum {
    INIT(0,"初始化"),
    SUCCESS(1,"报单成功"),
    FAILURE(-1,"报单失败"),
    STOP_CANCEL(2,"禁止撤单"),
    CANCEL_SUCCESS(3,"撤单成功"),
    DEAL_SUCCESS(4,"全部成交");

    private Integer code;

    private String desc;

    OrderCancelPoolStatusEnum(Integer code, String desc) {
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
