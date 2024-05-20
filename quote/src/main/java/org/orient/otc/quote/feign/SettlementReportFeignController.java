package org.orient.otc.quote.feign;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.orient.otc.api.quote.dto.BuildSettlementReportDTO;
import org.orient.otc.api.quote.feign.SettlementReportClient;
import org.orient.otc.api.quote.vo.SettlementReportFileVO;
import org.orient.otc.common.core.util.FileUtil;
import org.orient.otc.common.security.dto.AuthorizeInfo;
import org.orient.otc.common.security.util.ThreadContext;
import org.orient.otc.quote.dto.settlementReport.SettlementReportDTO;
import org.orient.otc.quote.enums.SettlemenReportSheetEnum;
import org.orient.otc.quote.service.SettlementReportService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 生成结算报告附件
 * @author chengqiang
 */
@RestController
@RequestMapping(value = "settlementReport")
@Slf4j
public class SettlementReportFeignController implements SettlementReportClient {
    @Autowired
    SettlementReportService settlementReportService;
    @Autowired
    HttpServletResponse response;
    @Override
    public SettlementReportFileVO getSettlementReportTempFileByClient(BuildSettlementReportDTO dto) {
        ThreadContext.setAuthorizeInfo(JSON.parseObject(dto.getAuthorizeInfo(), AuthorizeInfo.class));
        SettlementReportFileVO returnVO = new SettlementReportFileVO();
        SettlementReportDTO settlementReportDTO = new SettlementReportDTO();
        BeanUtils.copyProperties(dto,settlementReportDTO);
        Set<SettlemenReportSheetEnum> reportTypeSet = new HashSet<>();
        Set<String> reportTypeSetTemp = dto.getReportTypeSet();
        for (String item : reportTypeSetTemp) {
            for (SettlemenReportSheetEnum enumItem : SettlemenReportSheetEnum.values()){
                if (enumItem.name().equals(item)) {
                    reportTypeSet.add(enumItem);
                }
            }
        }
        settlementReportDTO.setReportTypeSet(reportTypeSet);
        settlementReportDTO.setSendMailFlag(true);
        try {
            log.info("-------生成结算报告入参----"+ JSON.toJSONString(dto));
            settlementReportService.export(settlementReportDTO,response); // 调用生成结算报告方法
            // feign返回临时文件
            File tempFile = settlementReportDTO.getTempFile();
            if (tempFile != null) {
                log.info("结算报告-发送邮件生成的结算报告附件路径=" + tempFile.getPath());
            }
            returnVO.setSettlementReportTempFileByte(FileUtil.file2MultipartFile(tempFile).getBytes());
            returnVO.setTempFileName(settlementReportDTO.getTempFileName());
            deleteSettlementReportTempFile(tempFile);
         } catch (IOException e) {
            log.error("获取邮件中生成的结算报告失败="+e.getMessage());
        }
        return returnVO;
    }

    /**
     * 删除结算报告生成的临时文件
     * @param file
     */
    public void deleteSettlementReportTempFile(File file){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                String path = file.getPath();
                boolean b = file.delete();
                log.info("删除结算报告创建的临时文件="+b+" 临时文件目录:"+path);
            }
        }, 1000*60*5); // 300秒之后删除临时文件, 不要立刻删除, 后面发送邮件使用
    }
}
