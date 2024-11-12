package com.datn.be.elasticsearch.repository;

import com.datn.be.elasticsearch.model.entity.SalesRecordES;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.time.LocalDate;
import java.util.List;

public interface SalesRecordESRepository extends ElasticsearchRepository<SalesRecordES, String> {
    // Truy vấn tìm theo 1 trường: Country hoặc Order ID hoặc Item Type
    List<SalesRecordES> findByCountry(String country, Pageable pageable);

    List<SalesRecordES> findByOrderID(String orderID);

    List<SalesRecordES> findByItemType(String itemType, Pageable pageable);

    // Truy vấn tìm theo 2 trường: Country và Item Type
    List<SalesRecordES> findByCountryAndItemType(String country, String itemType, Pageable pageable);

    // Truy vấn tìm Order Date hoặc Ship Date trong khoảng Start Date và End Date
    List<SalesRecordES> findByOrderDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    List<SalesRecordES> findByShipDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

}
