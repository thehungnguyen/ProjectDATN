package com.datn.be.elasticsearch.service;

import com.datn.be.elasticsearch.model.entity.SalesRecordES;
import com.datn.be.model.dto.response.SalesRecordResponse;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuerySearchEsService {
    private final RestHighLevelClient client;
    private static final String INDEX_NAME = "sales-records";
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    // Match Query
    public ResponseEntity<List<SalesRecordResponse>> matchQuery(String field, String value) throws IOException {
        QueryBuilder query = QueryBuilders.matchQuery(field, value);
        return executeSearch(query, 0, 10);
    }

    // Term Query
    public ResponseEntity<List<SalesRecordResponse>> termQuery(String field, String value) throws IOException {
        QueryBuilder query = QueryBuilders.termQuery(field, value);
        return executeSearch(query, 0, 10);
    }

    // Bool Query
    public ResponseEntity<List<SalesRecordResponse>> boolQuery(String f1, String v1, String f2, String v2, String f3, String v3) throws IOException {
        QueryBuilder query = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery(f1, v1))
                .should(QueryBuilders.termQuery(f2, v2))
                .mustNot(QueryBuilders.termQuery(f3, v3));

        return executeSearch(query, 0, 10);
    }

    // Range Query
    public ResponseEntity<List<SalesRecordResponse>> rangeQuery(String field, int fromValue, int toValue) throws IOException {
        QueryBuilder query = QueryBuilders.rangeQuery(field)
                .from(fromValue)
                .to(toValue)
                .includeLower(true)
                .includeUpper(true);

        return executeSearch(query, 0, 10);
    }

    // Wildcard Query
    public ResponseEntity<List<SalesRecordResponse>> wildcardQuery(String field, String pattern) throws IOException {
        QueryBuilder query = QueryBuilders.wildcardQuery(field, pattern);
        return executeSearch(query, 0, 10);
    }

    // Prefix Query
    public ResponseEntity<List<SalesRecordResponse>> prefixQuery(String field, String prefix) throws IOException {
        QueryBuilder query = QueryBuilders.prefixQuery(field, prefix);

        return executeSearch(query, 0, 10);
    }

    // Fuzzy Query
    public ResponseEntity<List<SalesRecordResponse>> fuzzyQuery(String field, String value) throws IOException {
        // Tạo Fuzzy Query để tìm kiếm gần đúng
        QueryBuilder query = QueryBuilders.fuzzyQuery(field, value);

        return executeSearch(query, 0, 10);  // Gọi phương thức executeSearch với giới hạn từ 0 đến 10 bản ghi
    }

    // Match Phrase Query
    public ResponseEntity<List<SalesRecordResponse>> matchPhraseQuery(String field, String value, int numOfSlop) throws IOException {
        // Tạo Match Phrase Query
        QueryBuilder query = QueryBuilders.matchPhraseQuery(field, value).slop(numOfSlop);

        return executeSearch(query, 0, 10);
    }

    // Multi-Match Query
    public ResponseEntity<List<SalesRecordResponse>> multiMatchQuery(String value, String... fields) throws IOException {
        QueryBuilder query = QueryBuilders.multiMatchQuery(value, fields);

        return executeSearch(query, 0, 10);
    }

    // Thực  hiện tìm kiếm
    private ResponseEntity<List<SalesRecordResponse>> executeSearch(QueryBuilder query, int from, int size) throws IOException {
        SearchRequest searchRequest = createSearchRequest(query, from, size);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        List<SalesRecordResponse> responseList = parseSearchResponse(searchResponse);
        return ResponseEntity.ok(responseList);
    }

    // Tạo SearchRequest
    private SearchRequest createSearchRequest(QueryBuilder query, int from, int size) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(query);
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(size);

        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
        searchRequest.source(searchSourceBuilder);
        return searchRequest;
    }

    // Chuyển danh sách kết quả tìm kiếm được thành danh sách DTO
    private List<SalesRecordResponse> parseSearchResponse(SearchResponse searchResponse) {
        List<SalesRecordResponse> responseList = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits()) {
            SalesRecordES record = convertHitToSalesRecord(hit);
            SalesRecordResponse response = buildResponse(record);
            responseList.add(response);
        }
        return responseList;
    }

    // Convert hit to model
    private SalesRecordES convertHitToSalesRecord(SearchHit hit) {
        // Tạo đối tượng SalesRecordES mới
        SalesRecordES record = new SalesRecordES();

        // Lấy chuỗi ngày tháng orderDate và shipDate từ Elasticsearch
        String orderDateStr = hit.getSourceAsMap().get("orderDate").toString();
        String shipDateStr = hit.getSourceAsMap().get("shipDate").toString();

        // Chuyển đổi chuỗi ngày tháng ISO 8601 với múi giờ thành OffsetDateTime
        OffsetDateTime orderDate = OffsetDateTime.parse(orderDateStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        OffsetDateTime shipDate = OffsetDateTime.parse(shipDateStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        // Thiết lập vào đối tượng SalesRecordES
        record.setOrderDate(orderDate.toLocalDate());  // Nếu chỉ cần phần ngày
        record.setShipDate(shipDate.toLocalDate());

        // Lấy các trường từ Map trong Source của SearchHit và gán vào các trường tương ứng của SalesRecordES
        record.setId(hit.getId());
        record.setRegion(hit.getSourceAsMap().get("region").toString());
        record.setCountry(hit.getSourceAsMap().get("country").toString());
        record.setItemType(hit.getSourceAsMap().get("itemType").toString());
        record.setSalesChannel(hit.getSourceAsMap().get("salesChannel").toString());
        record.setOrderPriority(hit.getSourceAsMap().get("orderPriority").toString());
        record.setOrderId(hit.getSourceAsMap().get("orderId").toString());

        // Chuyển đổi các trường số nguyên và số thực
        record.setUnitsSold(Integer.valueOf(hit.getSourceAsMap().get("unitsSold").toString()));
        record.setUnitPrice(Double.valueOf(hit.getSourceAsMap().get("unitPrice").toString()));
        record.setUnitCost(Double.valueOf(hit.getSourceAsMap().get("unitCost").toString()));

        // Tính toán doanh thu, chi phí và lợi nhuận
        record.setTotalRevenue(Double.valueOf(hit.getSourceAsMap().get("totalRevenue").toString()));
        record.setTotalCost(Double.valueOf(hit.getSourceAsMap().get("totalCost").toString()));
        record.setTotalProfit(Double.valueOf(hit.getSourceAsMap().get("totalProfit").toString()));

        return record;
    }

    // Build response
    private SalesRecordResponse buildResponse(SalesRecordES existsRecord) {
        return SalesRecordResponse.builder()
                .id(existsRecord.getId())
                .region(existsRecord.getRegion())
                .country(existsRecord.getCountry())
                .itemType(existsRecord.getItemType())
                .salesChannel(existsRecord.getSalesChannel())
                .orderPriority(existsRecord.getOrderPriority())
                .orderDate(existsRecord.getOrderDate().format(FORMATTER))
                .orderId(existsRecord.getOrderId())
                .shipDate(existsRecord.getShipDate().format(FORMATTER))
                .unitsSold(existsRecord.getUnitsSold())
                .unitPrice(existsRecord.getUnitPrice())
                .unitCost(existsRecord.getUnitCost())
                .totalRevenue(existsRecord.getTotalRevenue())
                .totalCost(existsRecord.getTotalCost())
                .totalProfit(existsRecord.getTotalProfit())
                .build();
    }
}
