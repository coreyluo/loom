package com.bazinga.loom.util;

import com.bazinga.util.DateUtil;

import java.util.Date;

public class UniqueKeyUtil {

    public static String getDealUniqueKey(String stockCode, Integer gearType, Date date){
        return stockCode+"-"+ gearType + DateUtil.format(date,DateUtil.yyyyMMdd);
    }
}
