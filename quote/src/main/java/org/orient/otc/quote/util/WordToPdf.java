package org.orient.otc.quote.util;


import org.docx4j.Docx4J;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.orient.otc.quote.config.TemplateConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

/**
 * word 转 pdf (简单模板)
 */
@Component
public class WordToPdf {
    @Autowired
     TemplateConfig templateConfig;

    static final Logger log = LoggerFactory.getLogger(WordToPdf.class);
    public  WordprocessingMLPackage getWordprocessingMLPackage(File docFile) throws Exception {
        WordprocessingMLPackage pkg = Docx4J.load(docFile);
        if(
                PhysicalFonts.get("SimSun") == null
            ||  PhysicalFonts.get("eastAsia") == null
            ||  PhysicalFonts.get("宋体") == null
            ||  PhysicalFonts.get("Calibri") == null
        ) {
            ApplicationHome home = new ApplicationHome(WordToPdf.class);
            // 如果系统字体不存在 , 统一用交易确认书模板下的字体
            String path = home.getSource().getParentFile().toString()+File.separator+templateConfig.getTradeConfirmBookPath()+"fonts/simsun.ttc";
            //String path = "D:/workspace/otc-server/quote/src/main/java/org/orient/otc/quote/util/simsun.ttc";
            File simsunFile = new File(path);
            if(simsunFile.exists()){
                log.info("加载本地SimSun字体库路径存在");
            }
            URL url = simsunFile.toURI().toURL();
            PhysicalFonts.addPhysicalFonts("SimSun", url);
        }
        if ( PhysicalFonts.get("黑体") == null) {
            ApplicationHome home = new ApplicationHome(WordToPdf.class);
            String path = home.getSource().getParentFile().toString()+File.separator+templateConfig.getTradeConfirmBookPath()+"fonts/simhei.ttf";
            File simsunFile = new File(path);
            if(simsunFile.exists()){
                log.info("加载本地simhei字体库路径存在");
            }
            URL url = simsunFile.toURI().toURL();
            PhysicalFonts.addPhysicalFonts("simhei", url);
        }
        if ( PhysicalFonts.get("楷体") == null) {
            ApplicationHome home = new ApplicationHome(WordToPdf.class);
            String path = home.getSource().getParentFile().toString()+File.separator+templateConfig.getTradeConfirmBookPath()+"fonts/kaiti.ttf";
            File simsunFile = new File(path);
            if(simsunFile.exists()){
                log.info("加载本地kaiti字体库路径存在");
            }
            URL url = simsunFile.toURI().toURL();
            PhysicalFonts.addPhysicalFonts("kaiti", url);
        }
        Mapper fontMapper = new IdentityPlusMapper();
        fontMapper.put("隶书", PhysicalFonts.get("LiSu"));
        fontMapper.put("微软雅黑", PhysicalFonts.get("Microsoft Yahei"));
        fontMapper.put("黑体", PhysicalFonts.get("simhei"));
        fontMapper.put("楷体", PhysicalFonts.get("kaiti"));
        fontMapper.put("新宋体", PhysicalFonts.get("NSimSun"));
        fontMapper.put("华文行楷", PhysicalFonts.get("STXingkai"));
        fontMapper.put("华文仿宋", PhysicalFonts.get("STFangsong"));
        fontMapper.put("仿宋", PhysicalFonts.get("FangSong"));
        fontMapper.put("幼圆", PhysicalFonts.get("YouYuan"));
        fontMapper.put("华文宋体", PhysicalFonts.get("STSong"));
        fontMapper.put("华文中宋", PhysicalFonts.get("STZhongsong"));
        fontMapper.put("等线", PhysicalFonts.get("SimSun"));
        fontMapper.put("等线 Light", PhysicalFonts.get("SimSun"));
        fontMapper.put("华文琥珀", PhysicalFonts.get("STHupo"));
        fontMapper.put("华文隶书", PhysicalFonts.get("STLiti"));
        fontMapper.put("华文新魏", PhysicalFonts.get("STXinwei"));
        fontMapper.put("华文彩云", PhysicalFonts.get("STCaiyun"));
        fontMapper.put("方正姚体", PhysicalFonts.get("FZYaoti"));
        fontMapper.put("方正舒体", PhysicalFonts.get("FZShuTi"));
        fontMapper.put("华文细黑", PhysicalFonts.get("STXihei"));
        fontMapper.put("宋体扩展", PhysicalFonts.get("simsun-extB"));
        fontMapper.put("仿宋_GB2312", PhysicalFonts.get("FangSong_GB2312"));
        //解决宋体（正文）和宋体（标题）的乱码问题
        PhysicalFonts.put("PMingLiU", PhysicalFonts.get("SimSun"));
        PhysicalFonts.put("新細明體", PhysicalFonts.get("SimSun"));
        //宋体&新宋体
        fontMapper.put("宋体", PhysicalFonts.get("SimSun"));
        fontMapper.put("SimSun", PhysicalFonts.get("SimSun"));
        pkg.setFontMapper(fontMapper);
        return pkg;
    }
    public   void wordToPdf(String docFile,String pdfFile) throws Exception {
        FileOutputStream fos = null;
        try {
            WordprocessingMLPackage pkg = getWordprocessingMLPackage(new File(docFile));
            fos = new FileOutputStream(pdfFile);
            Docx4J.toPDF(pkg, fos);
        } finally {
            if (fos != null){
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public   void wordToPdf(File docFile,File pdfFile) throws Exception {
        FileOutputStream fos = null;
        try {
            WordprocessingMLPackage pkg = getWordprocessingMLPackage(docFile);
            fos = new FileOutputStream(pdfFile);
            Docx4J.toPDF(pkg, fos);
        } finally {
            if (fos != null){
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public   void wordToPdf(File docFile,String pdfFile) throws Exception {
        FileOutputStream fos = null;
        try {
            WordprocessingMLPackage pkg = getWordprocessingMLPackage(docFile);
            fos = new FileOutputStream(pdfFile);
            Docx4J.toPDF(pkg, fos);
        } finally {
            if (fos != null){
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}