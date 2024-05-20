package org.orient.otc.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.orient.otc.api.dm.feign.CalendarClient;
import org.orient.otc.api.dm.feign.UnderlyingManagerClient;
import org.orient.otc.api.quote.dto.SettlementTradeObsDateDTO;
import org.orient.otc.api.quote.feign.SettlementClient;
import org.orient.otc.api.quote.feign.TradeMngClient;
import org.orient.otc.api.quote.feign.TradeObsDateClient;
import org.orient.otc.api.quote.vo.TradeMngVO;
import org.orient.otc.common.cache.enums.SystemConfigEnum;
import org.orient.otc.common.cache.adapter.RedisAdapter;
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
import org.orient.otc.system.enums.SuccessStatusEnum;
import org.orient.otc.system.exception.BussinessException;
import org.orient.otc.system.mapper.CloseDayDetailLogMapper;
import org.orient.otc.system.mapper.CloseDayLogMapper;
import org.orient.otc.system.mapper.SettlementDetailLogMapper;
import org.orient.otc.system.mapper.SettlementLogMapper;
import org.orient.otc.system.service.SystemCloseDayLogService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class SystemCloseDayLogServiceImpl implements SystemCloseDayLogService {

    @Resource
    private SettlementClient settlementClient;

    @Resource
    private CalendarClient calendarClient;

    @Resource
    private TradeMngClient tradeMngClient;

    @Resource
    private UnderlyingManagerClient underlyingManagerClient;

    @Resource
    private TradeObsDateClient tradeObsDateClient;

    @Resource
    private SettlementDetailLogMapper settlementDetailLogMapper;

    @Resource
    private CloseDayDetailLogMapper closeDayDetailLogMapper;

    @Resource
    private CloseDayLogMapper closeDayLogMapper;

    @Resource
    private SettlementLogMapper settlementLogMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;



    @Override
    public List<SettlementVO> settlement(SettlementDTO settlementDto) {

        //校验当前是否为交易日
        BussinessException.E_200107.assertTrue(calendarClient.isTradeDay(LocalDate.now()));
        //校验当前是否超过15点
        BussinessException.E_200108.assertTrue(LocalTime.now().isAfter(LocalTime.of(15, 0, 0)));
        //校验所有累计期权合约是否已经落观察价格
        BussinessException.E_200110.assertTrue(settlementClient.checkObsStatus(settlementDto));
        //校验当日是否存在到期未平仓数据
        List<TradeMngVO> needCloseList = settlementClient.getNeedClosedTradeList(settlementDto);
        BussinessException.E_200106.assertTrue(needCloseList.isEmpty()
                , needCloseList.stream().map(TradeMngVO::getTradeCode).collect(Collectors.toList()));
        List<SettlementVO> settlementVOList = new ArrayList<>();
        //校验雪球收盘价是否大于等于敲出价
        boolean isSuccess = true;

        SettlementTradeObsDateDTO snowOptionTypeSettlementTradeObsDateDTO = SettlementTradeObsDateDTO
                .builder()
                .settlementDate(settlementDto.getSettlementDate())
                .build();
        List<String> snowOptionTypeTradeCodeList = tradeObsDateClient.getNeedKnockOutTradeCodeList(snowOptionTypeSettlementTradeObsDateDTO);
        if (!snowOptionTypeTradeCodeList.isEmpty()) {
            String msg = String.join(",",snowOptionTypeTradeCodeList);
            BussinessException.E_200111.assertTrue(false, msg);
        }
        //校验场内持仓
        SettlementVO checkTodayPosResult = settlementClient.getCheckTodayPosResult();
        settlementVOList.add(checkTodayPosResult);
        saveSettlementLog("校验场内持仓", checkTodayPosResult);
        // 更新雪球敲入标识
        SettlementVO settlementVo = settlementClient.updateKnockedIn(settlementDto);
        settlementVOList.add(settlementVo);
        saveSettlementLog("更新雪球敲入标识", settlementVo);
        if (!settlementVo.getIsSuccess()) {
            isSuccess = false;
        }

        //更正场内补单持仓
        SettlementVO updateTodayPosDataResult  =   settlementClient.updateTodayPosData(settlementDto);
        settlementVOList.add(updateTodayPosDataResult);
        saveSettlementLog("更正场内补单持仓", updateTodayPosDataResult);
        if (!updateTodayPosDataResult.getIsSuccess()) {
            isSuccess = false;
        }
        //保存风险计算的内容到数据库
        SettlementVO saveTradeRiskInfoVo = settlementClient.saveTradeRiskInfo(settlementDto);
        settlementVOList.add(saveTradeRiskInfoVo);
        saveSettlementLog("风险界面快照落库", saveTradeRiskInfoVo);
        if (!saveTradeRiskInfoVo.getIsSuccess()) {
            isSuccess = false;
        }

        //复制波动率
        SettlementVO copyVolatility = settlementClient.saveVolToTradeDay(settlementDto);
        settlementVOList.add(copyVolatility);
        saveSettlementLog("最新波动率保存落库", copyVolatility);
        if (!copyVolatility.getIsSuccess()) {
            isSuccess = false;
        }

        //更新log
        SettlementLog settlementLog = settlementLogMapper.selectOne(new LambdaQueryWrapper<SettlementLog>().eq(SettlementLog::getIsDeleted, 0).eq(SettlementLog::getSettlementDate, LocalDate.now()));
        settlementLog.setSuccessStatus(isSuccess ? SuccessStatusEnum.success : SuccessStatusEnum.faild);
        settlementLog.setMessage(isSuccess ? "成功" : "失败");
        settlementLogMapper.updateById(settlementLog);
        return settlementVOList;
    }
    @Override
    public List<SettlementVO> closeDate() {

        List<SettlementVO> settlementVOList = new ArrayList<>();
        //切日初始化持仓
        SettlementVO initPosDataVo = settlementClient.copyPosDataToNextTradeDay();
        settlementVOList.add(initPosDataVo);
        saveCloseDayLog("场内持仓初始化",initPosDataVo);
        SettlementVO settlementVo = underlyingManagerClient.updateUnderlyingState();
        saveCloseDayLog("更新合约状态",settlementVo);

        //切换交易日
        SettlementVO gotoNextTradeDay = calendarClient.gotoNextTradeDay();
        saveCloseDayLog("切换交易日",gotoNextTradeDay);
        //计算合约上市以来的所有已平仓的累计盈亏
        SettlementVO saveClosedTradeByUnderlying = settlementClient.saveCloseTradeTotalPnl();
        settlementVOList.add(saveClosedTradeByUnderlying);
        saveCloseDayLog("计算并保存已平仓的累计盈亏", saveClosedTradeByUnderlying);
        //清空计算所需数据
        this.clearRedisData();
        //更新log
        CloseDayLog closeDayLog = closeDayLogMapper.selectOne(new LambdaQueryWrapper<CloseDayLog>()
                .eq(CloseDayLog::getIsDeleted, 0)
                .eq(CloseDayLog::getCloseDayDate, LocalDate.now()));
        closeDayLog.setSuccessStatus(SuccessStatusEnum.success);
        closeDayLog.setMessage("成功");
        closeDayLogMapper.updateById(closeDayLog);

        return settlementVOList;
    }

    @Override
    public List<SettlementDetailLog> getTodaySettlementLog() {
        return settlementDetailLogMapper.selectList(new LambdaQueryWrapper<SettlementDetailLog>().eq(SettlementDetailLog::getSettlementDate, LocalDate.now()).eq(SettlementDetailLog::getIsDeleted, 0));
    }

    @Override
    public List<CloseDayDetailLog> getCloseDayLog() {
        return closeDayDetailLogMapper.selectList(new LambdaQueryWrapper<CloseDayDetailLog>().eq(CloseDayDetailLog::getCloseDayDate, LocalDate.now()).eq(CloseDayDetailLog::getIsDeleted, 0));

    }

    @Override
    public Boolean initLog() {
        CloseDayLog closeDayLog = new CloseDayLog();
        closeDayLog.setCloseDayDate(LocalDate.now());
        closeDayLog.setSuccessStatus(SuccessStatusEnum.unexecuted);
        closeDayLog.setMessage("未执行");
        closeDayLogMapper.insert(closeDayLog);
        initCloseDayLog("场内持仓初始化");
        initCloseDayLog("更新合约状态");
        initCloseDayLog("切换交易日");
        initSettlementLog("计算并保存已平仓的累计盈亏");


        SettlementLog settlementLog = new SettlementLog();
        settlementLog.setSettlementDate(LocalDate.now());
        settlementLog.setSuccessStatus(SuccessStatusEnum.unexecuted);
        settlementLog.setMessage("未执行");
        settlementLogMapper.insert(settlementLog);
        initSettlementLog("校验场内持仓");
        initSettlementLog("更新雪球敲入标识");
        initSettlementLog("更正场内补单持仓");
        initSettlementLog("风险界面快照落库");
        initSettlementLog("最新波动率保存落库");


        return Boolean.TRUE;
    }

    @Override
    public Page<CloseDayLog> getCloseDateLogByPage(CloseDayLogPageDto closeDayLogPageDto) {
        Page<CloseDayLog> page = new Page<>();
        page.setCurrent(closeDayLogPageDto.getPageNo());
        page.setSize(closeDayLogPageDto.getPageSize());
        return closeDayLogMapper.selectPage(page, new LambdaQueryWrapper<CloseDayLog>().orderByDesc(CloseDayLog::getCloseDayDate));
    }

    @Override
    public Page<SettlementLog> getSettlementLogByPage(SettlementLogPageDto settlementLogPageDto) {
        Page<SettlementLog> page = new Page<>();
        page.setCurrent(settlementLogPageDto.getPageNo());
        page.setSize(settlementLogPageDto.getPageSize());
        return settlementLogMapper.selectPage(page, new LambdaQueryWrapper<SettlementLog>().orderByDesc(SettlementLog::getSettlementDate));
    }

    @Override
    public List<CloseDayDetailLog> getCloseDateLogDetailByDate(CloseDayLogDate closeDayLogDate) {
        return closeDayDetailLogMapper.selectList(new LambdaQueryWrapper<CloseDayDetailLog>()
                .eq(CloseDayDetailLog::getCloseDayDate, closeDayLogDate.getDate())
                .eq(CloseDayDetailLog::getIsDeleted, 0));
    }

    @Override
    public List<SettlementDetailLog> getSettlementLogDetailByDate(SettlementLogDate settlementLogDate) {
        return settlementDetailLogMapper.selectList(new LambdaQueryWrapper<SettlementDetailLog>()
                .eq(SettlementDetailLog::getSettlementDate, settlementLogDate.getDate())
                .eq(SettlementDetailLog::getIsDeleted, 0));
    }

    @Override
    public SettlementVO clearRedisData() {
        //今日开平仓数据清空
        String today = LocalDate.parse(Objects.requireNonNull(stringRedisTemplate.opsForHash().get(RedisAdapter.SYSTEM_CONFIG_INFO
                        , SystemConfigEnum.lastTradeDay.name())).toString())
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        //开平仓金额与昨日估值
        stringRedisTemplate.expire(RedisAdapter.TODAY_OPEN_TRADE_AMOUNT+today, 7, TimeUnit.DAYS);
        stringRedisTemplate.expire(RedisAdapter.TODAY_CLOSE_TRADE_AMOUNT+today, 7, TimeUnit.DAYS);
        stringRedisTemplate.expire(RedisAdapter.TRADE_LAST_RISK_INFO+today,7,TimeUnit.DAYS);
        //delta调整值
        stringRedisTemplate.delete(RedisAdapter.DELTA_ADJUSTMENT);
        //清空计算数据
        SettlementVO settlementVo = new SettlementVO();
        settlementVo.setIsSuccess(Boolean.TRUE);
        settlementVo.setMsg("清空成功");
        return settlementVo;
    }

    private void initCloseDayLog(String taskName) {
        CloseDayDetailLog closeDayDetailLog = new CloseDayDetailLog();
        closeDayDetailLog.setTaskName(taskName);
        closeDayDetailLog.setCloseDayDate(LocalDate.now());
        closeDayDetailLog.setSuccessStatus(SuccessStatusEnum.unexecuted);
        closeDayDetailLog.setMessage("未执行");
        closeDayDetailLogMapper.insert(closeDayDetailLog);
    }


    private void saveCloseDayLog(String taskName,SettlementVO settlementVo) {
        CloseDayDetailLog closeDayDetailLog = new CloseDayDetailLog();
        closeDayDetailLog.setTaskName(taskName);
        closeDayDetailLog.setCloseDayDate(LocalDate.now());
        closeDayDetailLog.setSuccessStatus(settlementVo.getIsSuccess() ? SuccessStatusEnum.success : SuccessStatusEnum.faild);
        closeDayDetailLog.setMessage(settlementVo.getMsg());
        closeDayDetailLogMapper.update(closeDayDetailLog, new LambdaQueryWrapper<CloseDayDetailLog>()
                .eq(CloseDayDetailLog::getCloseDayDate, LocalDate.now())
                .eq(CloseDayDetailLog::getTaskName, taskName)
                .eq(CloseDayDetailLog::getIsDeleted, 0));
    }

    private void initSettlementLog(String taskName) {
        SettlementDetailLog settlementDetailLog = new SettlementDetailLog();
        settlementDetailLog.setTaskName(taskName);
        settlementDetailLog.setSettlementDate(LocalDate.now());
        settlementDetailLog.setSuccessStatus(SuccessStatusEnum.unexecuted);
        settlementDetailLog.setMessage("未执行");
        settlementDetailLogMapper.insert(settlementDetailLog);
    }

    private void saveSettlementLog(String taskName, SettlementVO settlementVo) {
        SettlementDetailLog settlementDetailLog = new SettlementDetailLog();
        settlementDetailLog.setTaskName(taskName);
        settlementDetailLog.setSettlementDate(LocalDate.now());
        settlementDetailLog.setSuccessStatus(settlementVo.getIsSuccess() ? SuccessStatusEnum.success : SuccessStatusEnum.faild);
        settlementDetailLog.setMessage(settlementVo.getMsg());
        settlementDetailLogMapper.update(settlementDetailLog, new LambdaQueryWrapper<SettlementDetailLog>()
                .eq(SettlementDetailLog::getSettlementDate, LocalDate.now())
                .eq(SettlementDetailLog::getTaskName, taskName)
                .eq(SettlementDetailLog::getIsDeleted, 0));
    }

}
