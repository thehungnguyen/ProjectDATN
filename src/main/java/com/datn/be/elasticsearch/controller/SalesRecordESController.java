package com.datn.be.elasticsearch.controller;

import com.datn.be.elasticsearch.model.response.aggregation.DateHistogramAggResponse;
import com.datn.be.elasticsearch.model.response.aggregation.TermAggResponse;
import com.datn.be.elasticsearch.service.QueryAggregationEsService;
import com.datn.be.elasticsearch.service.QuerySearchEsService;
import com.datn.be.model.dto.request.SalesRecordRequest;
import com.datn.be.model.dto.response.SalesRecordResponse;
import com.datn.be.elasticsearch.service.SalesRecordESService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sales-es")
@Tag(name = "Elasticsearch")
public class SalesRecordESController {
    private final SalesRecordESService salesRecordESService;
    private final QuerySearchEsService querySearchEsService;
    private final QueryAggregationEsService queryAggregationEsService;

    // Tạo mới SalesRecord
    @PostMapping
    public ResponseEntity<SalesRecordResponse> createSalesRecord(@RequestBody SalesRecordRequest request) {
        return salesRecordESService.saveSalesRecord(request);
    }

    // Cập nhật SalesRecord theo ID
    @PatchMapping("/{id}")
    public ResponseEntity<SalesRecordResponse> updateSalesRecord(@PathVariable String id, @RequestBody SalesRecordRequest request) {
        return salesRecordESService.updateSalesRecord(id, request);
    }

    // Xóa SalesRecord theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSalesRecord(@PathVariable String id) {
        return salesRecordESService.deleteSalesRecord(id);
    }

//    @GetMapping("/orderId")
//    public ResponseEntity<SalesRecordResponse> getByOrderID(
//            @RequestParam(value = "orderId") String orderId
//    ) {
//        return service.getByOrderId(orderId);
//    }
//
//    @GetMapping("/country")
//    public ResponseEntity<List<SalesRecordResponse>> getByCountry(
//            @RequestParam(value = "country") String country
//    ) {
//        return service.getByCountry(country);
//    }
//
//    @GetMapping("/itemType")
//    public ResponseEntity<List<SalesRecordResponse>> getByItemType(
//            @RequestParam(value = "itemType") String itemType
//    ) {
//        return service.getByItemType(itemType);
//    }

    // Endpoint để lấy dữ liệu theo Country và Item Type, giới hạn 5 bản ghi
    @GetMapping("/country-itemType")
    public ResponseEntity<List<SalesRecordResponse>> getByCountryAndItemType(
            @RequestParam String country,
            @RequestParam String itemType) {
        return salesRecordESService.getByCountryAndItemType(country, itemType);
    }

    // Endpoint để lấy dữ liệu theo Order Date trong khoảng và giới hạn 5 bản ghi
    @GetMapping("/range-order-date")
    public ResponseEntity<List<SalesRecordResponse>> getByOrderDateBetween(
            @RequestParam(value = "startDate") String startDate,
            @RequestParam(value = "endDate") String endDate
    ) {
        return salesRecordESService.getByOrderDateBetween(startDate, endDate);
    }

    // Endpoint để lấy dữ liệu theo Ship Date trong khoảng và giới hạn 5 bản ghi
    @GetMapping("/range-ship-date")
    public ResponseEntity<List<SalesRecordResponse>> getByShipDateBetween(
            @RequestParam(value = "startDate") String startDate,
            @RequestParam(value = "endDate") String endDate
    ) {
        return salesRecordESService.getByShipDateBetween(startDate, endDate);
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////
    @GetMapping("/match-query")
    public ResponseEntity<List<SalesRecordResponse>> matchQuery(
            @RequestParam(value = "field") String field,
            @RequestParam(value = "value") String value
    ) throws IOException {
        return querySearchEsService.matchQuery(field, value);
    }

    @GetMapping("/term-query")
    public ResponseEntity<List<SalesRecordResponse>> termQuery(
            @RequestParam(value = "field") String field,
            @RequestParam(value = "value") String value
    ) throws IOException {
        return querySearchEsService.termQuery(field, value);
    }

    @GetMapping("/bool-query")
    public ResponseEntity<List<SalesRecordResponse>> boolQuery(
            @RequestParam(value = "field1") String field1,
            @RequestParam(value = "value1") String value1,
            @RequestParam(value = "field2") String field2,
            @RequestParam(value = "value2") String value2,
            @RequestParam(value = "field3") String field3,
            @RequestParam(value = "value3") String value3
    ) throws IOException {
        return querySearchEsService.boolQuery(field1, value1, field2, value2, field3, value3);
    }

    @GetMapping("/range-query")
    public ResponseEntity<List<SalesRecordResponse>> rangeQuery(
            @RequestParam(value = "field") String field,
            @RequestParam(value = "fromValue") int fromValue,
            @RequestParam(value = "toValue") int toValue
    ) throws IOException {
        return querySearchEsService.rangeQuery(field, fromValue, toValue);
    }

    @GetMapping("/wildcard-query")
    public ResponseEntity<List<SalesRecordResponse>> wildcardQuery(
            @RequestParam(value = "field") String field,
            @RequestParam(value = "pattern") String pattern
    ) throws IOException {
        return querySearchEsService.wildcardQuery(field, pattern);
    }

    @GetMapping("/prefix-query")
    public ResponseEntity<List<SalesRecordResponse>> prefixQuery(
            @RequestParam(value = "field") String field,
            @RequestParam(value = "prefix") String prefix
    ) throws IOException {
        return querySearchEsService.prefixQuery(field, prefix);
    }

    @GetMapping("/fuzzy-query")
    public ResponseEntity<List<SalesRecordResponse>> fuzzyQuery(
            @RequestParam(value = "field") String field,
            @RequestParam(value = "value") String value
    ) throws IOException {
        return querySearchEsService.fuzzyQuery(field, value);
    }

    @GetMapping("/match-phrase-query")
    public ResponseEntity<List<SalesRecordResponse>>matchPhraseQuery(
            @RequestParam(value = "field") String field,
            @RequestParam(value = "value") String value,
            @RequestParam(value = "numOfSlop") int numOfSlop
    ) throws IOException {
        return querySearchEsService.matchPhraseQuery(field, value, numOfSlop);
    }

    @GetMapping("/multi-match-query")
    public ResponseEntity<List<SalesRecordResponse>>multiMatchQuery(
            @RequestParam(value = "value") String value,
            @RequestParam(value = "fields") String[] fields
    ) throws IOException {
        return querySearchEsService.multiMatchQuery(value, fields);
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////
    @GetMapping("/term-agg")
    public ResponseEntity<List<TermAggResponse>> termAgg(
            @RequestParam(value = "field") String field
    ) throws IOException {
        return queryAggregationEsService.termAgg(field);
    }

    @GetMapping("/date-histogram-agg")
    public ResponseEntity<DateHistogramAggResponse> dateHistogramAgg(
            @RequestParam(value = "date") String date
    ) throws IOException {
        return queryAggregationEsService.dateHistogramAgg(date);
    }
}
