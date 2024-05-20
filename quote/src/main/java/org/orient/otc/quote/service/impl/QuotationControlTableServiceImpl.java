package org.orient.otc.quote.service.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.orient.otc.quote.entity.QuotationControlTable;
import org.orient.otc.quote.service.QuotationControlTableService;
import org.springframework.stereotype.Service;

/**
 * (QuotationControlTable)表服务实现类
 * @author makejava
 * @since 2023-07-10 13:50:47
 */
@Service("quotationControlTableService")
public class QuotationControlTableServiceImpl extends ServiceImpl<BaseMapper<QuotationControlTable>,QuotationControlTable> implements QuotationControlTableService {

}
