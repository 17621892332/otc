package org.orient.otc.quote.handler;

import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.style.column.AbstractColumnWidthStyleStrategy;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelWidthStyleStrategy extends AbstractColumnWidthStyleStrategy {

    // 单元格的最大宽度
    private static final int MAX_COLUMN_WIDTH = 50;
    // 缓存（第一个Map的键是sheet的index, 第二个Map的键是列的index, 值是数据长度）
    private  Map<Integer, Map<Integer, Integer>> CACHE = new HashMap<>(8);
    //需要设置自动设置列宽的列
    @Setter
    private List<Integer> needSetWidthColumn;

    // 重写设置列宽的方法
    @Override
    protected void setColumnWidth(WriteSheetHolder writeSheetHolder,
                                  List<WriteCellData<?>> cellDataList,
                                  Cell cell,
                                  Head head,
                                  Integer relativeRowIndex,
                                  Boolean isHead) {
        boolean needSetWidth = isHead || !CollectionUtils.isEmpty(cellDataList);
        // 当时表头或者单元格数据列表有数据时才进行处理
        if (needSetWidth && (needSetWidthColumn.contains(cell.getColumnIndex()) || needSetWidthColumn.isEmpty())) {
            Map<Integer, Integer> maxColumnWidthMap = CACHE.computeIfAbsent(writeSheetHolder.getSheetNo(), k -> new HashMap<>(16));

            // 获取数据长度
            Integer columnWidth = this.dataLength(cellDataList, cell, isHead);
            if (columnWidth >= 0) {
                if (columnWidth > MAX_COLUMN_WIDTH) {
                    columnWidth = MAX_COLUMN_WIDTH;
                }
                // 确保一个列的列宽以表头为主，如果表头已经设置了列宽，单元格将会跟随表头的列宽
                Integer maxColumnWidth = maxColumnWidthMap.get(cell.getColumnIndex());

                if (maxColumnWidth == null || columnWidth > maxColumnWidth) {
                    maxColumnWidthMap.put(cell.getColumnIndex(), columnWidth);
                    // 如果使用EasyExcel默认表头，那么使用columnWidth * 512
                    // 如果不使用EasyExcel默认表头，那么使用columnWidth * 256
                    // 如果是自己定义的字体大小，可以再去测试这个参数常量
                    writeSheetHolder
                            .getSheet()
                            .setColumnWidth(cell.getColumnIndex(), columnWidth * 256);
                }

            }
        }
    }

    /**
     * 获取当前单元格的数据长度
     */
    private Integer dataLength(List<WriteCellData<?>> cellDataList,
                               Cell cell,
                               Boolean isHead) {
        if (isHead) {
            return cell.getStringCellValue().getBytes().length;
        } else {
            WriteCellData<?> cellData = cellDataList.get(0);
            CellDataTypeEnum type = cellData.getType();
            if (type == null) {
                return -1;
            } else {
                switch (type) {
                    case STRING:
                        return cellData.getStringValue().getBytes().length;
                    case BOOLEAN:
                        return cellData.getBooleanValue().toString().getBytes().length;
                    case NUMBER:
                        return cellData.getNumberValue().toString().getBytes().length+2;
                    default:
                        return -1;
                }
            }
        }
    }

}
