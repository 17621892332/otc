package org.orient.otc.quote.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.orient.otc.api.quote.dto.CapitalSyncDTO;
import org.orient.otc.common.database.config.IServicePlus;
import org.orient.otc.quote.dto.capitalrecords.*;
import org.orient.otc.quote.dto.settlementReport.SettlementReportDTO;
import org.orient.otc.quote.entity.CapitalRecords;
import org.orient.otc.quote.vo.CapitalRecordsVO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 资金记录服务
 */
public interface CapitalRecordsService extends IServicePlus<CapitalRecords> {

    /**
     * 获取区间内的资金记录列表(包含开始日期和结束日期)
     * @param clientIdList 客户ID
     * @param endDate 结束日期
     * @return 资金记录列表
     */
    List<CapitalRecords> getListByVestingDate(Set<Integer> clientIdList, LocalDate endDate);
    /**
     * 获取某个日期之前的资金记录汇总
     * @param clientIdList 客户ID
     * @param endDate 结束日期
     * @return key 客户ID value 汇总金额
     */
    Map<Integer,BigDecimal> getMapByVestingDate(List<Integer> clientIdList, LocalDate endDate);

    /**
     * 分页查询资金记录
     * @param dto 查询条件
     * @return 查询结果
     */
    IPage<CapitalRecordsVO> getListByPage(CapitalRecordsPageDto dto);
    /**
     * 分页查询资金记录
     * @param settlementReportDTO 查询条件
     * @return 查询结果
     */
    IPage<CapitalRecordsVO> getListByClientPage(SettlementReportDTO settlementReportDTO);

    /**
     * 分页查询资金记录
     * @param settlementReportDTO 查询条件
     * @return 查询结果
     */
    List<CapitalRecordsVO> getListByClient(SettlementReportDTO settlementReportDTO);
    /**
     * @param dto 更新状态
     * @return 保存结果
     */
    String delete(CapitalRecordsDeleteDto dto);
    /**
     * @param dto 更新状态
     * @return 保存结果
     */
    String updateCapitalStatus(CapitalRecordsUpdateCapitalStatusDto dto);

    @Transactional
    Integer addConfirm(CapitalRecordsAddDto dto);

    /**
     * @param dto 资金记录内容
     * @return 保存结果
     */
    Integer add(CapitalRecordsAddDto dto);

    /**
     * @param dto 备注信息
     * @return 保存结果
     */
    String addRemark(CapitalRecordsUpdateRemarkDto dto);

    void export(CapitalRecordsExportDto dto, HttpServletRequest request, HttpServletResponse response) throws Exception;

    String importCapital(MultipartFile file) throws IOException;

    /**
     * 同步资金记录更新状态
     * @param dto 更新参数
     * @return 是否更新成功
     */
    Boolean capitalUpdateSync(CapitalSyncDTO dto);
}
