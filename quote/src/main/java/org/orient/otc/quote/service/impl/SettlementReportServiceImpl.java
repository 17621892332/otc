package org.orient.otc.quote.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.StopWatch;
import cn.hutool.extra.cglib.CglibUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.metadata.fill.FillWrapper;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jodd.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.orient.otc.api.client.dto.GrantCreditDTO;
import org.orient.otc.api.client.feign.ClientClient;
import org.orient.otc.api.client.feign.GrantCreditClient;
import org.orient.otc.api.client.vo.ClientVO;
import org.orient.otc.api.message.dto.ReSendDto;
import org.orient.otc.api.message.dto.SendMailDto;
import org.orient.otc.api.message.enums.MailTypeEnum;
import org.orient.otc.api.message.feign.SendMailClient;
import org.orient.otc.api.message.vo.MailKeywordsConfigVO;
import org.orient.otc.api.quote.dto.CloseProfitLossDTO;
import org.orient.otc.api.quote.enums.CapitalDirectionEnum;
import org.orient.otc.api.quote.enums.OptionTypeEnum;
import org.orient.otc.api.quote.enums.TradeRiskCacularResultSourceType;
import org.orient.otc.api.quote.vo.ClientCapitalMonitorVO;
import org.orient.otc.api.quote.vo.TradeObsDateVO;
import org.orient.otc.api.system.feign.DictionaryClient;
import org.orient.otc.common.core.util.BigDecimalUtil;
import org.orient.otc.common.security.dto.AuthorizeInfo;
import org.orient.otc.common.security.util.ThreadContext;
import org.orient.otc.quote.dto.risk.CapitalMonitorDTO;
import org.orient.otc.quote.dto.settlementReport.ExportAllAccSummaryDTO;
import org.orient.otc.quote.dto.settlementReport.MailDTO;
import org.orient.otc.quote.dto.settlementReport.MailKeywordsConfigResultDto;
import org.orient.otc.quote.dto.settlementReport.SettlementReportDTO;
import org.orient.otc.quote.entity.CapitalRecords;
import org.orient.otc.quote.entity.Collateral;
import org.orient.otc.quote.entity.TradeRiskInfo;
import org.orient.otc.quote.enums.CapitalMonitorConditionsEnum;
import org.orient.otc.quote.enums.SettlemenReportSheetEnum;
import org.orient.otc.quote.exeption.BussinessException;
import org.orient.otc.quote.handler.ExcelFillCellMergeStrategy;
import org.orient.otc.quote.handler.ExcelWidthStyleStrategy;
import org.orient.otc.quote.handler.SettlementReportWorkbookWriteHandler;
import org.orient.otc.quote.service.*;
import org.orient.otc.quote.vo.AccSummaryVO;
import org.orient.otc.quote.vo.CapitalRecordsVO;
import org.orient.otc.quote.vo.TradeRiskInfoVo;
import org.orient.otc.quote.vo.collateral.SettlementReportCollateralVO;
import org.orient.otc.quote.vo.settlementreport.*;
import org.orient.otc.quote.vo.trade.HistoryTradeMngVO;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
public class SettlementReportServiceImpl implements SettlementReportService {
    @Resource
    private CapitalRecordsService capitalRecordsService;
    @Resource
    private CollateralService collateralService;
    @Resource
    private TradeMngService tradeMngService;

    @Resource
    private TradeRiskInfoService tradeRiskInfoService;
    @Resource
    private TradeCloseMngService tradeCloseMngService;
    @Resource
    private GrantCreditClient grantCreditClient;

    @Resource
    private ClientClient clientClient;
    @Resource
    private SendMailClient sendMailClient;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private DictionaryClient dictionaryClient;

    @Resource
    @Qualifier("asyncTaskExecutor")
    private ThreadPoolTaskExecutor asyncTaskExecutor;

    @Value("${template.settlementReport}")
    private String settlementReportTemplatePath;

