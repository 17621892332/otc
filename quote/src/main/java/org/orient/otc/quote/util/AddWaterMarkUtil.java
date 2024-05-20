package org.orient.otc.quote.util;

import com.aspose.words.*;
import org.orient.otc.quote.config.TemplateConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * 添加水印工具
 */
@Component
public class AddWaterMarkUtil {
    @Autowired
    HttpServletRequest request;
    @Autowired
    TemplateConfig templateConfig;
    /**
     * 仅doc转docx
     * @param docFile
     * @throws Exception
     */
    public void doc2Docx(File docFile) throws Exception {
        String savePath = "C:/Users/dzrh/Desktop/1/水印/"+docFile.getName()+".docx";
        InputStream inputStream = new FileInputStream(docFile);
        Document document = new Document(inputStream);
        document.save(savePath, SaveFormat.DOCX);
    }

    /**
     * doc转docx并添加水印 - 文本水印
     * @param docFile docx文件目录
     * @param watermarkPath 转换之后带水印的目录
     * @throws Exception 异常
     */
    public String doc2DocxAndAddTextWatermark(File docFile, String watermarkPath) {
        InputStream inputStream = null;
        try {
            File watermarkFile = new File(watermarkPath);
            if (!watermarkFile.exists()){
                watermarkFile.mkdirs();
            }
            String savePath = watermarkPath+"/ "+docFile.getName().replaceAll(".doc","")+".docx";
            inputStream = new FileInputStream(docFile);
            Document document = new Document(inputStream);
            // 添加水印
            TextWatermarkOptions watermarkOptions = new TextWatermarkOptions();
            watermarkOptions.setFontSize(144f); // 水印字体大小
            String font = request.getParameter("font");
            watermarkOptions.setFontFamily(font); // 水印字体
            watermarkOptions.setColor(Color.lightGray); // 水印字体颜色
            watermarkOptions.setLayout(WatermarkLayout.HORIZONTAL); // 水印布局:斜体
            watermarkOptions.isSemitrasparent(true); // 半透明的
            document.getWatermark().setText("东证润和", watermarkOptions); // 水印文本
            String osName = System.getProperty("os.name");
            // 设置字体目录
            if (!(osName != null && (osName.toUpperCase().contains("Windows".toUpperCase()) || osName.toUpperCase().contains("mac".toUpperCase())))) { // 非windows或mac系统 视为linux系统
                FontSettings fontSettings = FontSettings.getDefaultInstance();
                String fontsPath = templateConfig.getTradeConfirmBookPath()+"fonts";
                fontSettings.setFontsFolder(fontsPath,true);
                document.setFontSettings(fontSettings);
            }
            document.save(savePath, SaveFormat.DOCX);
            return  savePath;
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (inputStream!=null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }
    /**
     * doc转docx并添加水印 - 图片水印
     * @param docFile docx文件目录
     * @param watermarkPath 转换之后带水印的目录
     * @throws Exception 异常
     */
    public String doc2DocxAndAddImageWatermark(File docFile, String watermarkPath) {
        InputStream inputStream = null;
        try {
            File watermarkFile = new File(watermarkPath);
            if (!watermarkFile.exists()){
                watermarkFile.mkdirs();
            }
            String savePath = watermarkPath+"/"+ UUID.randomUUID()+docFile.getName().replaceAll(".doc","")+".docx";
            inputStream = new FileInputStream(docFile);
            Document document = new Document(inputStream);
            ImageWatermarkOptions watermarkOptions = new ImageWatermarkOptions();
            watermarkOptions.isWashout(false);
            String osName = System.getProperty("os.name");
            // 设置水印目录
            String docWaterMarkPath = "";
            if (!(osName != null && (osName.toUpperCase().contains("Windows".toUpperCase()) || osName.toUpperCase().contains("mac".toUpperCase())))) { // 非windows或mac系统 视为linux系统
                docWaterMarkPath = templateConfig.getTradeConfirmBookPath() + "docWaterMark.png";
            } else {
                // 获取当前项目的根目录路径
                String rootpath = System.getProperty("user.dir").replace("\\","/");
                docWaterMarkPath = rootpath+"/doc/template/交易确认书/docWaterMark.png";
            }
            BufferedImage image = ImageIO.read(new File(docWaterMarkPath));
            document.getWatermark().setImage(image, watermarkOptions);
            document.save(savePath, SaveFormat.DOCX);
            return  savePath;
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (inputStream!=null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }
}
