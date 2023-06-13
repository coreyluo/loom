package com.bazinga.loom.dto;

import lombok.Data;

/**
 * @author yunshan
 * @date 2019/1/25
 */

@Data
public class DisableInsertStockDTO {

    private String stockCodeGear;
    /**
     * 0 系统操作 1 人工操作
     *
     * @允许为空   NO
     * @是否索引   NO
     */
    private Integer operateStatus;
}
