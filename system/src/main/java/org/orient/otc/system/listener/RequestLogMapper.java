package org.orient.otc.system.listener;

import org.orient.otc.system.entity.RequestLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface RequestLogMapper  extends ElasticsearchRepository<RequestLog,String> {
}
