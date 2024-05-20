package org.orient.otc.quote.util;

import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.pdf.PdfDocument;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.orient.otc.quote.config.TemplateConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 复杂word模板转换pdf (免费版本只能免费转前3页)
 * 1. 按节转换, 在制作模板时,需要在每一页末尾加上分节符 ,
 * 2. 根据分节符把word拆分成单独的word ,
 * 3. 把一个一个word转成pdf
 * 4. 把多个pdf合并成一个pdf
 */
@Component
@Slf4j
public class SpireUtil {
    @Autowired
    TemplateConfig templateConfig;
    /**
     * word 页数小于等于3
     * word 转pdf 免费jar只能转换前三页
     * @param inputFilePath     输入文件路径
     * @param outputFilePath    输出文件路径
     */
    public void wordToPdf(String inputFilePath,String outputFilePath){
        Document doc = new Document();
        doc.loadFromFile(inputFilePath, FileFormat.Docx);
        doc.saveToFile(outputFilePath,FileFormat.PDF);
    }
    /**
     * word 页数大于3
     * word 转pdf 免费jar只能转换前三页
     * @param inputFilePath 文件入参
     */
    public void wordToPdf2(String inputFilePath,String pdfFilePath) throws Exception {
        long s = System.currentTimeMillis();
        File inputFile = new File(inputFilePath);
        // word拆分出来的文件存放目录
        String splitFilePath = inputFile.getParent()+File.separator+"split/";
        File parentFile = new File(splitFilePath);
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        // 拆分word
        List<String> docxPathList = splitWordBySections(inputFilePath,splitFilePath);
        // 拆分出来的word转pdf
        List<String> pdfPathList = word2pdfBatch(docxPathList);
        // 合并pdf
        mergePdfFiles(pdfPathList,pdfFilePath);
        long e = System.currentTimeMillis();
        log.info("复杂模板的word拆分转成pdf用时="+(e-s));
        delteComparessFile(parentFile);
    }

    // 删除文件
    public void delteComparessFile(File dir){
        File[] files = dir.listFiles();
        if(files != null){
            for(File f:files) {
                if (f.isDirectory()){
                    delteComparessFile(f);
                } else {
                    // 先设置文件的可写权限
                    f.setWritable(true);
                    boolean b = FileUtils.deleteQuietly(f);
                    log.info("删除文件是否成功="+b+"文件路径"+f.getPath());
                }
            }
        }
        dir.setWritable(true);
        dir.delete(); // 删除文件夹
    }

    /**
     * 批量转换
     * @param inputFilePathList  拆分的word文件列表
     * @return  返回PDF文件路径
     */
    public List<String> word2pdfBatch(List<String> inputFilePathList){
        List<String> pdfPathList = new ArrayList<>();
        Document doc = new Document();
        for (String inputFilePath : inputFilePathList) {
            doc.loadFromFile(inputFilePath, FileFormat.Docx);
            String pdfPath = inputFilePath.replaceAll(".docx",".pdf");
            pdfPathList.add(pdfPath);
            // 获取操作系统名称 Windows xxx
            String osName = System.getProperty("os.name");
            // 设置字体目录
            if (!(osName != null && (osName.toUpperCase().contains("Windows".toUpperCase()) || osName.toUpperCase().contains("mac".toUpperCase())))) { // 非windows或mac系统 视为linux系统
                String fontsPath = templateConfig.getTradeConfirmBookPath()+"fonts";
                PdfDocument.setCustomFontsFolders(fontsPath);
            }

            doc.saveToFile(pdfPath,FileFormat.PDF);
        }
        return pdfPathList;
    }

    /**
     * 按节拆分,每二个分节符拆分一次(一个分节符表示一页)
     * 分节符设置的时候，选择下一页，不要选择连续，否则转换出来的pdf可能有问题
     * 分节符位置放在每一页的最后，多余的分节符要删除（部分分节符在操作过程中发生了格式变化，按住ctrl左键选中分节符，再delete）
     * @param inputFilePath     输入文件路径
     * @param outputFilePath    输出文件路径
     */
    public List<String> splitWordBySections(String inputFilePath,String outputFilePath) {
        List<String> pathList = new ArrayList<>(); // 存放拆分之后的文件路径
        Document document = new Document();
        document.loadFromFile(inputFilePath, FileFormat.Docx);
        int pages = document.getSections().getCount();
        log.info("转换word的分节总数="+pages);
        //声明新的Document对象
        Document newWord = new Document();
        File file = new File(outputFilePath);
        if (!file.exists()){
            file.mkdirs();
        }

        /*
         * 遍历源文档中的节 (一个分解符表示一页) , 每二个分节符拆分一次
         */
        int count = 0;
        int fileIndex = 0;
        for (int i = 0; i < document.getSections().getCount(); i++){
            //将源文档中的指定节复制到新文档
            newWord.getSections().add(document.getSections().get(i).deepClone());
            count++;
            if (count>=2) {
                //保存新文档到指定文件夹
                String outPath= outputFilePath+"拆分-"+fileIndex+".docx";
                pathList.add(outPath);
                newWord.saveToFile(outPath);
                fileIndex++;
                count = 1;
                //初始化新的Document对象
                newWord = new Document();

            }
        }
        if (count!=1) { // 剩余文档
            //保存新文档到指定文件夹
            String outPath= outputFilePath+"拆分-"+fileIndex+".docx";
            pathList.add(outPath);
            newWord.saveToFile(outPath);
        }

        return pathList;
    }

    /**
     * pdf合并
     * @param files         文件列表
     * @param newfile       新文件名
     * @throws Exception    异常
     */
    public void mergePdfFiles(List<String> files, String newfile) throws Exception{
        com.itextpdf.text.Document document = new com.itextpdf.text.Document(new PdfReader(files.get(0)).getPageSize(1));
        PdfCopy copy = new PdfCopy(document, Files.newOutputStream(Paths.get(newfile)));
        document.open();
        for (String file : files) {
            PdfReader reader = new PdfReader(file);
            int n = reader.getNumberOfPages();
            for (int j = 1; j <= n; j++) {
                document.newPage();
                PdfImportedPage page = copy.getImportedPage(reader, j);
                copy.addPage(page);
            }
        }
        document.close();
    }

}
