package com.datn.be.serivce.es;

import com.datn.be.model.dto.request.SalesRecordRequest;
import com.datn.be.model.dto.response.SalesRecordResponse;
import com.datn.be.model.entity.es.SalesRecordES;
import com.datn.be.repository.es.SalesRecordESRepository;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.FuzzyQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class SalesRecordESService {

    private final SalesRecordESRepository repository;
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    // Tạo SalesRecord cho Elasticsearch
    public ResponseEntity<SalesRecordResponse> saveSalesRecord(SalesRecordRequest request) {
        SalesRecordES newRecord = build(request);
        repository.save(newRecord);

        // Trả về ResponseEntity với HTTP status OK và body là response
        return ResponseEntity.ok(buildResponse(newRecord));
    }

    // Cập nhật SalesRecord cho Elasticsearch
    public ResponseEntity<SalesRecordResponse> updateSalesRecord(String id, SalesRecordRequest request) {
        Optional<SalesRecordES> salesRecordESOptional = repository.findById(id);
        if (salesRecordESOptional.isPresent()) {
            SalesRecordES existsRecord = salesRecordESOptional.get();
            update(existsRecord, request);
            repository.save(existsRecord); // Lưu lại sau khi cập nhật

            // Trả về ResponseEntity với HTTP status OK và body là response
            return ResponseEntity.ok(buildResponse(existsRecord));
        } else {
            // Nếu không tìm thấy record, trả về ResponseEntity với HTTP status NOT_FOUND
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Xóa SalesRecord theo ID trong Elasticsearch
    public ResponseEntity<String> deleteSalesRecord(String id) {
        Optional<SalesRecordES> salesRecordESOptional = repository.findById(id);
        if (salesRecordESOptional.isPresent()) {
            repository.deleteById(id);
            // Trả về ResponseEntity với HTTP status OK và thông báo thành công
            return ResponseEntity.ok("Delete successfully");
        } else {
            // Nếu không tìm thấy record, trả về ResponseEntity với HTTP status NOT_FOUND
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Record not found");
        }
    }

    private SalesRecordES build(SalesRecordRequest request) {
        SalesRecordES newRecord = new SalesRecordES();

        newRecord.setRegion(request.getRegion());
        newRecord.setCountry(request.getCountry());
        newRecord.setItemType(request.getItemType());
        newRecord.setSalesChannel(request.getSalesChannel());
        newRecord.setOrderPriority(request.getOrderPriority());

        LocalDate orderDate = LocalDate.parse(request.getOrderDate(), FORMATTER);
        newRecord.setOrderDate(orderDate);

        newRecord.setOrderId(generOrderId());

        LocalDate shipDate = LocalDate.parse(request.getShipDate(), FORMATTER);
        newRecord.setShipDate(shipDate);

        newRecord.setUnitsSold(request.getUnitsSold());
        newRecord.setUnitPrice(request.getUnitPrice());
        newRecord.setUnitCost(request.getUnitCost());

        newRecord.calculateTotalRevenue();
        newRecord.calculateTotalCost();
        newRecord.calculateTotalProfit();

        return newRecord;
    }

    private void update(SalesRecordES existsRecord, SalesRecordRequest request) {
        existsRecord.setRegion(request.getRegion());
        existsRecord.setCountry(request.getCountry());
        existsRecord.setItemType(request.getItemType());
        existsRecord.setSalesChannel(request.getSalesChannel());
        existsRecord.setOrderPriority(request.getOrderPriority());

        LocalDate orderDate = LocalDate.parse(request.getOrderDate(), FORMATTER);
        existsRecord.setOrderDate(orderDate);

        LocalDate shipDate = LocalDate.parse(request.getShipDate(), FORMATTER);
        existsRecord.setShipDate(shipDate);

        existsRecord.setUnitsSold(request.getUnitsSold());
        existsRecord.setUnitPrice(request.getUnitPrice());
        existsRecord.setUnitCost(request.getUnitCost());

        existsRecord.calculateTotalRevenue();
        existsRecord.calculateTotalCost();
        existsRecord.calculateTotalProfit();
    }

    private SalesRecordResponse buildResponse(SalesRecordES existsRecord) {
        return SalesRecordResponse.builder()
                .id(existsRecord.getId())
                .region(existsRecord.getRegion())
                .country(existsRecord.getCountry())
                .salesChannel(existsRecord.getSalesChannel())
                .orderPriority(existsRecord.getOrderPriority())
                .orderDate(String.valueOf(existsRecord.getOrderDate()))
                .orderId(existsRecord.getOrderId())
                .shipDate(String.valueOf(existsRecord.getShipDate()))
                .unitsSold(existsRecord.getUnitsSold())
                .unitPrice(existsRecord.getUnitPrice())
                .unitCost(existsRecord.getUnitCost())
                .totalRevenue(existsRecord.getTotalRevenue())
                .totalCost(existsRecord.getTotalCost())
                .totalProfit(existsRecord.getTotalProfit())
                .build();
    }

    private String generOrderId() {
        Random random = new Random();
        return String.format("%09d", random.nextInt(1_000_000_000)); // Tạo số ngẫu nhiên 9 chữ số
    }
}
