package com.datn.be.serivce.mongodb;

import com.datn.be.model.dto.request.SalesRecordRequest;
import com.datn.be.model.dto.response.SalesRecordResponse;
import com.datn.be.model.entity.mongodb.SalesRecordMDB;
import com.datn.be.repository.mongodb.SalesRecordMDBRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class SalesRecordMDBService {
    private final SalesRecordMDBRepository repository;
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    // Tạo SalesRecord
    public ResponseEntity<SalesRecordResponse> saveSalesRecord(SalesRecordRequest request) {
        SalesRecordMDB newRecord = build(request);

        repository.save(newRecord);

        // Trả về ResponseEntity với HTTP status OK và body là SalesRecordResponse
        return ResponseEntity.status(HttpStatus.CREATED).body(buildResponse(newRecord));
    }

    // Cập nhật SalesRecord
    public ResponseEntity<SalesRecordResponse> updateSalesRecord(String id, SalesRecordRequest request) {
        Optional<SalesRecordMDB> salesRecordMDBOptional = repository.findById(id);
        if (salesRecordMDBOptional.isPresent()) {
            SalesRecordMDB existsRecord = salesRecordMDBOptional.get();
            update(existsRecord, request);

            // Lưu lại bản ghi sau khi cập nhật
            repository.save(existsRecord);

            // Trả về ResponseEntity với HTTP status OK và body là SalesRecordResponse
            return ResponseEntity.ok(buildResponse(existsRecord));
        } else {
            // Nếu không tìm thấy bản ghi, trả về ResponseEntity với HTTP status NOT_FOUND
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Xóa SalesRecord theo ID
    public ResponseEntity<String> deleteSalesRecord(String id) {
        Optional<SalesRecordMDB> salesRecordMDBOptional = repository.findById(id);
        if (salesRecordMDBOptional.isPresent()) {
            repository.deleteById(id);
            // Trả về ResponseEntity với HTTP status OK và thông báo thành công
            return ResponseEntity.ok("Delete successfully");
        } else {
            // Nếu không tìm thấy bản ghi, trả về ResponseEntity với HTTP status NOT_FOUND
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Record not found");
        }
    }

    private SalesRecordMDB build(SalesRecordRequest request) {

        SalesRecordMDB newRecord = new SalesRecordMDB();

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

    private void update(SalesRecordMDB existsRecord, SalesRecordRequest request) {
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

    private SalesRecordResponse buildResponse(SalesRecordMDB existsRecord) {
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
