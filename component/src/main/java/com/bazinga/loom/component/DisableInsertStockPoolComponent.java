package com.bazinga.loom.component;


import com.bazinga.enums.OperateStatusEnum;
import com.bazinga.loom.cache.CacheManager;
import com.bazinga.loom.dto.DisableInsertStockDTO;
import com.bazinga.loom.model.DisableInsertStockPool;
import com.bazinga.loom.service.DisableInsertStockPoolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author yunshan
 */
@Component
@Slf4j
public class DisableInsertStockPoolComponent {

    @Autowired
    private DisableInsertStockPoolService disableInsertStockPoolService;


    //添加禁止下单池
    public void addManualDisableStockCode(String stockCode, Integer gearType) {
        log.info("添加禁止下单 人工操作 stockCode：{}",stockCode);
        DisableInsertStockDTO disableInsertStockDTO = new DisableInsertStockDTO();
        disableInsertStockDTO.setStockCodeGear(stockCode + gearType);
        disableInsertStockDTO.setOperateStatus(OperateStatusEnum.MANUAL.getCode());
        CacheManager.DISABLE_INSERT_STOCK_POOL.put(stockCode + gearType, disableInsertStockDTO);
        DisableInsertStockPool disableInsertStockPool = disableInsertStockPoolService.getByStockCodeGear(stockCode + gearType);
        if (disableInsertStockPool == null) {
            DisableInsertStockPool pool = new DisableInsertStockPool();
            pool.setStockCodeGear(stockCode + gearType);
            pool.setOperateStatus(OperateStatusEnum.MANUAL.getCode());
            disableInsertStockPoolService.save(pool);
        } else if (disableInsertStockPool.getOperateStatus() != OperateStatusEnum.MANUAL.getCode().intValue()) {
            disableInsertStockPool.setOperateStatus(OperateStatusEnum.MANUAL.getCode());
            disableInsertStockPoolService.updateById(disableInsertStockPool);
        }
    }

    //撤单成功，修改下单状态
    public void cancelOrderSuccessUpdateStataus(String stockCode ,Integer gearType) {
        DisableInsertStockDTO disableInsertStockDTO = CacheManager.DISABLE_INSERT_STOCK_POOL.get(stockCode);
        if (disableInsertStockDTO != null && OperateStatusEnum.INSERT_ORDER.getCode().equals(disableInsertStockDTO.getOperateStatus())) {
            disableInsertStockDTO.setOperateStatus(OperateStatusEnum.SYSTEM.getCode());
            DisableInsertStockPool forUpdate = disableInsertStockPoolService.getByStockCodeGear(stockCode + gearType);
            if (forUpdate != null) {
                forUpdate.setOperateStatus(OperateStatusEnum.SYSTEM.getCode());
                disableInsertStockPoolService.updateById(forUpdate);
            }

        }
    }



}
