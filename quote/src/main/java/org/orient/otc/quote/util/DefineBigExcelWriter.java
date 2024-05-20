package org.orient.otc.quote.util;

import cn.hutool.core.annotation.Alias;
import cn.hutool.core.exceptions.DependencyException;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.poi.excel.BigExcelWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;

/**
 * DefineBigExcelWriter 自定义BigExcelWriter
 */
@Component
@Slf4j
public class DefineBigExcelWriter extends BigExcelWriter {
    // 最大缓存行数
    static int rowAccessWindowSize = 100;
    // 总数据量
    static int dataCount = 0;
    /*
    导出的class, 用以计算列宽autoSizeColumnAll方法中的数据的某些列全为空
    导出的字段必须用Alias修饰, 宽度不足的可以用空格代替
     */
    static Class exportClazz;
    // 不需要自适应列宽的列 key=列下标 , value=列宽(默认20个字符)
    static Map<Integer,Integer> columnWidthMap;

    public DefineBigExcelWriter() {
    }
    public DefineBigExcelWriter(int rowAccessWindowSize) {
         super(rowAccessWindowSize);
    }

    /**
     *  获取DefineBigExcelWriter 对象
     * @param dataSize 数据量
     * @param columnWidthMapParam 不需要自适应列宽的列集合
     * @return 返回对象
     */
    public static DefineBigExcelWriter getBigWriter(int dataSize,Class clazz,Map<Integer,Integer> columnWidthMapParam) {
        try {
            columnWidthMap = columnWidthMapParam;
            exportClazz = clazz;
            dataCount = dataSize; // 赋值总数据量
            if (dataSize>100){
                while (dataSize%rowAccessWindowSize<99 && rowAccessWindowSize<150){ // 找到一个被除数, 数据长度对他取余之后的数量不小于99条,用来计算列宽
                    rowAccessWindowSize++;
                }
            }
            log.info("rowAccessWindowSize="+rowAccessWindowSize+"---最后依次缓存的数量="+dataSize%rowAccessWindowSize);
            return new DefineBigExcelWriter(rowAccessWindowSize);
        } catch (NoClassDefFoundError var1) {
            throw new DependencyException((Throwable) ObjectUtil.defaultIfNull(var1.getCause(), var1), "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
        }
    }

    /**
     * 重写自适应列宽
     * @return
     */
    @Override
    public BigExcelWriter autoSizeColumnAll() {
        Map<Integer,Integer> firstRowMap = getFieldLength();
        final SXSSFSheet sheet = (SXSSFSheet)this.sheet;
        sheet.trackAllColumnsForAutoSizing();
        super.autoSizeColumnAll();
        // 取最后一行
        SXSSFRow lastRow = sheet.getRow(sheet.getLastRowNum());
        // 获取列数
        int columnCount = lastRow.getPhysicalNumberOfCells();
        int minCellWith = 10*256; // 最小宽度为10个字符
        int maxRowIndex = dataCount>rowAccessWindowSize?(dataCount-rowAccessWindowSize+1):(dataCount-1);
        // 循环每一列
        for (int columnIndex = 0; columnIndex <columnCount; columnIndex++) {
            if (columnWidthMap.containsKey(columnIndex)){
                Integer width = columnWidthMap.get(columnIndex);
                // 如果没设置宽度
                if (width==null) {
                    width = 20;
                }
                sheet.setColumnWidth(columnIndex, width*256);
                continue;
            }
            // 记录当前循环列的最大列宽
            int maxCellWith = 0;
            // 循环内存缓存的行
            for (int rowIndex=maxRowIndex;rowIndex<dataCount;rowIndex++){
                // 取对应行
                SXSSFRow row = sheet.getRow(rowIndex);
                if (row==null){
                    continue;
                }
                if (row.getCell(columnIndex) == null){
                    continue;
                }
                // 列宽(字符数量)
                int cellWith = 0;
                // 单元格类型
                CellType cellType =  row.getCell(columnIndex).getCellType();
                SXSSFCell cell = row.getCell(columnIndex);
                switch (cellType) {
                    case STRING:
                        cellWith = cell.getStringCellValue().getBytes().length+2;
                        break;
                    case NUMERIC:
                        //cellWith = sheet.getColumnWidth(i);
                        cellWith = (cell.getNumericCellValue()+"").getBytes().length+4;
                        break;
                    case BOOLEAN:
                        cellWith = "true".getBytes().length;
                        break;
                    default:
                        cellWith = 12;
                        break;
                }
                // excel 单元格默认宽度8.0(最多容纳255个字符的长度)
                cellWith = cellWith>255?254:cellWith;
                cellWith = cellWith*256;
                maxCellWith = cellWith>maxCellWith?cellWith:maxCellWith;

            }
            maxCellWith = maxCellWith<minCellWith?minCellWith:maxCellWith;
            if (firstRowMap.containsKey(columnIndex)){
                int cellWithTemp = firstRowMap.get(columnIndex);
                maxCellWith = cellWithTemp>maxCellWith?cellWithTemp:maxCellWith;
            }
            // 解决自动设置列宽中文失效的问题
            sheet.setColumnWidth(columnIndex, maxCellWith);
        }
        sheet.untrackAllColumnsForAutoSizing();
        return this;
    }


    /**
     * 根据导出的vo中需要导出的字段上标有@Alias注解,后面计算表头单元格宽度使用(第一行列宽)
     * @return 返回map
     */
    public static Map<Integer,Integer> getFieldLength() {
        Field[] fields = exportClazz.getDeclaredFields();
        // key列下表 , value是宽度
        Map<Integer,Integer> resultMap = new HashMap<>();
        for (int columnIndex=0;columnIndex<fields.length;columnIndex++){
            Field field = fields[columnIndex];
            boolean bool = field.isAnnotationPresent(Alias.class);
            if (bool) {
                String value = field.getAnnotation(Alias.class).value();
                int cellWith = value.getBytes().length;
                cellWith = cellWith>255?254:cellWith;
                cellWith = cellWith*256;
                resultMap.put(columnIndex, cellWith);
            }
        }
        return resultMap;
    }
}
