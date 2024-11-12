package com.datn.be.mongodb.service;

import com.datn.be.model.dto.request.SalesRecordRequest;
import com.datn.be.model.dto.response.SalesRecordResponse;
import com.datn.be.mongodb.model.entity.SalesRecordMDB;
import com.datn.be.mongodb.repository.SalesRecordMDBRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class SalesRecordMDBService {
    private final SalesRecordMDBRepository repository;
    DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private final Pageable pageable = PageRequest.of(0, 10);

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

    // Get SaleRecord by OrderID
    public ResponseEntity<SalesRecordResponse> getByOrderID(String id) {
        List<SalesRecordMDB> records = repository.findByOrderID(id);
        if (!records.isEmpty()) {
            SalesRecordMDB existsRecord = records.stream()
                    .filter(s -> s.getOrderID().equals(id))
                    .findFirst()
                    .orElseThrow(null);

            return ResponseEntity.ok(buildResponse(existsRecord));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    public ResponseEntity<List<SalesRecordResponse>> getByCountry(String country) {
        List<SalesRecordMDB> records = repository.findByCountry(country, pageable);
        if (!records.isEmpty()) {
            List<SalesRecordResponse> responseList = records.stream()
                    .map(this::buildResponse)
                    .toList();

            return ResponseEntity.ok(responseList);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    public ResponseEntity<List<SalesRecordResponse>> getByItemType(String itemType) {
        List<SalesRecordMDB> records = repository.findByItemType(itemType, pageable);
        if (!records.isEmpty()) {
            List<SalesRecordResponse> responseList = records.stream()
                    .map(this::buildResponse)
                    .toList();

            return ResponseEntity.ok(responseList);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Truy vấn theo Country và Item Type và giới hạn số lượng trả về là 5
    public ResponseEntity<List<SalesRecordResponse>> getByCountryAndItemType(String country, String itemType) {
        List<SalesRecordMDB> records = repository.findByCountryAndItemType(country, itemType, pageable);
        if (!records.isEmpty()) {
            List<SalesRecordResponse> responseList = records.stream()
                    .map(this::buildResponse)
                    .toList();

            return ResponseEntity.ok(responseList);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Truy vấn theo Order Date trong khoảng và giới hạn số lượng trả về là 5
    public ResponseEntity<List<SalesRecordResponse>> getByOrderDateBetween(String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate, FORMATTER);
        LocalDate end = LocalDate.parse(endDate, FORMATTER);
        List<SalesRecordMDB> records = repository.findByOrderDateBetween(start, end, pageable);
        if (!records.isEmpty()) {
            List<SalesRecordResponse> responseList = records.stream()
                    .map(this::buildResponse)
                    .toList();

            return ResponseEntity.ok(responseList);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Truy vấn theo Ship Date trong khoảng và giới hạn số lượng trả về là 5
    public ResponseEntity<List<SalesRecordResponse>> getByShipDateBetween(String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate, FORMATTER);
        LocalDate end = LocalDate.parse(endDate, FORMATTER);
        List<SalesRecordMDB> records = repository.findByShipDateBetween(start, end, pageable);
        if (!records.isEmpty()) {
            List<SalesRecordResponse> responseList = records.stream()
                    .map(this::buildResponse)
                    .toList();

            return ResponseEntity.ok(responseList);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    private SalesRecordMDB build(SalesRecordRequest request) {

        SalesRecordMDB newRecord = new SalesRecordMDB();

        newRecord.setRegion(request.getRegion());
        newRecord.setCountry(request.getCountry());
        newRecord.setItemType(request.getItemType());
        newRecord.setSalesChannel(request.getSalesChannel());
        newRecord.setOrderPriority(request.getOrderPriority());

        newRecord.setOrderDate(parseDate(request.getOrderDate()));
        newRecord.setOrderID(generOrderId());
        newRecord.setShipDate(parseDate(request.getShipDate()));

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

        existsRecord.setOrderDate(parseDate(request.getOrderDate()));
        existsRecord.setShipDate(parseDate(request.getShipDate()));

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
                .itemType(existsRecord.getItemType())
                .salesChannel(existsRecord.getSalesChannel())
                .orderPriority(existsRecord.getOrderPriority())
                .orderDate(formatDate(existsRecord.getOrderDate()))
                .orderId(existsRecord.getOrderID())
                .shipDate(formatDate(existsRecord.getShipDate()))
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

    private LocalDate parseDate(String date) {
        return LocalDate.parse(date, FORMATTER);
    }

    private String formatDate(LocalDate date) {
        return date != null ? date.format(FORMATTER) : null;
    }
}
