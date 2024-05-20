package org.orient.otc.quote.feign;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.orient.otc.api.quote.dto.GetCapitalMonitorMailKeywordsConfigDto;
import org.orient.otc.api.quote.dto.GetSettlementReportMailKeywordsConfigDto;
import org.orient.otc.api.quote.feign.SettlementClient;
import org.orient.otc.api.quote.vo.ClientCapitalMonitorVO;
import org.orient.otc.api.quote.vo.TradeMngVO;
import org.orient.otc.common.core.dto.SettlementDTO;
import org.orient.otc.common.core.vo.SettlementVO;
import org.orient.otc.quote.dto.risk.CapitalMonitorDTO;
import org.orient.otc.quote.dto.settlementReport.MailKeywordsConfigResultDto;
import org.orient.otc.quote.enums.CapitalMonitorConditionsEnum;
import org.orient.otc.quote.service.SettlementReportService;
import org.orient.otc.quote.service.SettlementService;
import org.orient.otc.quote.service.TradeMngService;
import org.orient.otc.quote.service.VolatilityService;
import org.orient.otc.quote.vo.settlementreport.CapitalMonitorVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author pjc
 */
@RestController
@RequestMapping(value = "settlement")
@Slf4j
public class SettlementFeignController implements SettlementClient {
    @Autowired
    private SettlementService settlementService;

    @Autowired
    private TradeMngService tradeMngService;

    @Autowired
    private VolatilityService volatilityService;

    @Autowired
    SettlementReportService settlementReportService;

    @Autowired
    HttpServletResponse response;
    @Autowired
    HttpServletRequest request;

    @Override
    public List<TradeMngVO> getNeedClosedTradeList(SettlementDTO settlementDto) {
        List<TradeMngVO> voList = tradeMngService.getSurvivalTradeByTradeDay(settlementDto.getSettlementDate());
        LocalDate finalSettlement = settlementDto.getSettlementDate().minusDays(-1);
        return voList.stream().filter(a -> a.getMaturityDate().isBefore(finalSettlement)).collect(Collectors.toList());
    }

    @Override
    public SettlementVO saveVolToTradeDay(SettlementDTO settlementDto) {
       Boolean c= volatilityService.saveVolToTradeDay(settlementDto.getSettlementDate());
       SettlementVO settlementVo = new SettlementVO();
       if (c){
           settlementVo.setIsSuccess(Boolean.TRUE);
           settlementVo.setMsg("成功复制波动率");
       }
       else {
           settlementVo.setIsSuccess(Boolean.FALSE);
           settlementVo.setMsg("复制波动率失败");
       }

       return settlementVo;
    }



    @Override
    public SettlementVO updateTradeObsDatePrice(SettlementDTO settlementDto)  {
        return settlementService.updateTradeObsDatePrice(settlementDto.getSettlementDate());
    }

    @Override
    public SettlementVO saveTradeRiskInfo(SettlementDTO settlementDto) {
        return  settlementService.saveTradeRiskInfo(settlementDto);
    }

    @Override
    public void getExchangePosition() {
        settlementService.getExchangePosition();
    }

    @Override
    public void getExchangeTrade() {
        settlementService.getExchangeTrade();
    }

    @Override
    public void checkTodayCaclPos() {
        settlementService.checkTodayCaclPos();
    }

    @Override
    public SettlementVO updateTodayPosData(SettlementDTO settlementDto) {
        return settlementService.updateTodayPosData(settlementDto);
    }

    @Override
    public SettlementVO copyPosDataToNextTradeDay() {
        return settlementService.copyPosDataToNextTradeDay();
    }

    @Override
    public SettlementVO getCheckTodayPosResult() {
        return settlementService.getCheckTodayPosResult();
    }

    @Override
    public Boolean checkObsStatus(SettlementDTO settlementDto) {
        return settlementService.checkObsStatus(settlementDto.getSettlementDate());
    }

    @Override
    public SettlementVO updateKnockedIn(SettlementDTO settlementDto) {
        return settlementService.updateKnockedIn(settlementDto);
    }

    @Override
    public SettlementVO saveCloseTradeTotalPnl() {
        return settlementService.saveCloseTradeTotalPnl();
    }

    @Override
    public ClientCapitalMonitorVO getCapitalMonitorByClientId(Integer clientId) {
        CapitalMonitorDTO dto = new CapitalMonitorDTO();
        dto.setClientIdList(Collections.singleton(clientId));
        dto.setQueryDate(LocalDate.now());
        dto.setConditionsList(Collections.singletonList(CapitalMonitorConditionsEnum.callsMargin)); // 需要追保
        IPage<CapitalMonitorVO> ipage =  settlementReportService.getCapitalMonitorListByPage(dto);
        if (CollectionUtil.isNotEmpty(ipage.getRecords())) {
            CapitalMonitorVO capitalMonitorVO = ipage.getRecords().get(0);
            ClientCapitalMonitorVO returnVO = new ClientCapitalMonitorVO();
            BeanUtils.copyProperties(capitalMonitorVO,returnVO);
            return returnVO;
        }
        return null;
    }

    @Override
    public Map<String, String> getCapitalMonitorMailKeywordsConfig(GetCapitalMonitorMailKeywordsConfigDto dto) {
        MailKeywordsConfigResultDto mailKeywordsConfigResultDto = new MailKeywordsConfigResultDto();
        BeanUtils.copyProperties(dto,mailKeywordsConfigResultDto);
        return settlementReportService.getAllMailKeywordsConfig(mailKeywordsConfigResultDto);
    }

    @Override
    public Map<String, String> getSettlementReportMailKeywordsConfig(GetSettlementReportMailKeywordsConfigDto dto) {
        MailKeywordsConfigResultDto capitalMonitorDto = new MailKeywordsConfigResultDto();
        capitalMonitorDto.setClientId(dto.getClientId());
        capitalMonitorDto.setStartDate(dto.getStartDate());
        capitalMonitorDto.setQueryDate(dto.getEndDate());
        return settlementReportService.getAllMailKeywordsConfig(capitalMonitorDto);
    }
}