    @Override
    public AccountOverviewVO accountOverview(SettlementReportDTO dto) {
        //获取结束日的风险数据
        List<TradeRiskInfo> tradeRiskInfoList;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("风险数据");
        tradeRiskInfoList=  tradeRiskInfoService.selectTradeRiskInfoListByRiskDate(Collections.singleton(dto.getClientId()), dto.getEndDate());
        tradeRiskInfoList = tradeRiskInfoList.stream().filter(a -> a.getAvailableVolume().compareTo(BigDecimal.ZERO) > 0 && a.getTradeRiskCacularResultSourceType()== TradeRiskCacularResultSourceType.over).collect(Collectors.toList());
        tradeRiskInfoList.forEach(item -> {
            //交易数据需要由我们的方向转换为客户的方向
            item.changeDirection(item);
            item.setScale(item);
        });
        stopWatch.stop();
        stopWatch.start("资金记录");
        // 获取当前客户在结束日期前所有资金记录
        List<CapitalRecords> capitalRecordsList = capitalRecordsService.getListByVestingDate(Collections.singleton(dto.getClientId()), dto.getEndDate());
        //获取区间内的平仓盈亏
        CloseProfitLossDTO closeProfitLossDTO = new CloseProfitLossDTO();
        closeProfitLossDTO.setClientIdList(Collections.singleton(dto.getClientId()));
        closeProfitLossDTO.setStartDate(dto.getStartDate());
        closeProfitLossDTO.setEndDate(dto.getEndDate());
        stopWatch.stop();
        stopWatch.start("平仓盈亏");
        Map<Integer, BigDecimal> closeProfitLossMap = tradeMngService.getProfitLossByClient(closeProfitLossDTO);
        //获取截止至结束日期的抵押品市值
        stopWatch.stop();
        stopWatch.start("抵押记录");
        Map<Integer, BigDecimal> pledgePriceMap = collateralService.getCollateralPrice(Collections.singleton(dto.getClientId()), dto.getEndDate());
        stopWatch.stop();
        stopWatch.start("授信额度");
        GrantCreditDTO grantCreditDTO = new GrantCreditDTO();
        grantCreditDTO.setClientIdList(Collections.singleton(dto.getClientId()));
        grantCreditDTO.setEndDate(dto.getEndDate());
        Map<Integer, BigDecimal> creditPriceMap = grantCreditClient.getClientGrantCredit(grantCreditDTO);
        stopWatch.stop();
        log.debug(stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
        return getAccountOverviewVO(dto.getClientId(), dto.getStartDate(), tradeRiskInfoList, capitalRecordsList, closeProfitLossMap,
                pledgePriceMap.getOrDefault(dto.getClientId(), BigDecimal.ZERO), creditPriceMap.getOrDefault(dto.getClientId(), BigDecimal.ZERO));
    }

    /**
     * 构建账户状况对象
     * @param clientId 客户ID
     * @param startDate 开始日期
     * @param tradeRiskInfoList 持仓明细数据
     * @param capitalRecordsList 资金记录
     * @param closeProfitLossMap 已平仓数据
     * @param pledgePrice  质押市值
     * @param creditPrice 授信额度
     * @return 账户状况
     */
    private AccountOverviewVO getAccountOverviewVO(Integer clientId, LocalDate startDate, List<TradeRiskInfo> tradeRiskInfoList, List<CapitalRecords> capitalRecordsList
            , Map<Integer, BigDecimal> closeProfitLossMap, BigDecimal pledgePrice, BigDecimal creditPrice) {
        //  收支与结存
        InOutBalance inOutBalance = new InOutBalance();
        //区间的资金记录
        List<CapitalRecords> intervalCapitalRecordsList = capitalRecordsList.stream().filter(item -> !item.getVestingDate().isBefore(startDate)).collect(Collectors.toList());
        /*
          期初结存 , 起始日期这个时间之前 , 客户资金记录中状态为已结算的金额 之和
         */
        if (startDate != null) {
            inOutBalance.setStartBalance(capitalRecordsList.stream().filter(item->item.getVestingDate().isBefore(startDate)).map(CapitalRecords::getMoney).reduce(BigDecimal.ZERO, BigDecimal::add));
        } else {
            inOutBalance.setStartBalance(BigDecimal.ZERO);
        }

        /*
          成交收支 开始到结束日期区间内 , 资金方向=权利金支出/权利金收入 , 资金状态=已结算的金额之和
         */
        inOutBalance.setTradePrice(intervalCapitalRecordsList.stream().filter(item -> item.getDirection() == CapitalDirectionEnum.premiumOut || item.getDirection() == CapitalDirectionEnum.premiumIn)
                .map(CapitalRecords::getMoney).reduce(BigDecimal.ZERO, BigDecimal::add));

        /*
          了结收支 开始到结束时间区间内 ,资金方向=平仓/行权支出或平平仓/行权收入 , 状态=已结算 金额之和
         */
        inOutBalance.setClosePrice(intervalCapitalRecordsList.stream().filter(item -> item.getDirection() == CapitalDirectionEnum.exerciseOut || item.getDirection() == CapitalDirectionEnum.exerciseIn)
                .map(CapitalRecords::getMoney).reduce(BigDecimal.ZERO, BigDecimal::add));
        /*
          出入金 开始到结束时间区间内 , 资金方向=出金/入金 , 状态=已结算金额之和
         */
        inOutBalance.setInOutPrice(intervalCapitalRecordsList.stream().filter(item -> item.getDirection() == CapitalDirectionEnum.out || item.getDirection() == CapitalDirectionEnum.in)
                .map(CapitalRecords::getMoney).reduce(BigDecimal.ZERO, BigDecimal::add));
        /*
          其他收支 开始到结束时间区间内 ,资金方向=其他支出或其他收入 , 状态=已结算 金额之和
         */
        inOutBalance.setOtherPrice(intervalCapitalRecordsList.stream().filter(item -> item.getDirection() == CapitalDirectionEnum.otherOut || item.getDirection() == CapitalDirectionEnum.otherIn)
                .map(CapitalRecords::getMoney).reduce(BigDecimal.ZERO, BigDecimal::add));

        /*
          期末结存 = 期初结存+出入金+成交收支+了结收支+其他收支
         */
        BigDecimal endBalance = inOutBalance.getStartBalance()
                .add(inOutBalance.getInOutPrice())
                .add(inOutBalance.getTradePrice())
                .add(inOutBalance.getClosePrice())
                .add(inOutBalance.getOtherPrice());
        inOutBalance.setEndBalance(endBalance);

        /*
          质押市值 开始到结束时间区间内 , 抵押状态=抵押 , 执行状态=已确认 的所有抵押品价值之和
         */
        inOutBalance.setPledgePrice(pledgePrice);

        // 占用与可取
        OccupyDesirable occupyDesirable = new OccupyDesirable();
        //保证金占用
        BigDecimal marginOccupyPrice = tradeRiskInfoList.stream().filter(a -> a.getAvailableVolume().compareTo(BigDecimal.ZERO) > 0)
                .map(TradeRiskInfo::getMargin).reduce(BigDecimal.ZERO, BigDecimal::add).max(BigDecimal.ZERO);
        occupyDesirable.setMarginOccupyPrice(marginOccupyPrice);
        //授信额度
        occupyDesirable.setCreditPrice(creditPrice);


        // 可用资金 = 期末结存-保证金占用+授信额度
        BigDecimal availablePrice = endBalance.subtract(marginOccupyPrice).add(creditPrice);
        occupyDesirable.setAvailablePrice(availablePrice);

        /*
           仅有有持仓的客户才能使用授信，否则不能占用授信
          授信占用 =min{授信额度, -1*min{期末结存 - 保证金占用, 0} }
         */
        BigDecimal creditOccupyPrice = BigDecimal.ZERO;
        if (BigDecimal.ZERO.compareTo(endBalance.subtract(marginOccupyPrice)) > 0 && !tradeRiskInfoList.isEmpty()) {
            creditOccupyPrice = occupyDesirable.getCreditPrice().min(inOutBalance.getEndBalance().subtract(marginOccupyPrice).abs());
        }
        occupyDesirable.setCreditOccupyPrice(creditOccupyPrice);

        /*
         如果客户没有持仓并且期末结存+质押市值小于0，则需要追保
          追保金额 = 期末结存+质押市值+授信额度-保证占用后 的不足的部分
         */
        BigDecimal additionalPrice = BigDecimal.ZERO;
        if (tradeRiskInfoList.isEmpty()) {
            BigDecimal tempAdditionalPrice = endBalance.add(pledgePrice);
            if (BigDecimal.ZERO.compareTo(tempAdditionalPrice) > 0) { // 不足部分
                additionalPrice = tempAdditionalPrice.abs();
            }
        } else {
            BigDecimal tempAdditionalPrice = endBalance.add(pledgePrice).add(occupyDesirable.getCreditPrice()).subtract(marginOccupyPrice);
            if (BigDecimal.ZERO.compareTo(tempAdditionalPrice) > 0) { // 不足部分
                additionalPrice = tempAdditionalPrice.abs();
            }
        }
        occupyDesirable.setAdditionalPrice(additionalPrice);

        /*
          可取资金 = max(可用资金-授信额度,0)
         */
        BigDecimal desirablePrice = BigDecimal.ZERO.max(availablePrice.subtract(occupyDesirable.getCreditPrice()));
        occupyDesirable.setDesirablePrice(desirablePrice);

        // 盈亏和估值
        ProfitLossAppraisement profitLossAppraisement = new ProfitLossAppraisement();
        profitLossAppraisement.setPositionValue(BigDecimal.ZERO);
        profitLossAppraisement.setRealizeProfitLoss(BigDecimal.ZERO);
        profitLossAppraisement.setTotalAssets(BigDecimal.ZERO);
        profitLossAppraisement.setPositionProfitLoss(BigDecimal.ZERO);
        /*
          实现盈亏 统计时间区间内交易的累计盈亏之和
         */
        profitLossAppraisement.setRealizeProfitLoss(closeProfitLossMap.getOrDefault(clientId, BigDecimal.ZERO));
        if (CollectionUtils.isNotEmpty(tradeRiskInfoList)) {
            //持仓盈亏=结束日期的存续数量>0存续盈亏
            BigDecimal positionProfitLoss = tradeRiskInfoList.stream().map(item -> item.getTotalProfitLoss().subtract(item.getPositionProfitLoss())).reduce(BigDecimal.ZERO, BigDecimal::add);
            profitLossAppraisement.setPositionProfitLoss(positionProfitLoss);
            //持仓市值=结束日期存续总额之和
            BigDecimal positionValue = tradeRiskInfoList.stream().map(TradeRiskInfo::getAvailableAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            profitLossAppraisement.setPositionValue(positionValue);
        }
        //总资产=期末结存+持仓市值
        profitLossAppraisement.setTotalAssets(profitLossAppraisement.getPositionValue().add(inOutBalance.getEndBalance()));
        AccountOverviewVO accountOverviewVO = new AccountOverviewVO();
        accountOverviewVO.setInOutBalance(inOutBalance);
        accountOverviewVO.setOccupyDesirable(occupyDesirable);
        accountOverviewVO.setProfitLossAppraisement(profitLossAppraisement);
        return accountOverviewVO;
    }
    @Override
    public void exportToFile(MailDTO dto, HttpServletResponse response) {
        log.info("结算报告发送邮件入参:"+JSON.toJSONString(dto));
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        AuthorizeInfo authorizeInfo  = ThreadContext.getAuthorizeInfo();
        RLock lock = redissonClient.getLock("buildSettltmentTempFileLock");
        lock.lock();
        try {
            asyncTaskExecutor.execute(()->{
                RequestContextHolder.setRequestAttributes(attributes,true);
                ThreadContext.setAuthorizeInfo(authorizeInfo);
                // 组装发送邮件参数
                SendMailDto sendMailDto = new SendMailDto();
                BeanUtils.copyProperties(dto,sendMailDto);
                sendMailDto.setMailType(MailTypeEnum.settleReport.getKey());
                ReSendDto reSendDto = new ReSendDto();
                BeanUtils.copyProperties(dto,reSendDto);
                reSendDto.setMailTemplateId(dto.getMailTemplateId());
                if (!dto.getReportTypeSet().isEmpty()) {
                    Set<String> reportTypeSet = dto.getReportTypeSet().stream().map(Enum::name).collect(Collectors.toSet());
                    reSendDto.setReportTypeSet(reportTypeSet);
                    reSendDto.setStartDate(dto.getStartDate());
                    reSendDto.setEndDate(dto.getEndDate());
                }
                sendMailDto.setReSendParams(JSON.toJSONString(reSendDto));
                sendMailDto.setAuthorizeInfo(JSON.toJSONString(authorizeInfo));
                sendMailClient.sendMail(sendMailDto); // 发送邮件
            });
        } finally {
            lock.unlock();
        }
    }
    @Override
    public void export(SettlementReportDTO settlementReportDTO, HttpServletResponse response) throws IOException {
        Set<SettlemenReportSheetEnum> sheetEnumSet = settlementReportDTO.getReportTypeSet();
        BussinessException.E_300102.assertTrue(!sheetEnumSet.isEmpty(),"导出类型不能为空");
        StringBuilder xlsxNameBuilder = new StringBuilder();
        xlsxNameBuilder.append("持仓报告_");
        // 客户信息
        ClientVO clientVo = clientClient.getClientById(settlementReportDTO.getClientId());
        SettlementReportExcelVO excelVO = new SettlementReportExcelVO();
        excelVO.setClientCode(clientVo.getCode());
        excelVO.setClientName(clientVo.getName());
        //起始日期 xxx年xxx月xxx日
        if (settlementReportDTO.getStartDate() != null) {
            xlsxNameBuilder.append(settlementReportDTO.getStartDate().format(DatePattern.PURE_DATE_FORMATTER)).append("_");
            excelVO.setStartDateFormat(settlementReportDTO.getStartDate().format(DatePattern.CHINESE_DATE_FORMATTER));
        } else {
            excelVO.setStartDateFormat("");
        }
        //结束日期
        excelVO.setEndDate(settlementReportDTO.getEndDate().format(DatePattern.NORM_DATE_FORMATTER));
        //结束日期 xxx年xxx月xxx日
        excelVO.setEndDateFormat(settlementReportDTO.getEndDate().format(DatePattern.CHINESE_DATE_FORMATTER));
        xlsxNameBuilder.append(settlementReportDTO.getEndDate().format(DatePattern.PURE_DATE_FORMATTER)).append("_");
        xlsxNameBuilder.append(clientVo.getName());
         final String nowDateFormat = "yyyy年MM月dd日 HH:mm:ss";
        //制表时间
        excelVO.setNowDateFormat(LocalDateTime.now().format(DatePattern.createFormatter(nowDateFormat)));

        log.info("认证信息AuthorizeInfo-----"+JSON.toJSONString(ThreadContext.getAuthorizeInfo()));
        //制表人
        excelVO.setBuildReportUser(ThreadContext.getAuthorizeInfo().getName());
        //制表日期
        excelVO.setNowDateFormatChina(LocalDate.now().format(DatePattern.CHINESE_DATE_FORMATTER));
        OutputStream outputStream = response.getOutputStream();
        if ( settlementReportDTO.getSendMailFlag() != null && settlementReportDTO.getSendMailFlag()) {
            // 创建临时文件
            File tempFile = File.createTempFile(xlsxNameBuilder.toString(), ".xlsx");
            settlementReportDTO.setTempFileName(xlsxNameBuilder +".xlsx");
            // 回填临时文件
            settlementReportDTO.setTempFile(tempFile);
            outputStream = Files.newOutputStream(tempFile.toPath());
        } else {
            outputStream = response.getOutputStream();
            response.reset();
        }
        response.setContentType("application/ms-excel");
        response.setCharacterEncoding("UTF-8");
        String fileName = URLEncoder.encode(xlsxNameBuilder.append(".xlsx").toString(),"UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" +fileName);
        String templateFileName = settlementReportTemplatePath+"settlementReportTemplate.xlsx";
        /*
         * 持仓明细展示 , 区分期权类型, 不同的期权类型标题头不一样
         */
        try (ExcelWriter excelWriter = EasyExcel
                .write(outputStream)
                .withTemplate(templateFileName)
                .registerWriteHandler(new SettlementReportWorkbookWriteHandler(sheetEnumSet))
                .inMemory(true)
                .build()) {
            sheetEnumSet.forEach(sheet->{
                switch (sheet){
                    case accountOverview:
                        // 账户状况
                        AccountOverviewVO accountOverviewVO = accountOverview(settlementReportDTO);
                        excelVO.setAccountOverviewVO(accountOverviewVO);
                        writerAccountOverview(excelWriter, excelVO);
                        break;
                    case accSummary:
                        //累计汇总
                        List<AccSummaryVO> accSummaryVOList= tradeRiskInfoService.getAccSummaryList(settlementReportDTO);
                        writerAccSummary(excelWriter,accSummaryVOList);
                        break;
                    case tradeRisk:
                        // 持仓明细
                        List<TradeRiskInfo> tradeRiskInfoList ;
                        tradeRiskInfoList=  tradeRiskInfoService.selectTradeRiskInfoListByRiskDate(Collections.singleton(settlementReportDTO.getClientId()), settlementReportDTO.getEndDate());
                        tradeRiskInfoList = tradeRiskInfoList.stream().filter(a -> a.getAvailableVolume().compareTo(BigDecimal.ZERO) > 0
                                && a.getTradeRiskCacularResultSourceType()== TradeRiskCacularResultSourceType.over).collect(Collectors.toList());
                        tradeRiskInfoList.forEach(item -> {
                            //交易数据需要由我们的方向转换为客户的方向
                            item.changeDirection(item);
                            item.setScale(item);
                        });
                        writerTradeRisk(excelWriter, CglibUtil.copyList(tradeRiskInfoList,TradeRiskInfoVo::new,(db,vo)->{
                            vo.setBuyOrSellName(vo.getBuyOrSell().getDesc());
                            vo.setOptionTypeName(vo.getOptionType().getDesc());
                        }));
                        break;
                    case capital:
                        // 资金记录
                        List<CapitalRecordsVO> capitalRecordList = capitalRecordsService.getListByClient(settlementReportDTO);
                        writerCapitalList(excelWriter, capitalRecordList);
                        break;
                    case historyTrade:
                        // 历史交易
                        List<HistoryTradeMngVO> historyTradeMngList = tradeCloseMngService.historyTrade(settlementReportDTO);
                        writerHistoryTrade(excelWriter,historyTradeMngList);
                        break;
                    case collateral:
                        //抵押记录
                        List<Collateral> collateralList = collateralService.getCollateral(settlementReportDTO.getClientId(), settlementReportDTO.getEndDate());
                        writerCollateral(excelWriter, collateralList);
                        break;

                }
            });
        }
    }

    private void writerCollateral(ExcelWriter excelWriter, List<Collateral> collateralList) {
        List<SettlementReportCollateralVO> list = CglibUtil.copyList(collateralList,SettlementReportCollateralVO::new,(db,vo)->{
            vo.setCollateralStatusName(db.getCollateralStatus().getValue());
            vo.setRate(BigDecimalUtil.percentageToBigDecimal(db.getRate()));
        });
        //填充方式
        FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
        //资金记录
        WriteSheet capitalSheet = EasyExcel.writerSheet(SettlemenReportSheetEnum.collateral.getDesc())
                .registerWriteHandler(
                        new SheetWriteHandler() {
                            @Override
                            public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
                                Sheet sheet = writeSheetHolder.getSheet();
                                if (collateralList.isEmpty()) {
                                    sheet.removeRow(sheet.getRow(sheet.getLastRowNum()));
                                    sheet.removeRow(sheet.getRow(sheet.getLastRowNum()));
                                    sheet.removeRow(sheet.getRow(sheet.getLastRowNum()));
                                }
                            }
                        }
                )
                .build();
        excelWriter.fill(list, fillConfig, capitalSheet);
    }

    /**
     * 填充账户状况内容
     * @param excelWriter 导出表格
     * @param excelVO     账户状况数据
     */
    private void writerAccountOverview(ExcelWriter excelWriter, SettlementReportExcelVO excelVO) {
        //持仓汇总
        WriteSheet accountOverviewSheet = EasyExcel.writerSheet(SettlemenReportSheetEnum.accountOverview.getDesc()).build();
        excelWriter.fill(excelVO, accountOverviewSheet);
        excelWriter.fill(new FillWrapper("inOutBalance", Collections.singletonList(excelVO.getAccountOverviewVO().getInOutBalance()) ), accountOverviewSheet);
        excelWriter.fill(new FillWrapper("occupyDesirable", Collections.singletonList(excelVO.getAccountOverviewVO().getOccupyDesirable()) ), accountOverviewSheet);
        excelWriter.fill(new FillWrapper("profitLossAppraisement", Collections.singletonList(excelVO.getAccountOverviewVO().getProfitLossAppraisement()) ), accountOverviewSheet);
    }
    /**
     * 填充累计汇总内容
     * @param excelWriter 导出表格
     * @param accSummaryVOList     累计汇总数据
     */
    private void writerAccSummary(ExcelWriter excelWriter, List<AccSummaryVO> accSummaryVOList) {
        ExcelWidthStyleStrategy excelWidthStyleStrategy = new ExcelWidthStyleStrategy();
        excelWidthStyleStrategy.setNeedSetWidthColumn(Collections.singletonList(1));
        WriteSheet writeSheet = EasyExcel.writerSheet(SettlemenReportSheetEnum.accSummary.getDesc())
                .registerWriteHandler(
                    new SheetWriteHandler() {
                        @Override
                        public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
                            Sheet sheet = writeSheetHolder.getSheet();
                            if (accSummaryVOList.isEmpty()) {
                                sheet.removeRow(sheet.getRow(sheet.getLastRowNum()));
                                sheet.removeRow(sheet.getRow(sheet.getLastRowNum()));
                                sheet.removeRow(sheet.getRow(sheet.getLastRowNum()));
                                sheet.removeRow(sheet.getRow(sheet.getLastRowNum()));
                            }
                        }
                    }
                )
                .registerWriteHandler(new ExcelFillCellMergeStrategy(2, new int[]{0}))
                .registerWriteHandler(excelWidthStyleStrategy).build();
        //填充方式
        FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
        excelWriter.fill(accSummaryVOList, fillConfig, writeSheet);
        // 统计
        BigDecimal sumTodayAccumulatedPosition = accSummaryVOList.stream().map(AccSummaryVO::getTodayAccumulatedPosition).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal sumTodayAccumulatedPayment = accSummaryVOList.stream().map(AccSummaryVO::getTodayAccumulatedPayment).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal sumTodayAccumulatedPnl = accSummaryVOList.stream().map(AccSummaryVO::getTodayAccumulatedPnl).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal sumAccumulatedPosition = accSummaryVOList.stream().map(AccSummaryVO::getAccumulatedPosition).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal sumAccumulatedPayment = accSummaryVOList.stream().map(AccSummaryVO::getAccumulatedPayment).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal sumAccumulatedPnl = accSummaryVOList.stream().map(AccSummaryVO::getAccumulatedPnl).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal sumForwardVolume = accSummaryVOList.stream().map(AccSummaryVO::getForwardVolume).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal sumForwardPnl = accSummaryVOList.stream().map(AccSummaryVO::getForwardPnl).reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, BigDecimal> map = new HashMap<>();
        map.put("sumTodayAccumulatedPosition", sumTodayAccumulatedPosition);
        map.put("sumTodayAccumulatedPayment", sumTodayAccumulatedPayment);
        map.put("sumTodayAccumulatedPnl", sumTodayAccumulatedPnl);
        map.put("sumAccumulatedPosition", sumAccumulatedPosition);
        map.put("sumAccumulatedPayment", sumAccumulatedPayment);
        map.put("sumAccumulatedPnl", sumAccumulatedPnl);
        map.put("sumForwardVolume", sumForwardVolume);
        map.put("sumForwardPnl", sumForwardPnl);
        excelWriter.fill(map, writeSheet);
    }
    /**
     * 资金记录内容填充
     * @param excelWriter       导出表格
     * @param capitalRecordList 资金记录
     */
    private void writerCapitalList(ExcelWriter excelWriter, List<CapitalRecordsVO> capitalRecordList) {
        capitalRecordList.forEach(item-> item.setDirectionName(item.getDirection().getDesc()));
        //填充方式
        FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
        //资金记录
        WriteSheet capitalSheet = EasyExcel.writerSheet(SettlemenReportSheetEnum.capital.getDesc())
                .registerWriteHandler(
                        new SheetWriteHandler() {
                            @Override
                            public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
                                Sheet sheet = writeSheetHolder.getSheet();
                                if (capitalRecordList.isEmpty()) {
                                    sheet.removeRow(sheet.getRow(sheet.getLastRowNum()));
                                    sheet.removeRow(sheet.getRow(sheet.getLastRowNum()));
                                    sheet.removeRow(sheet.getRow(sheet.getLastRowNum()));
                                    sheet.removeRow(sheet.getRow(sheet.getLastRowNum()));
                                }
                            }
                        }
                )
                .build();
        excelWriter.fill(capitalRecordList, fillConfig, capitalSheet);
        // 统计
        BigDecimal capiotalTotal = capitalRecordList.stream().map(CapitalRecordsVO::getMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, BigDecimal> map = new HashMap<>();
        map.put("capiotalTotal", capiotalTotal);
        excelWriter.fill(map, capitalSheet);
    }

