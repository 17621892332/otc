package org.orient.otc.quote.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.orient.otc.quote.dto.riskearlywaring.RiskEarlyWarningAddDto;
import org.orient.otc.quote.dto.riskearlywaring.RiskEarlyWarningPageDto;
import org.orient.otc.quote.vo.riskearlywaring.RiskEarlyWarningVO;

public interface RiskEarlyWarningService {

    IPage<RiskEarlyWarningVO> selectListByPage(RiskEarlyWarningPageDto dto);

    String add(RiskEarlyWarningAddDto dto);
}
