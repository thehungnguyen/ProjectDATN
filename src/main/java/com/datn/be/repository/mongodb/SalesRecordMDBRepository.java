package com.datn.be.repository.mongodb;

import com.datn.be.model.entity.mongodb.SalesRecordMDB;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SalesRecordMDBRepository extends MongoRepository<SalesRecordMDB, String> {
}
