package org.orient.otc.dm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.orient.otc.common.database.config.IServicePlus;
import org.orient.otc.dm.dto.ExchangeDeleteDto;
import org.orient.otc.dm.dto.ExchangePageDto;
import org.orient.otc.dm.dto.ExchangeSaveDto;
import org.orient.otc.dm.entity.Exchange;

/**
 * (Exchange)表服务接口
 * @author makejava
 * @since 2023-07-20 13:44:58
 */
public interface ExchangeService extends IServicePlus<Exchange> {

    IPage<Exchange> selectListByPage(ExchangePageDto dto);

    String saveExchange(ExchangeSaveDto dto);

    String deleteExchange(ExchangeDeleteDto dto);
}

