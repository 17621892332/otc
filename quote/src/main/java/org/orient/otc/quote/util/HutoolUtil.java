package org.orient.otc.quote.util;

import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.BigExcelWriter;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class HutoolUtil {

    public static void export(List dataList, String fileName ,String sheetName , Class clazz, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BigExcelWriter writer = DefineBigExcelWriter.getBigWriter(dataList.size(),clazz,new HashMap<>());
        //ExcelUtil.getBigWriter();
        writer.renameSheet(0,sheetName);
        writer.setOnlyAlias(true);
        writer.write(dataList, true);
        writer.autoSizeColumnAll();
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName,"UTF-8") + ".xlsx");
        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            writer.flush(out, true);
        } catch (IOException e) {
            log.error(JSON.toJSONString(e.getMessage()));
        } finally {
            // 关闭writer，释放内存
            writer.close();
        }
        //此处记得关闭输出Servlet流
        IoUtil.close(out);
    }

    /**
     * 部分列不自适应列宽
     * @param dataList  数据
     * @param fileName  文件名称
     * @param sheetName sheet名称
     * @param clazz     导出类class
     * @param request   request
     * @param response  response
     * @param columnWidthMap   不需要自适应列宽的列下标和宽度
     * @throws Exception
     */
    public static void export(List dataList, String fileName , String sheetName , Class clazz, HttpServletRequest request, HttpServletResponse response, Map<Integer,Integer> columnWidthMap) throws Exception {
        BigExcelWriter writer = DefineBigExcelWriter.getBigWriter(dataList.size(),clazz,columnWidthMap);
        //ExcelUtil.getBigWriter();
        writer.renameSheet(0,sheetName);
        writer.setOnlyAlias(true);
        writer.write(dataList, true);
        writer.autoSizeColumnAll();
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName,"UTF-8") + ".xlsx");
        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            writer.flush(out, true);
        } catch (IOException e) {
            log.error(JSON.toJSONString(e.getMessage()));
        } finally {
            // 关闭writer，释放内存
            writer.close();
        }
        //此处记得关闭输出Servlet流
        IoUtil.close(out);
    }
}
