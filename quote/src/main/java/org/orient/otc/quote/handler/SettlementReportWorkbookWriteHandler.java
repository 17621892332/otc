package org.orient.otc.quote.handler;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.excel.write.handler.WorkbookWriteHandler;
import com.alibaba.excel.write.handler.context.WorkbookWriteHandlerContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.orient.otc.quote.enums.SettlemenReportSheetEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * 自定义拦截器.对第一列第一行和第二行的数据新增下拉框，显示 测试1 测试2
 * @author Jiaju Zhuang
 */
@Slf4j
@AllArgsConstructor
public class SettlementReportWorkbookWriteHandler implements WorkbookWriteHandler {

    Set<SettlemenReportSheetEnum> sheetEnumSet;

    @Override
    public void afterWorkbookDispose(WorkbookWriteHandlerContext context) {
        Workbook workbook = context.getWriteWorkbookHolder().getWorkbook();
        workbook.setActiveSheet(0);
        //移除不存在的导出选项
        List<String> removeList = new ArrayList<>();
        int sheetSize = workbook.getNumberOfSheets();
        for (int i = 0; i < sheetSize; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (!sheetEnumSet.contains(SettlemenReportSheetEnum.getEnumByDesc(sheet.getSheetName()))) {
                removeList.add(sheet.getSheetName());
            }
        }
        if (CollectionUtil.isNotEmpty(removeList)) {
            removeList.forEach(e -> {
                int sheetIndex = workbook.getSheetIndex(e);
                workbook.removeSheetAt(sheetIndex);
            });
        }
    }
}
