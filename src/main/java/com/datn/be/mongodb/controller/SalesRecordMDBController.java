package com.datn.be.mongodb.controller;

import com.datn.be.model.dto.request.SalesRecordRequest;
import com.datn.be.model.dto.response.SalesRecordResponse;
import com.datn.be.mongodb.service.SalesRecordMDBService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sales-mdb")
@Tag(name = "MongoDB")
public class SalesRecordMDBController {
    private final SalesRecordMDBService service;

    // Tạo mới SalesRecord
    @PostMapping
    public ResponseEntity<SalesRecordResponse> createSalesRecord(@RequestBody SalesRecordRequest request) {
        return service.saveSalesRecord(request);
    }

    // Cập nhật SalesRecord theo ID
    @PatchMapping("/{id}")
    public ResponseEntity<SalesRecordResponse> updateSalesRecord(@PathVariable String id, @RequestBody SalesRecordRequest request) {
        return service.updateSalesRecord(id, request);
    }

    // Xóa SalesRecord theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSalesRecord(@PathVariable String id) {
        return service.deleteSalesRecord(id);
    }

    @GetMapping("/orderId")
    public ResponseEntity<SalesRecordResponse> getByOrderID(
            @RequestParam(value = "orderID") String orderID
    ) {
        return service.getByOrderID(orderID);
    }

    @GetMapping("/country")
    public ResponseEntity<List<SalesRecordResponse>> getByCountry(
            @RequestParam(value = "country") String country
    ) {
        return service.getByCountry(country);
    }

    @GetMapping("/itemType")
    public ResponseEntity<List<SalesRecordResponse>> getByItemType(
            @RequestParam(value = "itemType") String itemType
    ) {
        return service.getByItemType(itemType);
    }

    // Endpoint để lấy dữ liệu theo Country và Item Type, giới hạn 5 bản ghi
    @GetMapping("/country-itemType")
    public ResponseEntity<List<SalesRecordResponse>> getByCountryAndItemType(
            @RequestParam String country,
            @RequestParam String itemType) {
        return service.getByCountryAndItemType(country, itemType);
    }

    // Endpoint để lấy dữ liệu theo Order Date trong khoảng và giới hạn 5 bản ghi
    @GetMapping("/range-order-date")
    public ResponseEntity<List<SalesRecordResponse>> getByOrderDateBetween(
            @RequestParam(value = "startDate") String startDate,
            @RequestParam(value = "endDate") String endDate
    ) {
        return service.getByOrderDateBetween(startDate, endDate);
    }

    // Endpoint để lấy dữ liệu theo Ship Date trong khoảng và giới hạn 5 bản ghi
    @GetMapping("/range-ship-date")
    public ResponseEntity<List<SalesRecordResponse>> getByShipDateBetween(
            @RequestParam(value = "startDate") String startDate,
            @RequestParam(value = "endDate") String endDate
    ) {
        return service.getByShipDateBetween(startDate, endDate);
    }
}
