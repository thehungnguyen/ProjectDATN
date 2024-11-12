package com.datn.be.mongodb.repository;

import com.datn.be.mongodb.model.entity.SalesRecordMDB;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface SalesRecordMDBRepository extends MongoRepository<SalesRecordMDB, String> {
    // Truy vấn tìm theo 1 trường: Country hoặc Order ID hoặc Item Type
    List<SalesRecordMDB> findByCountry(String country, Pageable pageable);

    List<SalesRecordMDB> findByOrderID(String orderID);

    List<SalesRecordMDB> findByItemType(String itemType, Pageable pageable);

    // Truy vấn tìm theo 2 trường: Country và Item Type
    List<SalesRecordMDB> findByCountryAndItemType(String country, String itemType, Pageable pageable);

    // Truy vấn tìm Order Date hoặc Ship Date trong khoảng Start Date và End Date
    List<SalesRecordMDB> findByOrderDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    List<SalesRecordMDB> findByShipDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
}
