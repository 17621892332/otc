package org.orient.otc.quote.util;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;

/**
 * word导出工具类
 * @author dzrh
 */
@Slf4j
@Component
public class WordUtil {

    /**
     * 导出确认书
     * @param config 填充配置
     * @param map 填充数据
     * @param templatePath   模板路径
     * @return 返回输出的文件路径
     *
     * @throws Exception 异常
     */
    public ByteArrayInputStream exportInputStream(String templatePath, Configure config,Object map) throws Exception{
        XWPFTemplate template = XWPFTemplate.compile(templatePath, config).render(map);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        template.writeAndClose(outputStream);

        return new ByteArrayInputStream(outputStream.toByteArray());
    }

}
