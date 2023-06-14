package com.bazinga.loom.job;


import com.bazinga.loom.component.ListenQuoteComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ListenBestCodeJob {

    @Autowired
    private ListenQuoteComponent listenQuoteComponent;



    public void execute(){
        log.info("<--------------ListenL1QuoteJob start --------------->");
        listenQuoteComponent.listenL2Quote();
        log.info("<--------------ListenL1QuoteJob end --------------->");
    }
}
