package com.bazinga.loom.util;


import com.tora.traderapi.traderapi;

public class ExchangeIdUtil {

    public static char getExchangeId(String stockCode){
        return  stockCode.startsWith("6")? traderapi.getTORA_TSTP_EXD_SSE():traderapi.getTORA_TSTP_EXD_SZSE();
    }

}
