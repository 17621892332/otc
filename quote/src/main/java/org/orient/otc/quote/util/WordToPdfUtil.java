package org.orient.otc.quote.util;

import com.aspose.words.Document;
import com.aspose.words.FontSettings;
import com.aspose.words.SaveFormat;
import com.spire.pdf.PdfDocument;
import org.orient.otc.quote.config.TemplateConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;

/**
 * 复杂word转pdf工具类
 * word中嵌套表格,表格中有多重合并单元格操作
 * @author dzrh
 */
@Component
public class WordToPdfUtil {
    @Autowired
    TemplateConfig templateConfig;
    /**
     * word转pdf
     * @param wordPath word路径
     * @param pdfPath pdf路径
     * @throws Exception 异常
     */
    public void word2Pdf(String wordPath,String pdfPath) throws Exception {
        Document document = new Document(wordPath);
        // 获取操作系统名称 Windows xxx
        String osName = System.getProperty("os.name");
        // 设置字体目录
        // 非windows或mac系统 视为linux系统
        if (!(osName != null && (osName.toUpperCase().contains("Windows".toUpperCase()) || osName.toUpperCase().contains("mac".toUpperCase())))) {
            String fontsPath = templateConfig.getTradeConfirmBookPath()+"fonts";
            PdfDocument.setCustomFontsFolders(fontsPath);
            FontSettings fontSettings = FontSettings.getDefaultInstance();
            // 设置字体目录,第二个参数表示是否递归此文件夹
            fontSettings.setFontsFolder(fontsPath,true);
            document.setFontSettings(fontSettings);
        }
        document.save(pdfPath, SaveFormat.PDF);
    }
    public void word2Pdf(ByteArrayInputStream inputStream, OutputStream stream) throws Exception {
        Document document = new Document(inputStream);
        String fontsPath = templateConfig.getTradeConfirmBookPath()+"fonts";
        PdfDocument.setCustomFontsFolders(fontsPath);
        FontSettings fontSettings = FontSettings.getDefaultInstance();
        // 设置字体目录,第二个参数表示是否递归此文件夹
        fontSettings.setFontsFolder(fontsPath,true);
        document.setFontSettings(fontSettings);
        document.save(stream, SaveFormat.PDF);
    }
}
