package org.orient.otc.quote.util;


import com.yongjiu.commons.utils.XmlReader;
import com.yongjiu.dto.freemarker.input.ExcelImageInput;
import com.yongjiu.dto.freemarker.input.FreemarkerInput;
import com.yongjiu.entity.excel.*;
import com.yongjiu.entity.excel.Style.Border;
import com.yongjiu.util.ColorUtil;
import com.yongjiu.entity.excel.Cell;
import com.yongjiu.entity.excel.Row;
import com.yongjiu.entity.excel.Table;
import org.apache.poi.ss.usermodel.Comment;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.*;
import org.orient.otc.quote.vo.CapitalRecordsVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * POI 将ftl(xml)模板转为excel
 */
@Component
public class ExcelTemplateExportUtil {
    private final Logger log = LoggerFactory.getLogger(ExcelTemplateExportUtil.class);
    @Autowired
    HttpServletResponse response;

    public ExcelTemplateExportUtil() {
    }

    /**
     * 导出
     * @param dataList 导出的数据
     */
    public void capitalExport(List<CapitalRecordsVO> dataList) {
        Map<String, List<CapitalRecordsVO>> dataMap = new HashMap();
        dataMap.put("capitalRecords", dataList);
        FreemarkerInput freemarkerInput = new FreemarkerInput();
        freemarkerInput.setTemplateFilePath("/template/capital/");
        freemarkerInput.setTemplateName("capitalExport.ftl");
        freemarkerInput.setXmlTempFile(System.getProperty("java.io.tmpdir"));
        freemarkerInput.setDataMap(dataMap);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String exportFileName = format.format(new Date());
        freemarkerInput.setFileName("资金记录_" + exportFileName);
        //exportExcelToStream(freemarkerInput,exportFileName);
        exportClientExcel(response, freemarkerInput);
    }

    /**
     * 获取标题和内容中最大的宽度
     */
    public int getMaxWidth(int cellWidth, int titleWidth) {
        int defaultWidth = 2150;
        int maxWidth = 255 * 256;
        if (cellWidth > maxWidth || titleWidth > maxWidth) {
            return defaultWidth * 2;
        } else {
            if (cellWidth < titleWidth) {
                return titleWidth;
            } else {
                return cellWidth;
            }
        }
    }

    /**
     * 计算table中每一列的最大宽度
     */
    public Map<Integer, Integer> tableWidth(List<Row> rows, String sheetName) {
        // 默认宽度
        int defaultWidth = 2150;
        // 最大宽度
        int maxWidth = 255 * 256;
        // key= 列下标 , value = 宽度
        Map<Integer, Integer> returnMap = new HashMap<>();
        for (int rowIndex = 1; rowIndex < rows.size(); rowIndex++) {
            Row row = rows.get(rowIndex);
            List<Cell> cellList = row.getCells();
            if (row.getCells() != null) {
                for (int clomunIndex = 0; clomunIndex < cellList.size(); clomunIndex++) {
                    Cell cell = row.getCells().get(clomunIndex);
                    if (cell.getData() != null && cell.getData().getText() != null) {
                        int temp = cell.getData().getText().getBytes().length * 260;
                        if (temp > maxWidth) {
                            temp = defaultWidth * 2;
                        }
                        if (returnMap.containsKey(clomunIndex)) {
                            Integer width = returnMap.get(clomunIndex);
                            if (width < temp) { // 存放列宽的最大值
                                returnMap.put(clomunIndex, temp);
                            }
                        } else {
                            returnMap.put(clomunIndex, temp);
                        }
                    }
                }
            }
        }
        return returnMap;
    }

    public void exportToFile(Map dataMap, String templateName, String templateFilePath, String fileFullPath) {
        try {
            File file = new File(fileFullPath);
            FileUtils.forceMkdirParent(file);
            FileOutputStream outputStream = new FileOutputStream(file);
            exportToStream(dataMap, templateName, templateFilePath, outputStream);
        } catch (Exception var6) {
            var6.printStackTrace();
        }

    }

    public void exportToStream(Map dataMap, String templateName, String templateFilePath, FileOutputStream outputStream) {
        try {
            Template template = getTemplate(templateName, templateFilePath);
            OutputStreamWriter outputWriter = new OutputStreamWriter(outputStream, "UTF-8");
            Writer writer = new BufferedWriter(outputWriter);
            template.process(dataMap, writer);
            writer.flush();
            writer.close();
            outputStream.close();
        } catch (Exception var7) {
            var7.printStackTrace();
        }

    }

