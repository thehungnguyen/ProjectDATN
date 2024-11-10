package com.datn.be.repository.es;

import com.datn.be.model.entity.es.SalesRecordES;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SalesRecordESRepository extends ElasticsearchRepository<SalesRecordES, String> {
}
