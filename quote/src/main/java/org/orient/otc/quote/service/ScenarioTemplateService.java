package org.orient.otc.quote.service;

import org.orient.otc.quote.dto.scenario.ScenarioTemplateAddDTO;
import org.orient.otc.quote.dto.scenario.ScenarioTemplateDeleteDTO;
import org.orient.otc.quote.dto.scenario.ScenarioTemplateSelectDTO;
import org.orient.otc.quote.dto.scenario.ScenarioTemplateUpdateDTO;
import org.orient.otc.quote.entity.ScenarioTemplate;

import java.util.List;

public interface ScenarioTemplateService {
    String save(ScenarioTemplateAddDTO dto);

    ScenarioTemplate getById(ScenarioTemplateSelectDTO dto);

    String delete(ScenarioTemplateDeleteDTO dto);

    List<ScenarioTemplate> selectByList();
}
