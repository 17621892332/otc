package org.orient.otc.system.feign;

import org.orient.otc.api.system.feign.StructureClient;
import org.orient.otc.api.system.vo.StructureInfoVO;
import org.orient.otc.system.service.StructureService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author dzrh
 */
@RestController
@RequestMapping("/structure")
public class StructureFeignController implements StructureClient {
    @Resource
    private StructureService structureService;


    @Override
    public List<StructureInfoVO> getStructureInfoList() {
        return structureService.getStructureInfoList();
    }
}
