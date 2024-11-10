package com.datn.be.controller.mongodb;

import com.datn.be.model.dto.request.SalesRecordRequest;
import com.datn.be.model.dto.response.SalesRecordResponse;
import com.datn.be.serivce.mongodb.SalesRecordMDBService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sales-mdb")
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

}
