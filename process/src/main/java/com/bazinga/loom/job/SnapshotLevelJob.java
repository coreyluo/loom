package com.bazinga.loom.job;

import com.bazinga.loom.component.LoomFilterComponent;
import com.bazinga.loom.component.SnapshotComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SnapshotLevelJob {

    @Autowired
    private SnapshotComponent snapshotComponent;



    public void execute(){
        log.info("<--------------LoomFilterJob start --------------->");
        try {
          //  snapshotComponent.saveSnapshot();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        log.info("<--------------LoomFilterJob end --------------->");
    }
}