    public void exportExcelToStream(FreemarkerInput freemarkerInput, String fileName) {
        try {
            OutputStream outputStream = response.getOutputStream();
            response.reset();
            response.setContentType("application/msexcel;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=\"" + new String((fileName + ".xlsx").getBytes("GBK"), "ISO8859-1") + "\"");
            response.setHeader("Response-Type", "Download");
            createExcelToStream(freemarkerInput, outputStream);
            FileUtils.forceDelete(new File(freemarkerInput.getXmlTempFile() + freemarkerInput.getFileName() + ".xml"));
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }


    public void exportClientExcel(HttpServletResponse response, FreemarkerInput freemarkerInput) {
        try {
            String encodedFilename = URLEncoder.encode((freemarkerInput.getFileName() + ".xlsx"), "UTF-8");
            OutputStream outputStream = response.getOutputStream();
            response.reset();
            response.setContentType("application/msexcel;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=\"" + encodedFilename + "\"");
            response.setHeader("Response-Type", "Download");
            createExcelToStream(freemarkerInput, outputStream);
            FileUtils.forceDelete(new File(freemarkerInput.getXmlTempFile() + freemarkerInput.getFileName() + ".xml"));
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    /**
     * 导出客户模板
     */
    public void exportClientExcel(HttpServletResponse response, String outFile, FreemarkerInput freemarkerInput) {
        try {
            FileOutputStream fos = new FileOutputStream(outFile);
            OutputStream outputStream = fos;
            response.reset();
            response.setContentType("application/msexcel;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=\"" + new String((freemarkerInput.getFileName() + ".xlsx").getBytes("GBK"), "ISO8859-1") + "\"");
            response.setHeader("Response-Type", "Download");
            createExcelToStream(freemarkerInput, outputStream);
            FileUtils.forceDelete(new File(freemarkerInput.getXmlTempFile() + freemarkerInput.getFileName() + ".xml"));
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public void exportImageExcel(HttpServletResponse response, FreemarkerInput freemarkerInput) {
        try {
            OutputStream outputStream = response.getOutputStream();
            response.reset();
            response.setContentType("application/msexcel;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=\"" + new String((freemarkerInput.getFileName() + ".xls").getBytes("GBK"), "ISO8859-1") + "\"");
            response.setHeader("Response-Type", "Download");
            createImageExcleToStream(freemarkerInput, outputStream);
            FileUtils.forceDelete(new File(freemarkerInput.getXmlTempFile() + freemarkerInput.getFileName() + ".xml"));
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    private Template getTemplate(String templateName, String filePath) throws IOException {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_28);
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateUpdateDelayMilliseconds(0L);
        configuration.setEncoding(Locale.CHINA, "UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        File templateDir = new File(filePath);
        if (templateDir.exists()){ // 模板不在resource目录中
            configuration.setDirectoryForTemplateLoading(templateDir);
        } else { // 模板在resource目录中
            configuration.setClassForTemplateLoading(ExcelTemplateExportUtil.class, filePath);
        }
        configuration.setOutputEncoding("UTF-8");
        return configuration.getTemplate(templateName, "UTF-8");
    }

    private void createImageExcleToStream(FreemarkerInput freemarkerInput, OutputStream outputStream) {
        BufferedWriter out = null;

        try {
            Template template = getTemplate(freemarkerInput.getTemplateName(), freemarkerInput.getTemplateFilePath());
            File tempXMLFile = new File(freemarkerInput.getXmlTempFile() + freemarkerInput.getFileName() + ".xml");
            FileUtils.forceMkdirParent(tempXMLFile);
            out = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(tempXMLFile.toPath()), "UTF-8"));
            template.process(freemarkerInput.getDataMap(), out);
            if (log.isDebugEnabled()) {
                log.info("1.完成将文本数据导入到XML文件中");
            }

            SAXReader reader = new SAXReader();
            Document document = reader.read(tempXMLFile);
            Map<String, Style> styleMap = readXmlStyle(document);
            log.info("2.完成解析XML中样式信息");
            List<Worksheet> worksheets = readXmlWorksheet(document);
            if (log.isDebugEnabled()) {
                log.info("3.开始将XML信息写入Excel，数据为：" + worksheets.toString());
            }

            HSSFWorkbook wb = new HSSFWorkbook();
            for (Worksheet worksheet : worksheets) {
                HSSFSheet sheet = wb.createSheet(worksheet.getName());
                Table table = worksheet.getTable();
                List<Row> rows = table.getRows();
                List<Column> columns = table.getColumns();
                int createRowIndex;
                if (columns != null && !columns.isEmpty()) {
                    createRowIndex = 0;
                    for (int i = 0; i < columns.size(); ++i) {
                        Column column = columns.get(i);
                        createRowIndex = getCellWidthIndex(createRowIndex, i, column.getIndex());
                        sheet.setColumnWidth(createRowIndex, (int) column.getWidth() * 50);
                    }
                }

                createRowIndex = 0;
                List<CellRangeAddressEntity> cellRangeAddresses = new ArrayList<>();

                for (int rowIndex = 0; rowIndex < rows.size(); ++rowIndex) {
                    Row rowInfo = (Row) rows.get(rowIndex);
                    if (rowInfo != null) {
                        createRowIndex = getIndex(createRowIndex, rowIndex, rowInfo.getIndex());
                        HSSFRow row = sheet.createRow(createRowIndex);
                        if (rowInfo.getHeight() != null) {
                            int height = rowInfo.getHeight() * 20;
                            row.setHeight((short) height);
                        }

                        List<Cell> cells = rowInfo.getCells();
                        if (!CollectionUtils.isEmpty(cells)) {
                            int startIndex = 0;

                            for (int cellIndex = 0; cellIndex < cells.size(); ++cellIndex) {
                                Cell cellInfo = cells.get(cellIndex);
                                if (cellInfo != null) {
                                    startIndex = getIndex(startIndex, cellIndex, cellInfo.getIndex());
                                    HSSFCell cell = row.createCell(startIndex);
                                    String styleID = cellInfo.getStyleID();
                                    Style style = styleMap.get(styleID);
                                    CellStyle dataStyle = wb.createCellStyle();
                                    setBorder(style, dataStyle);
                                    setAlignment(style, dataStyle);
                                    setValue(wb, cellInfo, cell, style, dataStyle);
                                    setCellColor(style, dataStyle);
                                    cell.setCellStyle(dataStyle);
                                    if (cellInfo.getComment() != null) {
                                        Data data = cellInfo.getComment().getData();
                                        Comment comment = sheet.createDrawingPatriarch().createCellComment(new HSSFClientAnchor(0, 0, 0, 0, (short) 3, 3, (short) 5, 6));
                                        comment.setString(new HSSFRichTextString(data.getText()));
                                        cell.setCellComment(comment);
                                    }

                                    startIndex = getCellRanges(createRowIndex, cellRangeAddresses, startIndex, cellInfo, style);
                                }
                            }
                        }
                    }
                }

                addCellRange(sheet, cellRangeAddresses);
            }

            log.info("4.开始写入图片：" + freemarkerInput.getExcelImageInputs());
            if (!CollectionUtils.isEmpty(freemarkerInput.getExcelImageInputs())) {
                writeImageToExcel(freemarkerInput.getExcelImageInputs(), wb);
            }

            log.info("5.完成写入图片：" + freemarkerInput.getExcelImageInputs());
            wb.write(outputStream);
            outputStream.close();
        } catch (Exception var39) {
            var39.printStackTrace();
            log.error("导出excel异常：" + var39.getMessage());
        } finally {
            try {
                out.close();
            } catch (Exception var38) {
            }

        }

    }

    private void createExcelToStream(FreemarkerInput freemarkerInput, OutputStream outputStream) {
        BufferedWriter out = null;

        try {
            Template template = getTemplate(freemarkerInput.getTemplateName(), freemarkerInput.getTemplateFilePath());
            File tempXMLFile = new File(freemarkerInput.getXmlTempFile() + freemarkerInput.getFileName() + ".xml");
            FileUtils.forceMkdirParent(tempXMLFile);
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempXMLFile), "UTF-8"));
            template.process(freemarkerInput.getDataMap(), out);
            log.info("1.完成将文本数据导入到XML文件中");

            SAXReader reader = new SAXReader();
            Document document = reader.read(tempXMLFile);
            Map<String, Style> styleMap = readXmlStyle(document);
            log.info("2.完成解析XML中样式信息");
            List<Worksheet> worksheets = readXmlWorksheet(document);
            log.info("3.开始将XML信息写入Excel，数据为：" + worksheets.toString());

            XSSFWorkbook wb = new XSSFWorkbook();
            Iterator var10 = worksheets.iterator();
            while (var10.hasNext()) {
                Worksheet worksheet = (Worksheet) var10.next();
                XSSFSheet sheet = wb.createSheet(worksheet.getName());
                Table table = worksheet.getTable();
                List<Row> rows = table.getRows();
                List<Column> columns = table.getColumns();
                log.info("当前sheet名称=" + worksheet.getName() + "; sheet---->列数" + columns.size());
                Map<Integer, Integer> cellWidthMap = getCellWidth10(rows);
                int createRowIndex;
                if (columns != null && columns.size() > 0) {
                    for (int i = 0; i < columns.size(); ++i) {
                        Column column = (Column) columns.get(i);
                        //createRowIndex = getCellWidthIndex(createRowIndex, i, column.getIndex());
                        //sheet.setColumnWidth(createRowIndex, (int)column.getWidth() * 50);
                        //sheet.setColumnWidth(i, (int)column.getWidth() * 80);
                        int width = 2160; // 默认宽度
                        if (cellWidthMap.containsKey(i)) {
                            width = cellWidthMap.get(i);
                        }
                        sheet.setColumnWidth(i, width);
                    }
                }


              /*  int defaultWidth = 2160; // excel默认宽度8.0 , 对应的java近似值
                Map<Integer,Integer> map = tableWidth(rows,worksheet.getName());
                for(int i = 0 ; i<columns.size();i++){
                    int columnWidth = sheet.getColumnWidth(i);
                    //sheet.setDefaultColumnWidth(2160);
                    if("账户状况".equals(worksheet.getName())) { // 第一个sheet页宽度设置默认值 , 其他另算
                        sheet.setColumnWidth(i,defaultWidth);
                    } else {
                        if (map.containsKey(i)) {
                            sheet.setColumnWidth(i, map.get(i));
                        } else {
                            sheet.setColumnWidth(i, defaultWidth);
                        }
                    }
                }
*/

                createRowIndex = 0;
                List<CellRangeAddressEntity> cellRangeAddresses = new ArrayList();

                for (int rowIndex = 0; rowIndex < rows.size(); ++rowIndex) {
                    Row rowInfo = (Row) rows.get(rowIndex);
                    if (rowInfo != null) {
                        createRowIndex = getIndex(createRowIndex, rowIndex, rowInfo.getIndex());
                        XSSFRow row = sheet.createRow(createRowIndex);
                        if (rowInfo.getHeight() != null) {
                            Integer height = rowInfo.getHeight() * 20;
                            row.setHeight(height.shortValue());
                        }

                        List<Cell> cells = rowInfo.getCells();
                        if (!CollectionUtils.isEmpty(cells)) {
                            int startIndex = 0;

                            for (int cellIndex = 0; cellIndex < cells.size(); ++cellIndex) {
                                Cell cellInfo = (Cell) cells.get(cellIndex);
                                if (cellInfo != null) {
                                    startIndex = getIndex(startIndex, cellIndex, cellInfo.getIndex());
                                    XSSFCell cell = row.createCell(startIndex);
                                    String styleID = cellInfo.getStyleID();
                                    Style style = styleMap.get(styleID);
                                    CellStyle dataStyle = wb.createCellStyle();
                                    DataFormat dataFormat = wb.createDataFormat();
                                    dataStyle.setDataFormat(dataFormat.getFormat("@")); // 设置为文本格式
                                    setBorder(style, dataStyle);
                                    setAlignment(style, dataStyle);
                                    setValue((XSSFWorkbook) wb, cellInfo, (XSSFCell) cell, style, dataStyle);
                                    setCellColor(style, dataStyle); // 设置背景色
                                    cell.setCellStyle(dataStyle);
                                    if ("账户状况".equals(worksheet.getName())) {
                                        String cellText = cellInfo.getData().getText();
                                        if (style != null) {
                                            Style.Interior interior = style.getInterior();
                                            if ("期初结存".equals(cellText)) {
                                                interior.setColor("#BDD7EE");
                                            } else if ("保证金占用".equals(cellText)) {
                                                interior.setColor("#8bc175");
                                            }
                                            style.setInterior(interior);
                                        }
                                        log.info("单元格内容=" + cellText);
                                    }
                                    if (cellInfo.getComment() != null) {
                                        Data data = cellInfo.getComment().getData();
                                        Comment comment = sheet.createDrawingPatriarch().createCellComment(new XSSFClientAnchor(0, 0, 0, 0, 3, 3, 5, 6));
                                        comment.setString(new XSSFRichTextString(data.getText()));
                                        cell.setCellComment(comment);
                                    }

                                    startIndex = getCellRanges(createRowIndex, cellRangeAddresses, startIndex, cellInfo, style);
                                }
                            }
                        }
                    }
                }

                addCellRange((XSSFSheet) sheet, cellRangeAddresses);
            }

            log.info("4.开始写入图片：" + freemarkerInput.getExcelImageInputs());
            if (!CollectionUtils.isEmpty(freemarkerInput.getExcelImageInputs())) {
                writeImageToExcel(freemarkerInput.getExcelImageInputs(), wb);
            }

            log.info("5.完成写入图片：" + freemarkerInput.getExcelImageInputs());
            wb.write(outputStream);
            outputStream.close();
        } catch (Exception var39) {
            var39.printStackTrace();
            log.error("导出excel异常：" + var39.getMessage());
        } finally {
            try {
                out.close();
            } catch (Exception var38) {
            }

        }

    }

    public Map<String, Style> readXmlStyle(Document document) {
        Map<String, Style> styleMap = XmlReader.getStyle(document);
        return styleMap;
    }

    public List<Worksheet> readXmlWorksheet(Document document) {
        List<Worksheet> worksheets = XmlReader.getWorksheet(document);
        return worksheets;
    }

    private int getIndex(int columnIndex, int i, Integer index) {
        if (index != null) {
            columnIndex = index - 1;
        }

        if (index == null && columnIndex != 0) {
            ++columnIndex;
        }

        if (index == null && columnIndex == 0) {
            columnIndex = i;
        }

        return columnIndex;
    }

    private int getCellWidthIndex(int columnIndex, int i, Integer index) {
        if (index != null) {
            columnIndex = index;
        }

        if (index == null && columnIndex != 0) {
            ++columnIndex;
        }

        if (index == null && columnIndex == 0) {
            columnIndex = i;
        }

        return columnIndex;
    }

    private void setBorder(Style style, CellStyle dataStyle) {
        if (style != null && style.getBorders() != null) {
            for (int k = 0; k < style.getBorders().size(); ++k) {
                Border border = (Border) style.getBorders().get(k);
                if (border != null) {
                    if ("Bottom".equals(border.getPosition())) {
                        dataStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
                        dataStyle.setBorderBottom(BorderStyle.THIN);
                    }

                    if ("Left".equals(border.getPosition())) {
                        dataStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
                        dataStyle.setBorderLeft(BorderStyle.THIN);
                    }

                    if ("Right".equals(border.getPosition())) {
                        dataStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
                        dataStyle.setBorderRight(BorderStyle.THIN);
                    }

                    if ("Top".equals(border.getPosition())) {
                        dataStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
                        dataStyle.setBorderTop(BorderStyle.THIN);
                    }
                }
            }
        }

    }

    private void writeImageToExcel(List<ExcelImageInput> excelImageInputs, HSSFWorkbook wb) throws IOException {
        BufferedImage bufferImg = null;
        if (!CollectionUtils.isEmpty(excelImageInputs)) {
            Iterator var3 = excelImageInputs.iterator();

            while (var3.hasNext()) {
                ExcelImageInput excelImageInput = (ExcelImageInput) var3.next();
                Sheet sheet = wb.getSheetAt(excelImageInput.getSheetIndex());
                if (sheet != null) {
                    Drawing patriarch = sheet.createDrawingPatriarch();
                    HSSFClientAnchor anchor = excelImageInput.getAnchorXls();
                    anchor.setAnchorType(AnchorType.DONT_MOVE_AND_RESIZE);
                    String imagePath = excelImageInput.getImgPath();
                    ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
                    bufferImg = ImageIO.read(new File(imagePath));
                    String imageType = imagePath.substring(imagePath.lastIndexOf(".") + 1, imagePath.length());
                    ImageIO.write(bufferImg, imageType, byteArrayOut);
                    patriarch.createPicture(anchor, wb.addPicture(byteArrayOut.toByteArray(), 5));
                }
            }
        }

    }

    private void writeImageToExcel(List<ExcelImageInput> excelImageInputs, XSSFWorkbook wb) throws IOException {
        BufferedImage bufferImg = null;
        if (!CollectionUtils.isEmpty(excelImageInputs)) {
            Iterator var3 = excelImageInputs.iterator();

            while (var3.hasNext()) {
                ExcelImageInput excelImageInput = (ExcelImageInput) var3.next();
                Sheet sheet = wb.getSheetAt(excelImageInput.getSheetIndex());
                if (sheet != null) {
                    Drawing patriarch = sheet.createDrawingPatriarch();
                    XSSFClientAnchor anchor = excelImageInput.getAnchorXlsx();
                    anchor.setAnchorType(AnchorType.DONT_MOVE_AND_RESIZE);
                    String imagePath = excelImageInput.getImgPath();
                    ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
                    bufferImg = ImageIO.read(new File(imagePath));
                    String imageType = imagePath.substring(imagePath.lastIndexOf(".") + 1, imagePath.length());
                    ImageIO.write(bufferImg, imageType, byteArrayOut);
                    patriarch.createPicture(anchor, wb.addPicture(byteArrayOut.toByteArray(), 5));
                }
            }
        }

    }

    private void addCellRange(HSSFSheet sheet, List<CellRangeAddressEntity> cellRangeAddresses) {
        if (!CollectionUtils.isEmpty(cellRangeAddresses)) {
            Iterator var2 = cellRangeAddresses.iterator();

            while (true) {
                CellRangeAddressEntity cellRangeAddressEntity;
                CellRangeAddress cellRangeAddress;
                do {
                    if (!var2.hasNext()) {
                        return;
                    }

                    cellRangeAddressEntity = (CellRangeAddressEntity) var2.next();
                    cellRangeAddress = cellRangeAddressEntity.getCellRangeAddress();
                    sheet.addMergedRegion(cellRangeAddress);
                } while (CollectionUtils.isEmpty(cellRangeAddressEntity.getBorders()));

                for (int k = 0; k < cellRangeAddressEntity.getBorders().size(); ++k) {
                    Border border = (Border) cellRangeAddressEntity.getBorders().get(k);
                    if (border != null) {
                        if ("Bottom".equals(border.getPosition())) {
                            RegionUtil.setBorderBottom(BorderStyle.THIN, cellRangeAddress, sheet);
                        }

                        if ("Left".equals(border.getPosition())) {
                            RegionUtil.setBorderLeft(BorderStyle.THIN, cellRangeAddress, sheet);
                        }

                        if ("Right".equals(border.getPosition())) {
                            RegionUtil.setBorderRight(BorderStyle.THIN, cellRangeAddress, sheet);
                        }

                        if ("Top".equals(border.getPosition())) {
                            RegionUtil.setBorderTop(BorderStyle.THIN, cellRangeAddress, sheet);
                        }
                    }
                }
            }
        }
    }

    private void addCellRange(XSSFSheet sheet, List<CellRangeAddressEntity> cellRangeAddresses) {
        if (!CollectionUtils.isEmpty(cellRangeAddresses)) {
            Iterator var2 = cellRangeAddresses.iterator();

            while (true) {
                CellRangeAddressEntity cellRangeAddressEntity;
                CellRangeAddress cellRangeAddress;
                do {
                    if (!var2.hasNext()) {
                        return;
                    }

                    cellRangeAddressEntity = (CellRangeAddressEntity) var2.next();
                    cellRangeAddress = cellRangeAddressEntity.getCellRangeAddress();
                    sheet.addMergedRegion(cellRangeAddress);
                } while (CollectionUtils.isEmpty(cellRangeAddressEntity.getBorders()));

                for (int k = 0; k < cellRangeAddressEntity.getBorders().size(); ++k) {
                    Border border = (Border) cellRangeAddressEntity.getBorders().get(k);
                    if (border != null) {
                        if ("Bottom".equals(border.getPosition())) {
                            RegionUtil.setBorderBottom(BorderStyle.THIN, cellRangeAddress, sheet);
                        }

                        if ("Left".equals(border.getPosition())) {
                            RegionUtil.setBorderLeft(BorderStyle.THIN, cellRangeAddress, sheet);
                        }

                        if ("Right".equals(border.getPosition())) {
                            RegionUtil.setBorderRight(BorderStyle.THIN, cellRangeAddress, sheet);
                        }

                        if ("Top".equals(border.getPosition())) {
                            RegionUtil.setBorderTop(BorderStyle.THIN, cellRangeAddress, sheet);
                        }
                    }
                }
            }
        }
    }

    private void setAlignment(Style style, CellStyle dataStyle) {
        if (style != null && style.getAlignment() != null) {
            String horizontal = style.getAlignment().getHorizontal();
            if (!ObjectUtils.isEmpty(horizontal)) {
                if ("Left".equals(horizontal)) {
                    dataStyle.setAlignment(HorizontalAlignment.LEFT);
                } else if ("Center".equals(horizontal)) {
                    dataStyle.setAlignment(HorizontalAlignment.CENTER);
                } else {
                    dataStyle.setAlignment(HorizontalAlignment.RIGHT);
                }
            }

            String vertical = style.getAlignment().getVertical();
            if (!ObjectUtils.isEmpty(vertical)) {
                if ("Top".equals(vertical)) {
                    dataStyle.setVerticalAlignment(VerticalAlignment.TOP);
                } else if ("Center".equals(vertical)) {
                    dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                } else if ("Bottom".equals(vertical)) {
                    dataStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
                } else if ("JUSTIFY".equals(vertical)) {
                    dataStyle.setVerticalAlignment(VerticalAlignment.JUSTIFY);
                } else {
                    dataStyle.setVerticalAlignment(VerticalAlignment.DISTRIBUTED);
                }
            }

            String wrapText = style.getAlignment().getWrapText();
            if (!ObjectUtils.isEmpty(wrapText)) {
                dataStyle.setWrapText(true);
            }
        }

    }

    private void setCellColor(Style style, CellStyle dataStyle) {
        if (style != null && style.getInterior() != null) {
            String color = style.getInterior().getColor();
            if (color == null) {
                color = "#FFFFFF";
            }
            Integer[] rgb = ColorUtil.hex2Rgb(color);
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
            HSSFPalette palette = hssfWorkbook.getCustomPalette();
            HSSFColor paletteColor = palette.findSimilarColor(rgb[0], rgb[1], rgb[2]);
            //dataStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex()); // 背景色
            dataStyle.setFillForegroundColor(paletteColor.getIndex()); // 背景色
            dataStyle.setFillBackgroundColor(paletteColor.getIndex());// 前景色
            if ("Solid".equals(style.getInterior().getPattern())) {
                dataStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            }
        }

    }

    private int getCellRanges(int createRowIndex, List<CellRangeAddressEntity> cellRangeAddresses, int startIndex, Cell cellInfo, Style style) {
        if (cellInfo.getMergeAcross() != null || cellInfo.getMergeDown() != null) {
            CellRangeAddress cellRangeAddress = null;
            int length;
            int i;
            if (cellInfo.getMergeAcross() != null && cellInfo.getMergeDown() != null) {
                length = startIndex;
                if (cellInfo.getMergeAcross() != 0) {
                    length = startIndex + cellInfo.getMergeAcross();
                }

                i = createRowIndex;
                if (cellInfo.getMergeDown() != 0) {
                    i = createRowIndex + cellInfo.getMergeDown();
                }

                cellRangeAddress = new CellRangeAddress(createRowIndex, i, (short) startIndex, (short) length);
            } else if (cellInfo.getMergeAcross() != null && cellInfo.getMergeDown() == null) {
                if (cellInfo.getMergeAcross() != 0) {
                    length = startIndex + cellInfo.getMergeAcross();
                    cellRangeAddress = new CellRangeAddress(createRowIndex, createRowIndex, (short) startIndex, (short) length);
                }
            } else if (cellInfo.getMergeDown() != null && cellInfo.getMergeAcross() == null && cellInfo.getMergeDown() != 0) {
                length = createRowIndex + cellInfo.getMergeDown();
                cellRangeAddress = new CellRangeAddress(createRowIndex, length, (short) startIndex, (short) startIndex);
            }

            if (cellInfo.getMergeAcross() != null) {
                length = cellInfo.getMergeAcross();

                for (i = 0; i < length; ++i) {
                    ++startIndex;
                }
            }

            CellRangeAddressEntity cellRangeAddressEntity = new CellRangeAddressEntity();
            cellRangeAddressEntity.setCellRangeAddress(cellRangeAddress);
            if (style != null && style.getBorders() != null) {
                cellRangeAddressEntity.setBorders(style.getBorders());
            }

            cellRangeAddresses.add(cellRangeAddressEntity);
        }

        return startIndex;
    }

    private void setValue(XSSFWorkbook wb, Cell cellInfo, XSSFCell cell, Style style, CellStyle dataStyle) {
        if (cellInfo.getData() != null) {
            XSSFFont font = wb.createFont();
            String color;
            Integer[] rgb;
            HSSFWorkbook hssfWorkbook;
            HSSFPalette palette;
            HSSFColor paletteColor;
            if (style != null && style.getFont() != null) {
                color = style.getFont().getColor();
                if (color == null) {
                    color = "#000000";
                }

                rgb = ColorUtil.hex2Rgb(color);
                hssfWorkbook = new HSSFWorkbook();
                palette = hssfWorkbook.getCustomPalette();
                paletteColor = palette.findSimilarColor(rgb[0], rgb[1], rgb[2]);
                font.setColor(paletteColor.getIndex());
            }

            if (!ObjectUtils.isEmpty(cellInfo.getData().getType()) && "Number".equals(cellInfo.getData().getType())) {
                cell.setCellType(CellType.NUMERIC);
            }

            if (style != null && style.getFont().getBold() > 0) {
                font.setBold(true);
            }

            if (style != null && !ObjectUtils.isEmpty(style.getFont().getFontName())) {
                font.setFontName(style.getFont().getFontName());
            }

            if (style != null && style.getFont().getSize() > 0.0D) {
                font.setFontHeightInPoints((short) ((int) style.getFont().getSize()));
            }

            if (cellInfo.getData().getFont() != null) {
                if (cellInfo.getData().getFont().getBold() > 0) {
                    font.setBold(true);
                }

                if ("Number".equals(cellInfo.getData().getType())) {
                    cell.setCellValue((double) Float.parseFloat(cellInfo.getData().getFont().getText()));
                } else {
                    cell.setCellValue(cellInfo.getData().getFont().getText());
                }

                if (!ObjectUtils.isEmpty(cellInfo.getData().getFont().getCharSet())) {
                    font.setCharSet(Integer.valueOf(cellInfo.getData().getFont().getCharSet()));
                }
            } else if ("Number".equals(cellInfo.getData().getType())) {
                if (!ObjectUtils.isEmpty(cellInfo.getData().getText())) {
                    cell.setCellValue((double) Float.parseFloat(cellInfo.getData().getText().replaceAll(",", "")));
                }
            } else {
                cell.setCellValue(cellInfo.getData().getText());
            }

            if (style != null && style.getNumberFormat() != null) {
                color = style.getFont().getColor();
                if (color == null) {
                    color = "#000000";
                }

                rgb = ColorUtil.hex2Rgb(color);
                hssfWorkbook = new HSSFWorkbook();
                palette = hssfWorkbook.getCustomPalette();
                paletteColor = palette.findSimilarColor(rgb[0], rgb[1], rgb[2]);
                font.setColor(paletteColor.getIndex());
                if ("0%".equals(style.getNumberFormat().getFormat())) {
                    XSSFDataFormat format = wb.createDataFormat();
                    dataStyle.setDataFormat(format.getFormat(style.getNumberFormat().getFormat()));
                } else {
                    dataStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
                }
            }

            dataStyle.setFont(font);
        }

    }

    private void setValue(HSSFWorkbook wb, Cell cellInfo, HSSFCell cell, Style style, CellStyle dataStyle) {
        if (cellInfo.getData() != null) {
            HSSFFont font = wb.createFont();
            String color;
            Integer[] rgb;
            HSSFWorkbook hssfWorkbook;
            HSSFPalette palette;
            HSSFColor paletteColor;
            if (style != null && style.getFont() != null) {
                color = style.getFont().getColor();
                if (color == null) {
                    color = "#000000";
                }

                rgb = ColorUtil.hex2Rgb(color);
                hssfWorkbook = new HSSFWorkbook();
                palette = hssfWorkbook.getCustomPalette();
                paletteColor = palette.findSimilarColor(rgb[0], rgb[1], rgb[2]);
                font.setColor(paletteColor.getIndex());
            }

            if (!ObjectUtils.isEmpty(cellInfo.getData().getType()) && "Number".equals(cellInfo.getData().getType())) {
                cell.setCellType(CellType.NUMERIC);
            }

            if (style != null && style.getFont().getBold() > 0) {
                font.setBold(true);
            }

            if (style != null && !ObjectUtils.isEmpty(style.getFont().getFontName())) {
                font.setFontName(style.getFont().getFontName());
            }

            if (style != null && style.getFont().getSize() > 0.0D) {
                font.setFontHeightInPoints((short) ((int) style.getFont().getSize()));
            }

            if (cellInfo.getData().getFont() != null) {
                if (cellInfo.getData().getFont().getBold() > 0) {
                    font.setBold(true);
                }

                if ("Number".equals(cellInfo.getData().getType())) {
                    cell.setCellValue((double) Float.parseFloat(cellInfo.getData().getFont().getText()));
                } else {
                    cell.setCellValue(cellInfo.getData().getFont().getText());
                }

                if (!ObjectUtils.isEmpty(cellInfo.getData().getFont().getCharSet())) {
                    font.setCharSet(Integer.valueOf(cellInfo.getData().getFont().getCharSet()));
                }
            } else if ("Number".equals(cellInfo.getData().getType())) {
                if (!ObjectUtils.isEmpty(cellInfo.getData().getText())) {
                    cell.setCellValue((double) Float.parseFloat(cellInfo.getData().getText().replaceAll(",", "")));
                }
            } else {
                cell.setCellValue(cellInfo.getData().getText());
            }

            if (style != null && style.getNumberFormat() != null) {
                color = style.getFont().getColor();
                if (color == null) {
                    color = "#000000";
                }

                rgb = ColorUtil.hex2Rgb(color);
                hssfWorkbook = new HSSFWorkbook();
                palette = hssfWorkbook.getCustomPalette();
                paletteColor = palette.findSimilarColor(rgb[0], rgb[1], rgb[2]);
                font.setColor(paletteColor.getIndex());
                if ("0%".equals(style.getNumberFormat().getFormat())) {
                    HSSFDataFormat format = wb.createDataFormat();
                    dataStyle.setDataFormat(format.getFormat(style.getNumberFormat().getFormat()));
                } else {
                    dataStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
                }
            }
            dataStyle.setFont(font);
        }
    }

    /**
     * 获取某一行的所有单元格宽度 key = 列下表 , value = 单元格宽度 仅适用于一行全是字符串类型
     */
    public Map<Integer, Integer> getCellWidth(Row row) {
        Map<Integer, Integer> returnMap = new HashMap<>();
        if (row != null) {
            List<Cell> cells = row.getCells();
            for (int index = 0; index < cells.size(); index++) {
                Cell cell = cells.get(index);
                String value = cell.getData().getText();
                // excel 单元格默认宽度8.0,对应java的近似值为2160,大约能容纳4个中文 , 一个中文占3个字符 , 每个字符长度大约=2160/4/3=180
                int width = value.getBytes().length * 180;
                returnMap.put(index, width + 360); // 单元格多两头多空余一点空间
            }

        }
        return returnMap;
    }

    /**
     * 计算前10行中每一个单元格宽度最大宽度值,并绑定到列下标
     */
    public Map<Integer, Integer> getCellWidth10(List<Row> rows) {
        Map<Integer, Integer> returnMap = new HashMap<>();
        for (int index = 0; index < rows.size() && index < 10; index++) {
            Row row = rows.get(index);
            // 当前行的所有列下标和列宽
            Map<Integer, Integer> map = getCellWidth(row);
            if (index == 0) { // 第一行
                returnMap = map;
            } else { // 从第二行开始
                compareAndAssign(returnMap, map);
            }
        }
        return returnMap;
    }

    /**
     * 比较两个map中相同key的值大小 , 取最大值并赋值
     * @param destMap 目标数据map
     * @param tempMap 临时map
     */
    public void compareAndAssign(Map<Integer, Integer> destMap, Map<Integer, Integer> tempMap) {
        for (Map.Entry<Integer, Integer> entry : tempMap.entrySet()) {
            Integer tempKey = entry.getKey();
            Integer tempValue = entry.getValue();
            if (destMap.containsKey(tempKey)) {
                Integer value = destMap.get(tempKey);
                if (value < tempValue) { // 临时值大于目标值, 用临时值去覆盖目标值
                    destMap.put(tempKey, tempValue);
                }
            }
        }
    }
}
