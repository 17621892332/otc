package org.orient.otc.quote.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.orient.otc.quote.dto.daily.transdetail.StatusConvertDto;
import org.orient.otc.quote.dto.daily.transdetail.TransDetailPageDto;
import org.orient.otc.quote.entity.TransDetail;
import org.orient.otc.quote.vo.transdetail.TransDetailListVO;
import org.orient.otc.quote.vo.transdetail.TransDetailVO;

/**
 * 风险服务
 * @author chengqiang
 */
public interface TransDetailService extends IService<TransDetail> {
    /**
     * 从 transDetailClient 获取交易细节数据，剔除已存在于数据库中的数据，然后将剩余数据保存到数据库中。
     * @return 是否保存成功的布尔值
     */
    String getTransDetail();
    /**
     * 根据传入的 TransDetailPageDto 对象中的条件，查询数据库中的 TransDetail 数据，并返回分页结果。
     * @param dto TransDetailPageDto 对象，包含了查询条件和分页信息
     * @return 查询结果的分页视图对象
     */
    IPage<TransDetailListVO> getListByPage(TransDetailPageDto dto);
    /**
     * 根据传入的数据传输对象中的原始 ID 执行状态转换操作，并更新相应的交易详情记录。
     *
     * @param dto 包含原始 ID 的数据传输对象
     * @return 如果状态转换成功且更新操作成功，则返回 true；否则返回 false
     */
    String statusConvertY(StatusConvertDto dto);
    /**
     * 根据传入的数据传输对象中的原始 ID 执行状态转换操作，并更新相应的交易详情记录。
     *
     * @param dto 包含原始 ID 的数据传输对象
     * @return 如果状态转换成功且更新操作成功，则返回 true；否则返回 false
     */
    String statusConvertN(StatusConvertDto dto);
    /**
     * 根据传入的数据传输对象中的原始 ID 查询数据库中对应的交易详情，并将其转换为交易详情值对象。
     *
     * @param dto 包含原始 ID 的数据传输对象
     * @return 包含查询到的交易详情信息的交易详情值对象，如果未找到对应记录则返回空对象
     */
    TransDetailVO getDetail(StatusConvertDto dto);
}