    /**
     * 填充持仓明细内容
     * @param excelWriter       导出表格
     * @param tradeRiskInfoList 持仓明细数据
     */
    private void writerTradeRisk(ExcelWriter excelWriter, List<TradeRiskInfoVo> tradeRiskInfoList) {

        //参数转换
        tradeRiskInfoList.forEach(item -> {
            item.setOptionTypeName(item.getOptionType().getDesc());
            item.setBuyOrSellName(item.getBuyOrSell().getDesc());
            item.setCallOrPutName(null != item.getCallOrPut()?item.getCallOrPut().getDesc():"");
            item.setOptionCombTypeName(item.getOptionCombType() != null ? item.getOptionCombType().getDesc() : "");
            item.setSettleTypeName(item.getSettleType() != null ? item.getSettleType().getDesc() : "");
            if (!item.getObsDateList().isEmpty()){
                item.setStartObsDate(item.getObsDateList().get(0).getObsDate());
                item.setObsNumber(item.getObsDateList().size());
            }
            //敲出价格如果为0时，将敲出价格设置为空
            if (item.getBarrier()!=null &&item.getBarrier().compareTo(BigDecimal.ZERO)==0){
                item.setBarrier(null);
            }

        });
        /*
         * 持仓明细展示 , 区分期权类型, 不同的期权类型标题头不一样
         */
        //香草
        List<TradeRiskInfoVo> vanillaList = tradeRiskInfoList.stream().filter(mng -> mng.getOptionType() == OptionTypeEnum.AIVanillaPricer).collect(Collectors.toList());
        //远期
        List<TradeRiskInfoVo> forwardList = tradeRiskInfoList.stream().filter(mng -> mng.getOptionType() == OptionTypeEnum.AIForwardPricer).collect(Collectors.toList());
        //亚式期权
        List<TradeRiskInfoVo> asianList = tradeRiskInfoList.stream().filter(mng -> OptionTypeEnum.getAsianOptionType().contains(mng.getOptionType())).collect(Collectors.toList());
        //累计期权
        List<TradeRiskInfoVo> accList = tradeRiskInfoList.stream().filter(mng -> OptionTypeEnum.getOrdinaryAccOptionType().contains(mng.getOptionType())).collect(Collectors.toList());
        //熔断累计期权
        List<TradeRiskInfoVo> koAccList = tradeRiskInfoList.stream().filter(mng -> OptionTypeEnum.getKOOptionType().contains(mng.getOptionType())).collect(Collectors.toList());
        //雪球期权
        List<TradeRiskInfoVo> snowBallList = tradeRiskInfoList.stream().filter(mng -> OptionTypeEnum.getSnowBall().contains(mng.getOptionType())).collect(Collectors.toList());
        ExcelWidthStyleStrategy excelWidthStyleStrategy = new ExcelWidthStyleStrategy();
        excelWidthStyleStrategy.setNeedSetWidthColumn(Arrays.asList(9,10,11,12,13,14,15,16,17,18,19,20,21,22));
        //持仓明细
        WriteSheet tradeSheet = EasyExcel.writerSheet(SettlemenReportSheetEnum.tradeRisk.getDesc())
                .registerWriteHandler(
                        new SheetWriteHandler() {
                            @Override
                            public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
                                Sheet sheet = writeSheetHolder.getSheet();
                                if (koAccList.isEmpty()) {
                                    sheet.shiftRows(25, sheet.getLastRowNum(), -5);
                                }
                                if (accList.isEmpty()) {
                                    sheet.shiftRows(20, sheet.getLastRowNum(), -5);
                                }
                                if (forwardList.isEmpty()) {
                                    sheet.shiftRows(15, sheet.getLastRowNum(), -5);
                                }
                                if (asianList.isEmpty()) {
                                    sheet.shiftRows(10, sheet.getLastRowNum(), -5);
                                }
                                if (vanillaList.isEmpty()) {
                                    sheet.shiftRows(5, sheet.getLastRowNum(), -5);
                                }
                                if (snowBallList.isEmpty()) {
                                    sheet.removeRow(sheet.getRow(sheet.getLastRowNum()));
                                    sheet.removeRow(sheet.getRow(sheet.getLastRowNum()));
                                    sheet.removeRow(sheet.getRow(sheet.getLastRowNum()));
                                    sheet.removeRow(sheet.getRow(sheet.getLastRowNum()));
                                    sheet.removeRow(sheet.getRow(sheet.getLastRowNum()));
                                }
                            }
                        }
                )
                .registerWriteHandler(excelWidthStyleStrategy)
                .build();
        //填充方式
        FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();

