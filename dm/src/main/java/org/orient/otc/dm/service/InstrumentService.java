package org.orient.otc.dm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.orient.otc.api.dm.vo.InstrumentInfoVo;
import org.orient.otc.common.database.config.IServicePlus;
import org.orient.otc.dm.dto.InstrumentPageDto;
import org.orient.otc.dm.dto.InstrumentUpdateDto;
import org.orient.otc.dm.dto.QueryInstrumentPage;
import org.orient.otc.dm.entity.Instrument;

import java.util.List;
import java.util.Set;

public interface InstrumentService extends IServicePlus<Instrument> {
    Page<Instrument> getListByPage(QueryInstrumentPage queryInstrumentPage);
    InstrumentInfoVo getInstrumentInfo(String instID);

    List<InstrumentInfoVo> getInstrumentInfoByIds(Set<String> instIDs);

    List<InstrumentInfoVo> getInstrumentInfoByUndeingCodes(Set<String> codes);

    String updateInstrument(InstrumentUpdateDto dto) throws Exception;

    Page<Instrument> selectListByPage(InstrumentPageDto dto);
}
