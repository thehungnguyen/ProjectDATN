package com.datn.be.elasticsearch.service;

import com.datn.be.elasticsearch.model.response.aggregation.TermAggResponse;
import com.datn.be.elasticsearch.model.response.aggregation.DateHistogramAggResponse;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QueryAggregationEsService {
    private final RestHighLevelClient client;
    private static final String INDEX_NAME = "sales-records";
    private static final String TERM_AGG = "termAgg";
    private static final String DATE_HISTOGRAM_AGG = "dateHistogramAgg";

    public ResponseEntity<List<TermAggResponse>> termAgg(String field) throws IOException {
        // Tạo SearchRequest với aggregation
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(0);
        searchSourceBuilder.aggregation(AggregationBuilders.terms(TERM_AGG).field(field + ".keyword"));

        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
        searchRequest.source(searchSourceBuilder);

        // Thực hiện truy vấn và lấy kết quả aggregation
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        // Chuyển đổi kết quả aggregation thành danh sách TermAggResponse
        Terms terms = searchResponse.getAggregations().get(TERM_AGG);
        List<TermAggResponse> responseList = new ArrayList<>();
        for (Terms.Bucket bucket : terms.getBuckets()) {
            responseList.add(TermAggResponse.builder()
                    .key(bucket.getKeyAsString())
                    .count(String.valueOf(bucket.getDocCount()))
                    .build());
        }

        // Trả về kết quả
        return ResponseEntity.ok(responseList);
    }

    public ResponseEntity<DateHistogramAggResponse> dateHistogramAgg(String date) throws IOException {
        // Tạo SearchRequest với aggregation
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(0);

        // Xử lý ngày đầu vào và chuyển đổi sang định dạng đúng
        String startDate = date + "T00:00:00.000Z";  // Ngày bắt đầu (định dạng ISO 8601)
        String endDate = date + "T23:59:59.999Z";    // Ngày kết thúc (định dạng ISO 8601)

        // Tạo filter để lọc theo ngày
        searchSourceBuilder.query(QueryBuilders.rangeQuery("orderDate")
                .gte(startDate) // Lọc các bản ghi có orderDate >= startDate
                .lte(endDate)   // Lọc các bản ghi có orderDate <= endDate
                .format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") // Định dạng ngày để Elasticsearch nhận diện đúng
        );

        // Aggregation cho trường orderDate, nhóm theo ngày
        searchSourceBuilder.aggregation(AggregationBuilders.dateHistogram(DATE_HISTOGRAM_AGG)
                .field("orderDate") // Sử dụng trường orderDate để nhóm theo ngày
                .calendarInterval(DateHistogramInterval.DAY) // Sử dụng calendarInterval để nhóm theo ngày
                .format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")  // Định dạng ngày theo định dạng chuẩn ISO 8601
                .subAggregation(AggregationBuilders.terms("itemTypeAgg") // Phân nhóm theo itemType
                        .field("itemType.keyword") // Phân nhóm theo loại mặt hàng
                )
        );

        // Tạo truy vấn
        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
        searchRequest.source(searchSourceBuilder);

        // Thực hiện truy vấn và lấy kết quả
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        // Phân tích kết quả từ response
        ParsedDateHistogram dateAgg = searchResponse.getAggregations().get(DATE_HISTOGRAM_AGG);

        // Nếu không có kết quả cho ngày nhập vào, trả về response trống
        if (dateAgg.getBuckets().isEmpty()) {
            return ResponseEntity.ok(new DateHistogramAggResponse(date, ""));  // Không có mặt hàng
        }

        // Lấy bucket đầu tiên (do chỉ lọc theo 1 ngày)
        Histogram.Bucket bucket = dateAgg.getBuckets().get(0);

        // Lấy ngày từ bucket
        String dateKey = bucket.getKeyAsString();

        // Lấy các mặt hàng trong ngày
        Terms itemTypeAgg = bucket.getAggregations().get("itemTypeAgg");

        // Tạo danh sách các mặt hàng trong ngày đó
        List<String> itemTypes = new ArrayList<>();
        for (Terms.Bucket itemTypeBucket : itemTypeAgg.getBuckets()) {
            itemTypes.add(itemTypeBucket.getKeyAsString()); // Chỉ lấy tên mặt hàng
        }

        // Chuyển danh sách mặt hàng thành chuỗi (các mặt hàng cách nhau bởi dấu phẩy)
        String items = String.join(", ", itemTypes);

        // Tạo đối tượng DateHistogramAggResponse và trả về kết quả
        DateHistogramAggResponse dateHistogramAggResponse = DateHistogramAggResponse.builder()
                .date(dateKey)
                .items(items)
                .build();

        // Trả về kết quả
        return ResponseEntity.ok(dateHistogramAggResponse);
    }

}