        //香草部分
        BigDecimal AIVanillaTotalAmountSum = vanillaList.stream().map(TradeRiskInfoVo::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal AIVanillaNotionalPrincipalSum = vanillaList.stream().map(TradeRiskInfoVo::getNotionalPrincipal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal AIVanillaPvSum = vanillaList.stream().map(TradeRiskInfoVo::getAvailableAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        //需要去掉部分平仓部分的盈亏
        BigDecimal AIVanillaPnlSum = vanillaList.stream().map(item -> item.getTotalProfitLoss().subtract(item.getPositionProfitLoss())).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal AIVanillaMarginSum = vanillaList.stream().map(TradeRiskInfoVo::getMargin).reduce(BigDecimal.ZERO, BigDecimal::add);
        excelWriter.fill(new FillWrapper("AIVanilla", vanillaList), fillConfig, tradeSheet);
        Map<String, BigDecimal> AIVanillaSum = new HashMap<>();
        AIVanillaSum.put("AIVanillaTotalAmountSum", AIVanillaTotalAmountSum);
        AIVanillaSum.put("AIVanillaNotionalPrincipalSum", AIVanillaNotionalPrincipalSum);
        AIVanillaSum.put("AIVanillaPvSum", AIVanillaPvSum);
        AIVanillaSum.put("AIVanillaPnlSum", AIVanillaPnlSum);
        AIVanillaSum.put("AIVanillaMarginSum", AIVanillaMarginSum);
        excelWriter.fill(AIVanillaSum, tradeSheet);


        //亚式
        BigDecimal AsianTotalAmountSum = asianList.stream().map(TradeRiskInfoVo::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal AsianNotionalPrincipalSum = asianList.stream().map(TradeRiskInfoVo::getNotionalPrincipal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal AsianPvSum = asianList.stream().map(TradeRiskInfoVo::getAvailableAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        //需要去掉部分平仓部分的盈亏
        BigDecimal AsianPnlSum = asianList.stream().map(item -> item.getTotalProfitLoss().subtract(item.getPositionProfitLoss())).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal AsianMarginSum = asianList.stream().map(TradeRiskInfoVo::getMargin).reduce(BigDecimal.ZERO, BigDecimal::add);
        excelWriter.fill(new FillWrapper("Asian", asianList), fillConfig, tradeSheet);
        Map<String, BigDecimal> AsianSum = new HashMap<>();
        AsianSum.put("AsianTotalAmountSum", AsianTotalAmountSum);
        AsianSum.put("AsianNotionalPrincipalSum", AsianNotionalPrincipalSum);
        AsianSum.put("AsianPvSum", AsianPvSum);
        AsianSum.put("AsianPnlSum", AsianPnlSum);
        AsianSum.put("AsianMarginSum", AsianMarginSum);
        excelWriter.fill(AsianSum, tradeSheet);

        //远期
        BigDecimal AIForwardTotalAmountSum = forwardList.stream().map(TradeRiskInfoVo::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal AIForwardNotionalPrincipalSum = forwardList.stream().map(TradeRiskInfoVo::getNotionalPrincipal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal AIForwardPvSum = forwardList.stream().map(TradeRiskInfoVo::getAvailableAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        //需要去掉部分平仓部分的盈亏
        BigDecimal AIForwardPnlSum = forwardList.stream().map(item -> item.getTotalProfitLoss().subtract(item.getPositionProfitLoss())).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal AIForwardMarginSum = forwardList.stream().map(TradeRiskInfoVo::getMargin).reduce(BigDecimal.ZERO, BigDecimal::add);
        excelWriter.fill(new FillWrapper("AIForward", forwardList), fillConfig, tradeSheet);
        Map<String, BigDecimal> AIForwardSum = new HashMap<>();
        AIForwardSum.put("AIForwardTotalAmountSum", AIForwardTotalAmountSum);
        AIForwardSum.put("AIForwardNotionalPrincipalSum", AIForwardNotionalPrincipalSum);
        AIForwardSum.put("AIForwardPvSum", AIForwardPvSum);
        AIForwardSum.put("AIForwardPnlSum", AIForwardPnlSum);
        AIForwardSum.put("AIForwardMarginSum", AIForwardMarginSum);
        excelWriter.fill(AIForwardSum, tradeSheet);

        //累计期权
        BigDecimal AccTotalAmountSum = accList.stream().map(TradeRiskInfoVo::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal AccNotionalPrincipalSum = accList.stream().map(TradeRiskInfoVo::getNotionalPrincipal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal AccPvSum = accList.stream().map(TradeRiskInfoVo::getAvailableAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        //需要去掉部分平仓部分的盈亏
        BigDecimal AccPnlSum = accList.stream().map(item -> item.getTotalProfitLoss().subtract(item.getPositionProfitLoss())).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal AccMarginSum = accList.stream().map(TradeRiskInfoVo::getMargin).reduce(BigDecimal.ZERO, BigDecimal::add);
        excelWriter.fill(new FillWrapper("Acc", accList), fillConfig, tradeSheet);
        Map<String, BigDecimal> AccSum = new HashMap<>();
        AccSum.put("AccTotalAmountSum", AccTotalAmountSum);
        AccSum.put("AccNotionalPrincipalSum", AccNotionalPrincipalSum);
        AccSum.put("AccPvSum", AccPvSum);
        AccSum.put("AccPnlSum", AccPnlSum);
        AccSum.put("AccMarginSum", AccMarginSum);
        excelWriter.fill(AccSum, tradeSheet);

        //熔断累计期权
        BigDecimal koAccTotalAmountSum = koAccList.stream().map(TradeRiskInfoVo::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal koAccNotionalPrincipalSum = koAccList.stream().map(TradeRiskInfoVo::getNotionalPrincipal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal koAccPvSum = koAccList.stream().map(TradeRiskInfoVo::getAvailableAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        //需要去掉部分平仓部分的盈亏
        BigDecimal koAccPnlSum = koAccList.stream().map(item -> item.getTotalProfitLoss().subtract(item.getPositionProfitLoss())).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal koAccMarginSum = koAccList.stream().map(TradeRiskInfoVo::getMargin).reduce(BigDecimal.ZERO, BigDecimal::add);
        excelWriter.fill(new FillWrapper("KOAcc", koAccList), fillConfig, tradeSheet);
        Map<String, BigDecimal> koAccSum = new HashMap<>();
        koAccSum.put("KOAccTotalAmountSum", koAccTotalAmountSum);
        koAccSum.put("KOAccNotionalPrincipalSum", koAccNotionalPrincipalSum);
        koAccSum.put("KOAccPvSum", koAccPvSum);
        koAccSum.put("KOAccPnlSum", koAccPnlSum);
        koAccSum.put("KOAccMarginSum", koAccMarginSum);
        excelWriter.fill(koAccSum, tradeSheet);
        snowBallList.forEach(item -> {

            //格式化敲入价格
            if (item.getKnockinBarrierValue() != null) {
                if (item.getKnockinBarrierRelative()) {
                    BigDecimal temp = item.getEntryPrice().multiply(BigDecimalUtil.percentageToBigDecimal(item.getKnockinBarrierValue())).setScale(2, RoundingMode.HALF_UP);
                    item.setKnockinBarrierValueFormat(temp + "(" + item.getKnockinBarrierValue() + "%)");
                } else {
                    item.setKnockinBarrierValueFormat(item.getKnockinBarrierValue().toString());
                }
            }

            //格式化敲入行权价格一
            if (item.getStrikeOnceKnockedinValue() != null) {
                if (item.getStrikeOnceKnockedinRelative()) {
                    BigDecimal temp = item.getEntryPrice().multiply(BigDecimalUtil.percentageToBigDecimal(item.getStrikeOnceKnockedinValue())).setScale(2, RoundingMode.HALF_UP);
                    item.setStrikeOnceKnockedinValueFormat(temp + "(" + item.getStrikeOnceKnockedinValue() + "%)");
                } else {
                    item.setStrikeOnceKnockedinValueFormat(item.getStrikeOnceKnockedinValue().toString());
                }
            }

                    //格式化敲入行权价格二
            if (item.getStrike2OnceKnockedinValue() != null) {
                if (item.getStrike2OnceKnockedinRelative()) {
                    BigDecimal temp = item.getEntryPrice().multiply(BigDecimalUtil.percentageToBigDecimal(item.getStrike2OnceKnockedinValue())).setScale(2, RoundingMode.HALF_UP);
                    item.setStrike2OnceKnockedinValueFormat(temp + "(" + item.getStrike2OnceKnockedinValue() + "%)");
                } else {
                    item.setStrike2OnceKnockedinValueFormat(item.getStrike2OnceKnockedinValue().toString());
                }
            }

                    //敲出价格
                    List<BigDecimal> barrierList = item.getObsDateList().stream().map(TradeObsDateVO::getBarrier).collect(Collectors.toList());
                    if (new HashSet<>(barrierList).size() == 1) {
                        TradeObsDateVO tradeObsDateVO = item.getObsDateList().get(0);
                        if (tradeObsDateVO.getBarrierRelative()) {
                            BigDecimal temp = item.getEntryPrice().multiply(BigDecimalUtil.percentageToBigDecimal(tradeObsDateVO.getBarrier())).setScale(2, RoundingMode.HALF_UP);
                            item.setBarrierValueFormat(temp + "(" + tradeObsDateVO.getBarrier() + "%)");
                        } else {
                            item.setBarrierValueFormat(tradeObsDateVO.getBarrier().toString());
                        }
                    } else {
                        List<String> barrierFormatList = new ArrayList<>();
                        for (TradeObsDateVO tradeObsDateVO : item.getObsDateList()) {
                            if (tradeObsDateVO.getBarrierRelative()) {
                                BigDecimal temp = item.getEntryPrice().multiply(BigDecimalUtil.percentageToBigDecimal(tradeObsDateVO.getBarrier())).setScale(2, RoundingMode.HALF_UP);
                                barrierFormatList.add(temp + "(" + tradeObsDateVO.getBarrier() + "%)");
                            } else {
                                barrierFormatList.add(tradeObsDateVO.getBarrier().toString());
                            }
                        }
                        item.setBarrierValueFormat(String.join(",", barrierFormatList));
                    }
                    //敲出观察日
                    List<String> obsDateFormatList = new ArrayList<>();
                    for (TradeObsDateVO tradeObsDateVO : item.getObsDateList()) {
                        obsDateFormatList.add(tradeObsDateVO.getObsDate().format(DatePattern.NORM_DATE_FORMATTER));
                    }
                    item.setBarrierObsDateFormat(String.join(",", obsDateFormatList));
                    //敲出票息
                    List<BigDecimal> rebateRateList = item.getObsDateList().stream().map(TradeObsDateVO::getRebateRate).collect(Collectors.toList());
                    if (new HashSet<>(rebateRateList).size() == 1) {
                        TradeObsDateVO tradeObsDateVO = item.getObsDateList().get(0);
                        item.setRebateRateFormat(tradeObsDateVO.getRebateRate().toString() + "%");
                    } else {
                        List<String> rebateRateFormatList = new ArrayList<>();
                        for (TradeObsDateVO tradeObsDateVO : item.getObsDateList()) {
                            rebateRateFormatList.add(tradeObsDateVO.getRebateRate().toString() + "%");
                        }
                        item.setRebateRateFormat(String.join(",", rebateRateFormatList));
                    }
                    //红利票息
                    item.setBonusRateStructValue(BigDecimalUtil.percentageToBigDecimal(item.getBonusRateStructValue()));
                    //是否敲入
                    item.setAlreadyKnockedInFormat(item.getAlreadyKnockedIn()!=null&&item.getAlreadyKnockedIn()?"是":"否");
                }
        );
        //雪球期权
        BigDecimal SnowBallTotalAmountSum = snowBallList.stream().map(TradeRiskInfoVo::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal SnowBallNotionalPrincipalSum = snowBallList.stream().map(TradeRiskInfoVo::getNotionalPrincipal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal SnowBallPvSum = snowBallList.stream().map(TradeRiskInfoVo::getAvailableAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        //需要去掉部分平仓部分的盈亏
        BigDecimal SnowBallPnlSum = snowBallList.stream().map(item -> item.getTotalProfitLoss().subtract(item.getPositionProfitLoss())).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal SnowBallMarginSum = snowBallList.stream().map(TradeRiskInfoVo::getMargin).reduce(BigDecimal.ZERO, BigDecimal::add);
        excelWriter.fill(new FillWrapper("SnowBall", snowBallList), fillConfig, tradeSheet);
        Map<String, BigDecimal> SnowBallSum = new HashMap<>();
        SnowBallSum.put("SnowBallTotalAmountSum", SnowBallTotalAmountSum);
        SnowBallSum.put("SnowBallNotionalPrincipalSum", SnowBallNotionalPrincipalSum);
        SnowBallSum.put("SnowBallPvSum", SnowBallPvSum);
        SnowBallSum.put("SnowBallPnlSum", SnowBallPnlSum);
        SnowBallSum.put("SnowBallMarginSum", SnowBallMarginSum);
        excelWriter.fill(SnowBallSum, tradeSheet);

    }
    /**
     * 填充历史交易内容
     * @param excelWriter       导出表格
     * @param historyTradeMngVOList 历史交易数据
     */
    private void writerHistoryTrade(ExcelWriter excelWriter, List<HistoryTradeMngVO> historyTradeMngVOList) {

        /*
         * 持仓明细展示 , 区分期权类型, 不同的期权类型标题头不一样
         */
        //香草
        List<HistoryTradeMngVO> vanillaList = historyTradeMngVOList.stream().filter(mng -> mng.getOptionType() == OptionTypeEnum.AIVanillaPricer).collect(Collectors.toList());
        //远期
        List<HistoryTradeMngVO> forwardList = historyTradeMngVOList.stream().filter(mng -> mng.getOptionType() == OptionTypeEnum.AIForwardPricer).collect(Collectors.toList());
        //亚式期权
        List<HistoryTradeMngVO> asianList = historyTradeMngVOList.stream().filter(mng -> OptionTypeEnum.getAsianOptionType().contains(mng.getOptionType())).collect(Collectors.toList());
        //累计期权
        List<HistoryTradeMngVO> accList = historyTradeMngVOList.stream().filter(mng -> OptionTypeEnum.getOrdinaryAccOptionType().contains(mng.getOptionType())).collect(Collectors.toList());
        //熔断累计期权
        List<HistoryTradeMngVO> koAccList = historyTradeMngVOList.stream().filter(mng -> OptionTypeEnum.getKOOptionType().contains(mng.getOptionType())).collect(Collectors.toList());
        //雪球期权
        List<HistoryTradeMngVO> snowBallList = historyTradeMngVOList.stream().filter(mng -> OptionTypeEnum.getSnowBall().contains(mng.getOptionType())).collect(Collectors.toList());
        //持仓明细
        WriteSheet tradeSheet = EasyExcel.writerSheet(SettlemenReportSheetEnum.historyTrade.getDesc())
                .registerWriteHandler(
                        new SheetWriteHandler() {
                            @Override
                            public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
                                Sheet sheet = writeSheetHolder.getSheet();
                                if (koAccList.isEmpty()) {
                                    sheet.shiftRows(25, sheet.getLastRowNum(), -5);
                                }
                                if (accList.isEmpty()) {
                                    sheet.shiftRows(20, sheet.getLastRowNum(), -5);
                                }
                                if (forwardList.isEmpty()) {
                                    sheet.shiftRows(15, sheet.getLastRowNum(), -5);
                                }
                                if (asianList.isEmpty()) {
                                    sheet.shiftRows(10, sheet.getLastRowNum(), -5);
                                }
                                if (vanillaList.isEmpty()) {
                                    sheet.shiftRows(5, sheet.getLastRowNum(), -5);
                                }
                                if (snowBallList.isEmpty()) {
                                    sheet.removeRow(sheet.getRow(sheet.getLastRowNum()));
                                    sheet.removeRow(sheet.getRow(sheet.getLastRowNum()));
                                    sheet.removeRow(sheet.getRow(sheet.getLastRowNum()));
                                    sheet.removeRow(sheet.getRow(sheet.getLastRowNum()));
                                    sheet.removeRow(sheet.getRow(sheet.getLastRowNum()));
                                }
                            }
                        }
                ).build();

        //填充方式
        FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();

        //香草部分
            BigDecimal AIVanillaTotalAmountSum = vanillaList.stream().map(HistoryTradeMngVO::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal AIVanillaNotionalPrincipalSum = vanillaList.stream().map(HistoryTradeMngVO::getNotionalPrincipal).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal AIVanillaCloseTotalAmountSum = vanillaList.stream().map(HistoryTradeMngVO::getCloseTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal AIVanillaProfitLossSum = vanillaList.stream().map(HistoryTradeMngVO::getProfitLoss).reduce(BigDecimal.ZERO, BigDecimal::add);
            excelWriter.fill(new FillWrapper("AIVanilla", vanillaList), fillConfig, tradeSheet);
            Map<String, BigDecimal> AIVanillaSum = new HashMap<>();
            AIVanillaSum.put("AIVanillaTotalAmountSum", AIVanillaTotalAmountSum);
            AIVanillaSum.put("AIVanillaNotionalPrincipalSum", AIVanillaNotionalPrincipalSum);
            AIVanillaSum.put("AIVanillaCloseTotalAmountSum", AIVanillaCloseTotalAmountSum);
            AIVanillaSum.put("AIVanillaProfitLossSum", AIVanillaProfitLossSum);
            excelWriter.fill(AIVanillaSum, tradeSheet);
            //亚式
            BigDecimal AsianTotalAmountSum = asianList.stream().map(HistoryTradeMngVO::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal AsianNotionalPrincipalSum = asianList.stream().map(HistoryTradeMngVO::getNotionalPrincipal).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal AsianCloseTotalAmountSum = asianList.stream().map(HistoryTradeMngVO::getCloseTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal AsianProfitLossSum = asianList.stream().map(HistoryTradeMngVO::getProfitLoss).reduce(BigDecimal.ZERO, BigDecimal::add);
            excelWriter.fill(new FillWrapper("Asian", asianList), fillConfig, tradeSheet);
            Map<String, BigDecimal> AsianSum = new HashMap<>();
            AsianSum.put("AsianTotalAmountSum", AsianTotalAmountSum);
            AsianSum.put("AsianNotionalPrincipalSum", AsianNotionalPrincipalSum);
            AsianSum.put("AsianCloseTotalAmountSum", AsianCloseTotalAmountSum);
            AsianSum.put("AsianProfitLossSum", AsianProfitLossSum);
            excelWriter.fill(AsianSum, tradeSheet);

        //远期
            BigDecimal AIForwardTotalAmountSum = forwardList.stream().map(HistoryTradeMngVO::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal AIForwardNotionalPrincipalSum = forwardList.stream().map(HistoryTradeMngVO::getNotionalPrincipal).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal AIForwardCloseTotalAmountSum = forwardList.stream().map(HistoryTradeMngVO::getCloseTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal AIForwardProfitLossSum = forwardList.stream().map(HistoryTradeMngVO::getProfitLoss).reduce(BigDecimal.ZERO, BigDecimal::add);
            excelWriter.fill(new FillWrapper("AIForward", forwardList), fillConfig, tradeSheet);
            Map<String, BigDecimal> AIForwardSum = new HashMap<>();
            AIForwardSum.put("AIForwardTotalAmountSum", AIForwardTotalAmountSum);
            AIForwardSum.put("AIForwardNotionalPrincipalSum", AIForwardNotionalPrincipalSum);
            AIForwardSum.put("AIForwardCloseTotalAmountSum", AIForwardCloseTotalAmountSum);
            AIForwardSum.put("AIForwardProfitLossSum", AIForwardProfitLossSum);
            excelWriter.fill(AIForwardSum, tradeSheet);

        //累计期权
            BigDecimal AccTotalAmountSum = accList.stream().map(HistoryTradeMngVO::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal AccNotionalPrincipalSum = accList.stream().map(HistoryTradeMngVO::getNotionalPrincipal).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal AccCloseTotalAmountSum = accList.stream().map(HistoryTradeMngVO::getCloseTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal AccProfitLossSum = accList.stream().map(HistoryTradeMngVO::getProfitLoss).reduce(BigDecimal.ZERO, BigDecimal::add);
            excelWriter.fill(new FillWrapper("Acc", accList), fillConfig, tradeSheet);
            Map<String, BigDecimal> AccSum = new HashMap<>();
            AccSum.put("AccTotalAmountSum", AccTotalAmountSum);
            AccSum.put("AccNotionalPrincipalSum", AccNotionalPrincipalSum);
            AccSum.put("AccCloseTotalAmountSum", AccCloseTotalAmountSum);
            AccSum.put("AccProfitLossSum", AccProfitLossSum);
            excelWriter.fill(AccSum, tradeSheet);

        //熔断累计期权
            BigDecimal KOAccTotalAmountSum = koAccList.stream().map(HistoryTradeMngVO::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal KOAccNotionalPrincipalSum = koAccList.stream().map(HistoryTradeMngVO::getNotionalPrincipal).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal KOAccCloseTotalAmountSum = koAccList.stream().map(HistoryTradeMngVO::getCloseTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal KOAccProfitLossSum = koAccList.stream().map(HistoryTradeMngVO::getProfitLoss).reduce(BigDecimal.ZERO, BigDecimal::add);
            excelWriter.fill(new FillWrapper("KOAcc", koAccList), fillConfig, tradeSheet);
            Map<String, BigDecimal> KOAccSum = new HashMap<>();
            KOAccSum.put("KOAccTotalAmountSum", KOAccTotalAmountSum);
            KOAccSum.put("KOAccNotionalPrincipalSum", KOAccNotionalPrincipalSum);
            KOAccSum.put("KOAccCloseTotalAmountSum", KOAccCloseTotalAmountSum);
            KOAccSum.put("KOAccProfitLossSum", KOAccProfitLossSum);
            excelWriter.fill(KOAccSum, tradeSheet);

        //雪球期权
            BigDecimal SnowBallTotalAmountSum = snowBallList.stream().map(HistoryTradeMngVO::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal SnowBallNotionalPrincipalSum = snowBallList.stream().map(HistoryTradeMngVO::getNotionalPrincipal).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal SnowBallCloseTotalAmountSum = snowBallList.stream().map(HistoryTradeMngVO::getCloseTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal SnowBallProfitLossSum = snowBallList.stream().map(HistoryTradeMngVO::getProfitLoss).reduce(BigDecimal.ZERO, BigDecimal::add);
            snowBallList.forEach(item -> {

                //格式化敲入价格
                if (item.getKnockinBarrierRelative() != null) {
                    if (item.getKnockinBarrierRelative()) {
                        BigDecimal temp = item.getEntryPrice().multiply(BigDecimalUtil.percentageToBigDecimal(item.getKnockinBarrierValue())).setScale(2, RoundingMode.HALF_UP);
                        item.setKnockinBarrierValueFormat(temp + "(" + item.getKnockinBarrierValue() + "%)");
                    } else {
                        item.setKnockinBarrierValueFormat(item.getKnockinBarrierValue().toString());
                    }
                }

                //格式化敲入行权价格一
                if (item.getStrikeOnceKnockedinRelative() != null) {
                    if (item.getStrikeOnceKnockedinRelative()) {
                        BigDecimal temp = item.getEntryPrice().multiply(BigDecimalUtil.percentageToBigDecimal(item.getStrikeOnceKnockedinValue())).setScale(2, RoundingMode.HALF_UP);
                        item.setStrikeOnceKnockedinValueFormat(temp + "(" + item.getStrikeOnceKnockedinValue() + "%)");
                    } else {
                        item.setStrikeOnceKnockedinValueFormat(item.getStrikeOnceKnockedinValue().toString());
                    }
                }

                //格式化敲入行权价格二
                if (item.getStrike2OnceKnockedinRelative() != null) {
                    if (item.getStrike2OnceKnockedinRelative()) {
                        BigDecimal temp = item.getEntryPrice().multiply(BigDecimalUtil.percentageToBigDecimal(item.getStrike2OnceKnockedinValue())).setScale(2, RoundingMode.HALF_UP);
                        item.setStrike2OnceKnockedinValueFormat(temp + "(" + item.getStrike2OnceKnockedinValue() + "%)");
                    } else {
                        item.setStrike2OnceKnockedinValueFormat(item.getStrike2OnceKnockedinValue().toString());
                    }
                }

                //敲出价格
                List<BigDecimal> barrierList = item.getObsDateList().stream().map(TradeObsDateVO::getBarrier).collect(Collectors.toList());
                if (new HashSet<>(barrierList).size() == 1) {
                    TradeObsDateVO tradeObsDateVO = item.getObsDateList().get(0);
                    if (tradeObsDateVO.getBarrierRelative()) {
                        BigDecimal temp = item.getEntryPrice().multiply(BigDecimalUtil.percentageToBigDecimal(tradeObsDateVO.getBarrier())).setScale(2, RoundingMode.HALF_UP);
                        item.setBarrierValueFormat(temp + "(" + tradeObsDateVO.getBarrier() + "%)");
                    } else {
                        item.setBarrierValueFormat(tradeObsDateVO.getBarrier().toString());
                    }
                } else {
                    List<String> barrierFormatList = new ArrayList<>();
                    for (TradeObsDateVO tradeObsDateVO : item.getObsDateList()) {
                        if (tradeObsDateVO.getBarrierRelative()) {
                            BigDecimal temp = item.getEntryPrice().multiply(BigDecimalUtil.percentageToBigDecimal(tradeObsDateVO.getBarrier())).setScale(2, RoundingMode.HALF_UP);
                            barrierFormatList.add(temp + "(" + tradeObsDateVO.getBarrier() + "%)");
                        } else {
                            barrierFormatList.add(tradeObsDateVO.getBarrier().toString());
                        }
                    }
                    item.setBarrierValueFormat(String.join(",", barrierFormatList));
                }
                //敲出观察日
                List<String> obsDateFormatList = new ArrayList<>();
                for (TradeObsDateVO tradeObsDateVO : item.getObsDateList()) {
                    obsDateFormatList.add(tradeObsDateVO.getObsDate().format(DatePattern.NORM_DATE_FORMATTER));
                }
                item.setBarrierObsDateFormat(String.join(",", obsDateFormatList));
                //敲出票息
                List<BigDecimal> rebateRateList = item.getObsDateList().stream().map(TradeObsDateVO::getRebateRate).collect(Collectors.toList());
                if (new HashSet<>(rebateRateList).size() == 1) {
                    TradeObsDateVO tradeObsDateVO = item.getObsDateList().get(0);
                    item.setRebateRateFormat(tradeObsDateVO.getRebateRate().toString() + "%");
                } else {
                    List<String> rebateRateFormatList = new ArrayList<>();
                    for (TradeObsDateVO tradeObsDateVO : item.getObsDateList()) {
                        rebateRateFormatList.add(tradeObsDateVO.getRebateRate().toString() + "%");
                    }
                    item.setRebateRateFormat(String.join(",", rebateRateFormatList));
                }
                //红利票息
                item.setBonusRateStructValue(BigDecimalUtil.percentageToBigDecimal(item.getBonusRateStructValue()));
                //是否敲入
                item.setAlreadyKnockedInFormat(item.getAlreadyKnockedIn()!=null&&item.getAlreadyKnockedIn() ? "是" : "否");
            });
            excelWriter.fill(new FillWrapper("SnowBall", snowBallList), fillConfig, tradeSheet);
            Map<String, BigDecimal> SnowBallSum = new HashMap<>();
            SnowBallSum.put("SnowBallTotalAmountSum", SnowBallTotalAmountSum);
            SnowBallSum.put("SnowBallNotionalPrincipalSum", SnowBallNotionalPrincipalSum);
            SnowBallSum.put("SnowBallCloseTotalAmountSum", SnowBallCloseTotalAmountSum);
            SnowBallSum.put("SnowBallProfitLossSum", SnowBallProfitLossSum);
            excelWriter.fill(SnowBallSum, tradeSheet);
    }

    @Override
    public void exportAllAccSummary(ExportAllAccSummaryDTO exportAllAccSummaryDTO, HttpServletResponse response) {
        List<TradeRiskInfo> tradeRiskInfoList = tradeRiskInfoService.selectTradeRiskInfoListByRiskDate(new HashSet<>(), exportAllAccSummaryDTO.getEndDate());
        tradeRiskInfoList = tradeRiskInfoList.stream().filter(a -> a.getAvailableVolume().compareTo(BigDecimal.ZERO) > 0
                && a.getTradeRiskCacularResultSourceType() == TradeRiskCacularResultSourceType.over).collect(Collectors.toList());
        Map<Integer, List<TradeRiskInfo>> tradeRiskInfoMap = tradeRiskInfoList.stream().collect(Collectors.groupingBy(TradeRiskInfo::getClientId));
        //客户信息
        Map<Integer, ClientVO> clientVOMap = clientClient.getClientListByIds(tradeRiskInfoMap.keySet()).stream().collect(Collectors.toMap(ClientVO::getId, item -> item, (v1, v2) -> v2));
        String zipName = "累计汇总_" + exportAllAccSummaryDTO.getEndDate();
        String templateFileName = settlementReportTemplatePath + "settlementReportTemplate.xlsx";
        Set<SettlemenReportSheetEnum> sheetEnumSet = new HashSet<>();

        sheetEnumSet.add(SettlemenReportSheetEnum.accSummary);
        try (ServletOutputStream outputStream = response.getOutputStream();
             ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            String fileName = URLEncoder.encode(zipName + ".zip", "UTF-8");
            response.setContentType("application/zip");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            for (Map.Entry<Integer, List<TradeRiskInfo>> entry : tradeRiskInfoMap.entrySet()) {
                //仅获取对应的累计期权
                List<TradeRiskInfo> dbList = entry.getValue();
                List<TradeRiskInfo> accList = dbList.stream().filter(acc -> OptionTypeEnum.getAccOption().contains(acc.getOptionType())).collect(Collectors.toList());
                if (accList.isEmpty()) {
                    continue;
                }
                //获取对应的远期
                List<TradeRiskInfo> forwardList = dbList.stream().filter(forward -> forward.getOptionType() == OptionTypeEnum.AIForwardPricer
                        && forward.getRelevanceTradeCode() != null).collect(Collectors.toList());

                ByteArrayOutputStream dataOutputStream = new ByteArrayOutputStream();
                try (ExcelWriter excelWriter = EasyExcel
                        .write(dataOutputStream)
                        .withTemplate(templateFileName)
                        .registerWriteHandler(new SettlementReportWorkbookWriteHandler(sheetEnumSet))
                        .build()) {
                    //填充累计汇总数据
                    writerAccSummary(excelWriter, tradeRiskInfoService.getAccSummaryList(accList, forwardList));
                }
                zipOutputStream.putNextEntry(new ZipEntry(clientVOMap.get(entry.getKey()).getName() + ".xlsx"));
                zipOutputStream.write(dataOutputStream.toByteArray());
                zipOutputStream.flush();
                zipOutputStream.closeEntry();
            }
        } catch (Exception e) {
            log.error("文件导出失败", e);
        }
    }

    @Override
    public IPage<CapitalMonitorVO> getCapitalMonitorListByPage(CapitalMonitorDTO capitalMonitorDTO) {
        List<CapitalMonitorVO> capitalMonitorVOList = getCapitalMonitorList(capitalMonitorDTO);
        int pageNo = 1;
        int pageSize = 100;
        if (capitalMonitorDTO.getPageNo() != null && capitalMonitorDTO.getPageSize() != null) {
            pageNo = capitalMonitorDTO.getPageNo();
            pageSize = capitalMonitorDTO.getPageSize();
        }
        // 总条数
        int totalCount = capitalMonitorVOList.size();
        int totalPage; // 总页数
        totalPage = totalCount / pageSize;
        if (totalCount % pageSize != 0) {
            ++totalPage;
        }
        List<CapitalMonitorVO> list = capitalMonitorVOList.stream().skip((long) (pageNo - 1) * pageSize).limit(pageSize).collect(Collectors.toList());
        IPage<CapitalMonitorVO> page = new Page<>();
        page.setRecords(list);
        page.setTotal(totalCount);
        page.setSize(pageSize);
        page.setCurrent(pageNo);
        page.setPages(totalPage);
        return page;

    }

    private List<CapitalMonitorVO> getCapitalMonitorList(CapitalMonitorDTO capitalMonitorDTO) {
        List<ClientVO> clientVOList = clientClient.getClientListByIds(capitalMonitorDTO.getClientIdList() != null ? capitalMonitorDTO.getClientIdList() : null);
        clientVOList = clientVOList.stream()
                .filter(item -> {
                    //内部客户筛选条件
                    if (capitalMonitorDTO.getIsInsided() != null) {
                        return Objects.equals(item.getIsInsided(), capitalMonitorDTO.getIsInsided());
                    } else {
                        return true;
                    }
                })
                .filter(item -> {
                    //监管客户类别筛选条件
                    if (capitalMonitorDTO.getClientSuperviseTypeList() != null && !capitalMonitorDTO.getClientSuperviseTypeList().isEmpty()) {
                        return capitalMonitorDTO.getClientSuperviseTypeList().contains(item.getClientSuperviseType());
                    } else {
                        return true;
                    }
                })
                .collect(Collectors.toList());
        //过滤后的客户数据
        Set<Integer> clientIdSet = clientVOList.stream().map(ClientVO::getId).collect(Collectors.toSet());
        //获取结束日的风险数据
        List<TradeRiskInfo> tradeRiskInfoList;
        tradeRiskInfoList = tradeRiskInfoService.selectTradeRiskInfoListByRiskDate(clientIdSet, capitalMonitorDTO.getQueryDate());
        tradeRiskInfoList = tradeRiskInfoList.stream().filter(a -> a.getAvailableVolume().compareTo(BigDecimal.ZERO) > 0
                && a.getTradeRiskCacularResultSourceType() == TradeRiskCacularResultSourceType.over).collect(Collectors.toList());
        tradeRiskInfoList.forEach(item -> {
            //交易数据需要由我们的方向转换为客户的方向
            item.changeDirection(item);
            item.setScale(item);
        });
        Map<Integer, List<TradeRiskInfo>> tradeRiskInfoMap = tradeRiskInfoList.stream().collect(Collectors.groupingBy(TradeRiskInfo::getClientId));
        // 获取在时间段前已结算的所有资金记录
        List<CapitalRecords> capitalRecordsList = capitalRecordsService.getListByVestingDate(clientIdSet, capitalMonitorDTO.getQueryDate());
        Map<Integer, List<CapitalRecords>> capitalRecordsMap = capitalRecordsList.stream().collect(Collectors.groupingBy(CapitalRecords::getClientId));
        //获取区间内的平仓盈亏
        CloseProfitLossDTO closeProfitLossDTO = new CloseProfitLossDTO();
        closeProfitLossDTO.setClientIdList(clientIdSet);
        closeProfitLossDTO.setStartDate(capitalMonitorDTO.getQueryDate());
        closeProfitLossDTO.setEndDate(capitalMonitorDTO.getQueryDate());
        Map<Integer, BigDecimal> closeProfitLossMap = tradeMngService.getProfitLossByClient(closeProfitLossDTO);
        //获取截止至结束日期的抵押品市值
        Map<Integer, BigDecimal> pledgePriceMap = collateralService.getCollateralPrice(clientIdSet, capitalMonitorDTO.getQueryDate());
        //获取客户的授信额度
        GrantCreditDTO grantCreditDTO = new GrantCreditDTO();
        grantCreditDTO.setClientIdList(clientIdSet);
        grantCreditDTO.setEndDate(capitalMonitorDTO.getQueryDate());
        Map<Integer, BigDecimal> creditPriceMap = grantCreditClient.getClientGrantCredit(grantCreditDTO);
        //获取字典内容
        List<CapitalMonitorVO> capitalMonitorVOList = new ArrayList<>();
        for (ClientVO clientVo : clientVOList) {
            AccountOverviewVO accountOverviewVO = getAccountOverviewVO(clientVo.getId(), capitalMonitorDTO.getQueryDate(), tradeRiskInfoMap.getOrDefault(clientVo.getId(), new ArrayList<>())
                    , capitalRecordsMap.getOrDefault(clientVo.getId(), new ArrayList<>())
                    , closeProfitLossMap, pledgePriceMap.getOrDefault(clientVo.getId(), BigDecimal.ZERO), creditPriceMap.getOrDefault(clientVo.getId(), BigDecimal.ZERO));
            CapitalMonitorVO capitalMonitorVO = CglibUtil.copy(accountOverviewVO, CapitalMonitorVO.class);
            capitalMonitorVO.setClientCode(clientVo.getCode());
            capitalMonitorVO.setClientName(clientVo.getName());
            capitalMonitorVO.setClientLevelId(clientVo.getLevelId());
            capitalMonitorVO.setClientLevelName(clientVo.getLevelName());
            capitalMonitorVO.setClientId(clientVo.getId());
            capitalMonitorVO.setIsInsided(clientVo.getIsInsided());
            capitalMonitorVO.setClientSuperviseType(clientVo.getClientSuperviseType());
            capitalMonitorVOList.add(capitalMonitorVO);
        }
        if (capitalMonitorDTO.getConditionsList() != null && !capitalMonitorDTO.getConditionsList().isEmpty()) {
            capitalMonitorVOList = capitalMonitorVOList.stream().filter(
                    item -> (capitalMonitorDTO.getConditionsList().contains(CapitalMonitorConditionsEnum.callsMargin)
                            && item.getOccupyDesirable().getAdditionalPrice().compareTo(BigDecimal.ZERO) > 0)
                            || (capitalMonitorDTO.getConditionsList().contains(CapitalMonitorConditionsEnum.callsMoney)
                            && item.getOccupyDesirable().getAdditionalPrice().compareTo(BigDecimal.ZERO) > 0
                            && item.getProfitLossAppraisement().getPositionValue().compareTo(BigDecimal.ZERO) == 0)
                            || (capitalMonitorDTO.getConditionsList().contains(CapitalMonitorConditionsEnum.havaPosition)
                            && item.getProfitLossAppraisement().getPositionValue().compareTo(BigDecimal.ZERO) > 0)
                            || (capitalMonitorDTO.getConditionsList().contains(CapitalMonitorConditionsEnum.userGrantCredit)
                            && item.getOccupyDesirable().getCreditOccupyPrice().compareTo(BigDecimal.ZERO) > 0)
            ).collect(Collectors.toList());
        }
        return capitalMonitorVOList;
    }

    @Override
    public void exportCapitalMonitor(CapitalMonitorDTO capitalMonitorDTO, HttpServletResponse response) throws IOException {
        List<CapitalMonitorVO> capitalMonitorVOList = getCapitalMonitorList(capitalMonitorDTO);
        //标的资产类型
        Map<String, String> clientSuperviseTypeMap = dictionaryClient.getDictionaryMapByIds("ClientSuperviseType");

        List<CapitalMonitorExcelVO> excelVOList = CglibUtil.copyList(capitalMonitorVOList, CapitalMonitorExcelVO::new, (vo, excelVO) -> {
            //客户信息
            excelVO.setIsInternal(vo.getIsInsided() != null && vo.getIsInsided() == 1 ? "是" : "否");
            if (vo.getClientSuperviseType() != null) {
                excelVO.setClientSuperviseType(clientSuperviseTypeMap.get(vo.getClientSuperviseType().toString()));
            }
            //收支与结存
            excelVO.setStartBalance(vo.getInOutBalance().getStartBalance());
            excelVO.setEndBalance(vo.getInOutBalance().getEndBalance());
            excelVO.setInOutPrice(vo.getInOutBalance().getInOutPrice());
            excelVO.setTradePrice(vo.getInOutBalance().getTradePrice());
            excelVO.setClosePrice(vo.getInOutBalance().getClosePrice());
            excelVO.setOtherPrice(vo.getInOutBalance().getOtherPrice());
            excelVO.setPledgePrice(vo.getInOutBalance().getPledgePrice());
            //占用与可取
            excelVO.setMarginOccupyPrice(vo.getOccupyDesirable().getMarginOccupyPrice());
            excelVO.setAvailablePrice(vo.getOccupyDesirable().getAvailablePrice());
            excelVO.setCreditPrice(vo.getOccupyDesirable().getCreditPrice());
            excelVO.setCreditOccupyPrice(vo.getOccupyDesirable().getCreditOccupyPrice());
            excelVO.setAdditionalPrice(vo.getOccupyDesirable().getAdditionalPrice());
            excelVO.setDesirablePrice(vo.getOccupyDesirable().getDesirablePrice());
            //盈亏和估值
            excelVO.setRealizeProfitLoss(vo.getProfitLossAppraisement().getRealizeProfitLoss());
            excelVO.setPositionProfitLoss(vo.getProfitLossAppraisement().getPositionProfitLoss());
            excelVO.setPositionValue(vo.getProfitLossAppraisement().getPositionValue());
            excelVO.setTotalAssets(vo.getProfitLossAppraisement().getTotalAssets());
        });
        response.setContentType("application/ms-excel");
        response.setCharacterEncoding("UTF-8");
        StringBuilder xlsBuilder = new StringBuilder();
        xlsBuilder.append("资金监控").append(capitalMonitorDTO.getQueryDate());
        String fileName = URLEncoder.encode(xlsBuilder.append(".xlsx").toString(), "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        EasyExcel.write(response.getOutputStream(), CapitalMonitorExcelVO.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet().doWrite(excelVOList);
    }

    /**
     * 结算报页面, 单个邮件发送中, 获取邮件通配符结果, 不包含追保金额
     * @param clientId 客户ID
     * @return 返回值
     */
    @Override
    public Map<String, String> getMailKeywordsConfig(Integer clientId) {
        Map<String, String> returnMap = new HashMap<>();
        ClientVO client = clientClient.getClientById(clientId);
        List<MailKeywordsConfigVO> mailKeywordsConfigVOList = sendMailClient.getMailKeywordsConfigLsit();
        if (!mailKeywordsConfigVOList.isEmpty()) {
            for (MailKeywordsConfigVO vo : mailKeywordsConfigVOList) {
                if ("客户名称".equals(vo.getKeyWord())){
                    if (client != null){
                        returnMap.put("客户名称",client.getName());
                    }
                } else {
                    returnMap.put(vo.getKeyWord(),"");
                }

            }
        }
        return returnMap;
    }

    /**
     * 获取邮件通配符对应结果(包含追保金额)
     * @param dto 入参
     * @return 返回map
     */
    @Override
    public Map<String, String> getAllMailKeywordsConfig(MailKeywordsConfigResultDto dto) {
        log.info("getAllMailKeywordsConfig获取邮件通配符结果入参="+ JSON.toJSONString(dto));
        Map<String, String> returnMap = new HashMap<>();
        ClientVO client = clientClient.getClientById(dto.getClientId());
        List<MailKeywordsConfigVO> mailKeywordsConfigVOList = sendMailClient.getMailKeywordsConfigLsit();
        String additionalPrice = ""; // 追保金额
        if (dto.getStartDate() != null) { // 结算报告中查询追保金额
            SettlementReportDTO settlementReportDTO = new SettlementReportDTO();
            settlementReportDTO.setClientId(dto.getClientId());
            settlementReportDTO.setStartDate(dto.getStartDate());
            settlementReportDTO.setEndDate(dto.getQueryDate());
            AccountOverviewVO accountOverviewVO = accountOverview(settlementReportDTO);
            if (accountOverviewVO != null && accountOverviewVO.getOccupyDesirable()!=null){
                BigDecimal additionalPriceTemp = accountOverviewVO.getOccupyDesirable().getAdditionalPrice();
                if (additionalPriceTemp!=null) {
                    additionalPrice = getBigDecimal2String(additionalPriceTemp);
                }
            }
        } else { // 资金记录
            ClientCapitalMonitorVO capitalMonitorVO = getCapitalMonitorByClientId(dto);
            log.info("获取通配符结果"+ JSON.toJSONString(capitalMonitorVO));
            if (capitalMonitorVO != null && capitalMonitorVO.getOccupyDesirable() != null){
                BigDecimal additionalPriceTemp = capitalMonitorVO.getOccupyDesirable().getAdditionalPrice();
                if (additionalPriceTemp!=null) {
                    additionalPrice = getBigDecimal2String(additionalPriceTemp);
                }
            }
        }
        if (!mailKeywordsConfigVOList.isEmpty()) {
            for (MailKeywordsConfigVO vo : mailKeywordsConfigVOList) {
                if ("客户名称".equals(vo.getKeyWord())){
                    if (client != null){
                        returnMap.put("客户名称",client.getName());
                    }
                } else if ("追保金额".equals(vo.getKeyWord())) {
                    if (StringUtils.isNotBlank(additionalPrice)){
                        returnMap.put("追保金额",additionalPrice);
                    } else{
                        returnMap.put("追保金额","0.00");
                    }
                }
            }
        }
        return returnMap;
    }
    /**
     * 四舍五入返回字符串
     * @param bigDecimalValue 入参
     * @return 返回字符串
     */
    public String getBigDecimal2String(BigDecimal bigDecimalValue){
        if (bigDecimalValue != null){
            String pattern = "###0.00";
            DecimalFormat decimalFormat = new DecimalFormat(pattern);
            return decimalFormat.format(bigDecimalValue);
        } else {
            return "";
        }
    }

    public ClientCapitalMonitorVO getCapitalMonitorByClientId(MailKeywordsConfigResultDto paramDto) {
        CapitalMonitorDTO dto = new CapitalMonitorDTO();
        dto.setClientIdList(Collections.singleton(paramDto.getClientId()));
        dto.setQueryDate(paramDto.getQueryDate());
        IPage<CapitalMonitorVO> ipage =  getCapitalMonitorListByPage(dto);
        if (CollectionUtil.isNotEmpty(ipage.getRecords())) {
            CapitalMonitorVO capitalMonitorVO = ipage.getRecords().get(0);
            ClientCapitalMonitorVO returnVO = new ClientCapitalMonitorVO();
            BeanUtils.copyProperties(capitalMonitorVO,returnVO);
            org.orient.otc.api.quote.vo.OccupyDesirable occupyDesirable = new org.orient.otc.api.quote.vo.OccupyDesirable();
            BeanUtils.copyProperties( capitalMonitorVO.getOccupyDesirable(),occupyDesirable);
            returnVO.setOccupyDesirable(occupyDesirable);
            return returnVO;
        }
        return null;
    }
}
