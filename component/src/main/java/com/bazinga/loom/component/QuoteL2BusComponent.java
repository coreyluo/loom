package com.bazinga.loom.component;

import com.bazinga.constant.DateConstant;
import com.bazinga.loom.dto.CommonQuoteDTO;
import com.bazinga.util.DateTimeUtils;
import com.bazinga.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class QuoteL2BusComponent {

    public void dealWithQuote(CommonQuoteDTO commonQuoteDTO){
        Date date = new Date();
        String quoteTime = DateTimeUtils.trans2CommonFormat(commonQuoteDTO.getQuoteTime(),false);
        Date quoteDate = DateUtil.parseDate(DateConstant.TODAY_STRING + quoteTime, DateUtil.yyyyMMddHHmmssSSS);

        if(quoteDate.before(DateConstant.AM_09_25_10)){

        }


    }

}
