package org.orient.otc.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.orient.otc.common.core.dto.SettlementDTO;
import org.orient.otc.common.core.vo.SettlementVO;
import org.orient.otc.system.dto.CloseDayLogDate;
import org.orient.otc.system.dto.CloseDayLogPageDto;
import org.orient.otc.system.dto.SettlementLogDate;
import org.orient.otc.system.dto.SettlementLogPageDto;
import org.orient.otc.system.entity.CloseDayDetailLog;
import org.orient.otc.system.entity.CloseDayLog;
import org.orient.otc.system.entity.SettlementDetailLog;
import org.orient.otc.system.entity.SettlementLog;

import java.util.List;

public interface SystemCloseDayLogService {
    List<SettlementVO> settlement(SettlementDTO settlementDto);

    List<SettlementVO> closeDate();

    List<SettlementDetailLog>  getTodaySettlementLog();

    List<CloseDayDetailLog>  getCloseDayLog();

    Boolean initLog();

    Page<CloseDayLog> getCloseDateLogByPage(CloseDayLogPageDto closeDayLogPageDto);
    Page<SettlementLog> getSettlementLogByPage(SettlementLogPageDto settlementLogPageDto);

    List<CloseDayDetailLog> getCloseDateLogDetailByDate(CloseDayLogDate closeDayLogDate);

    List<SettlementDetailLog> getSettlementLogDetailByDate(SettlementLogDate settlementLogDate);

    /**
     * 清空redis数据
     * @return 清空结果
     */
    SettlementVO clearRedisData();
}
