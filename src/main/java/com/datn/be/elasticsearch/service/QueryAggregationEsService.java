package com.datn.be.elasticsearch.service;

import com.datn.be.elasticsearch.model.response.aggregation.*;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.bucket.range.ParsedRange;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QueryAggregationEsService {
    private final RestHighLevelClient client;
    private static final String INDEX_NAME = "sales-records";
    private static final String TERM_AGG = "termAgg";
    private static final String DATE_HISTOGRAM_AGG = "dateHistogramAgg";
    private static final String RANGE_AGG = "rangeAgg";
    private static final String AVG_AGG = "avgAgg";
    private static final String SUM_AGG = "sumAgg";
    private static final String MIN_AGG = "minAgg";
    private static final String MAX_AGG = "maxAgg";
    private static final String CARDINALITY_AGG = "cardinalityAgg";

    private static final String FORMAT_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String FORMAT_DATE = "yyyy-MM-dd";
    private static final DateTimeFormatter INPUT_DATE_FORMATTER = DateTimeFormatter.ofPattern(FORMAT_ISO_8601);
    private static final DateTimeFormatter OUTPUT_DATE_FORMATTER = DateTimeFormatter.ofPattern(FORMAT_DATE);
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.###");

    // Term Agg
    public ResponseEntity<List<TermAggResponse>> termAgg(String field) throws IOException {
        // Tạo SearchRequest với aggregation
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(0);

        searchSourceBuilder.aggregation(AggregationBuilders.terms(TERM_AGG).field(field + ".keyword"));

        // Thực hiện truy vấn và lấy kết quả aggregation
        SearchResponse searchResponse = executeAggregationQuery(searchSourceBuilder);

        Terms terms = searchResponse.getAggregations().get(TERM_AGG);

        // Chuyển đổi kết quả aggregation thành danh sách TermAggResponse
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

    // Date Histogram Agg
    public ResponseEntity<DateHistogramAggResponse> dateHistogramAgg(String date) throws IOException, ParseException {
        // Xử lý ngày đầu vào và chuyển đổi sang định dạng đúng
        String startDate = date + "T00:00:00.000Z";  // Ngày bắt đầu
        String endDate = date + "T23:59:59.999Z";    // Ngày kết thúc

        // Tạo SearchRequest với aggregation
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(0);

        // Tạo filter để lọc theo ngày
        searchSourceBuilder.query(QueryBuilders.rangeQuery("orderDate")
                .gte(startDate) // Lọc các bản ghi có orderDate >= startDate
                .lte(endDate)   // Lọc các bản ghi có orderDate <= endDate
                .format(FORMAT_ISO_8601) // Định dạng ngày để Elasticsearch nhận diện đúng
        );

        // Aggregation cho trường orderDate, nhóm theo ngày
        searchSourceBuilder.aggregation(AggregationBuilders.dateHistogram(DATE_HISTOGRAM_AGG)
                .field("orderDate") // Sử dụng trường orderDate để nhóm theo ngày
                .calendarInterval(DateHistogramInterval.DAY) // Sử dụng calendarInterval để nhóm theo ngày
                .format(FORMAT_ISO_8601)  // Định dạng ngày theo định dạng chuẩn ISO 8601
                .subAggregation(AggregationBuilders.terms("itemTypeAgg") // Phân nhóm theo itemType
                        .field("itemType.keyword") // Phân nhóm theo loại mặt hàng
                )
        );

        // Thực hiện truy vấn và lấy kết quả
        SearchResponse searchResponse = executeAggregationQuery(searchSourceBuilder);

        // Phân tích kết quả từ response
        ParsedDateHistogram dateAgg = searchResponse.getAggregations().get(DATE_HISTOGRAM_AGG);

        // Lấy bucket đầu tiên (do chỉ lọc theo 1 ngày)
        Histogram.Bucket bucket = dateAgg.getBuckets().getFirst();

        // Lấy ngày từ bucket
        String formatDate = formatDate(bucket.getKeyAsString());

        // Tạo danh sách các mặt hàng trong ngày đó
        Terms itemTypeAgg = bucket.getAggregations().get("itemTypeAgg");
        List<String> itemTypes = new ArrayList<>();
        for (Terms.Bucket itemTypeBucket : itemTypeAgg.getBuckets()) {
            itemTypes.add(itemTypeBucket.getKeyAsString()); // Chỉ lấy tên mặt hàng
        }
        String items = String.join(", ", itemTypes);

        // Tạo đối tượng DateHistogramAggResponse và trả về kết quả
        DateHistogramAggResponse dateHistogramAggResponse = DateHistogramAggResponse.builder()
                .date(formatDate)
                .items(items)
                .build();

        // Trả về kết quả
        return ResponseEntity.ok(dateHistogramAggResponse);
    }

    // Range Agg
    public ResponseEntity<List<RangeAggResponse>> rangeAgg(String field, Double minValue, Double maxValue) throws IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(0);

        // Dựng Range Aggregation
        searchSourceBuilder.aggregation(AggregationBuilders.range(RANGE_AGG)
                .field(field)
                .addUnboundedTo(minValue)
                .addRange(minValue, maxValue)
                .addUnboundedFrom(maxValue)
        );

        // Thực hiện truy vấn và lấy kết quả
        SearchResponse searchResponse = executeAggregationQuery(searchSourceBuilder);

        ParsedRange rangeAgg = searchResponse.getAggregations().get(RANGE_AGG);
        List<RangeAggResponse> responseList = new ArrayList<>();
        for (Range.Bucket bucket : rangeAgg.getBuckets()) {
            responseList.add(RangeAggResponse.builder()
                    .key(bucket.getKeyAsString())
                    .from((Double) bucket.getFrom())
                    .to((Double) bucket.getTo())
                    .count(bucket.getDocCount())
                    .build()
            );
        }

        return ResponseEntity.ok(responseList);
    }

    // Avg Agg
    public ResponseEntity<List<AvgAggResponse>> avgAgg(String field, String yearMonth) throws IOException {
        YearMonth yearMonthObj = YearMonth.parse(yearMonth);
        String startOfMonth = yearMonthObj.atDay(1).toString(); // yyyy-MM-01
        String endOfMonth = yearMonthObj.atEndOfMonth().toString(); // yyyy-MM-dd (ngày cuối cùng của tháng)

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(0);

        searchSourceBuilder.query(
                QueryBuilders.rangeQuery("orderDate")
                        .from(startOfMonth)
                        .to(endOfMonth)
                        .format(FORMAT_DATE) // Định dạng ngày
        );

        searchSourceBuilder.aggregation(AggregationBuilders.terms("itemTypeAgg")
                .field("itemType.keyword")
                .subAggregation(AggregationBuilders.avg(AVG_AGG)
                        .field(field)
                )
        );

        SearchResponse searchResponse = executeAggregationQuery(searchSourceBuilder);

        ParsedStringTerms itemTypeAgg = searchResponse.getAggregations().get("itemTypeAgg");

        List<AvgAggResponse> responseList = new ArrayList<>();
        for (Terms.Bucket bucket : itemTypeAgg.getBuckets()) {
            ParsedAvg avgAgg = bucket.getAggregations().get(AVG_AGG);
            responseList.add(AvgAggResponse.builder()
                    .item(bucket.getKeyAsString())
                    .avgValue(Double.parseDouble(DECIMAL_FORMAT.format(avgAgg.getValue())))
                    .build()
            );
        }

        return ResponseEntity.ok(responseList);
    }

    // Sum Agg
    public ResponseEntity<List<SumAggResponse>> sumAgg(String field, String yearMonth) throws IOException {
        YearMonth yearMonthObj = YearMonth.parse(yearMonth);
        String startOfMonth = yearMonthObj.atDay(1).toString(); // yyyy-MM-01
        String endOfMonth = yearMonthObj.atEndOfMonth().toString(); // yyyy-MM-dd (ngày cuối cùng của tháng)


        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(0);

        searchSourceBuilder.query(
                QueryBuilders.rangeQuery("orderDate")
                        .from(startOfMonth)
                        .to(endOfMonth)
                        .format(FORMAT_DATE) // Định dạng ngày
        );

        searchSourceBuilder.aggregation(AggregationBuilders.terms("itemTypeAgg")
                .field("itemType.keyword")
                .subAggregation(AggregationBuilders.sum(SUM_AGG)
                        .field(field)
                )
        );

        SearchResponse searchResponse = executeAggregationQuery(searchSourceBuilder);

        ParsedStringTerms itemTypeAgg = searchResponse.getAggregations().get("itemTypeAgg");

        List<SumAggResponse> responseList = new ArrayList<>();
        for (Terms.Bucket bucket : itemTypeAgg.getBuckets()) {
            ParsedSum sumAgg = bucket.getAggregations().get(SUM_AGG);
            responseList.add(SumAggResponse.builder()
                    .item(bucket.getKeyAsString())
                    .sumValue(Double.parseDouble(DECIMAL_FORMAT.format(sumAgg.getValue())))
                    .build()
            );
        }

        return ResponseEntity.ok(responseList);
    }

    // Min Agg
    public ResponseEntity<List<MinMaxAggResponse>> minAgg(String field, String yearMonth) throws IOException {
        YearMonth yearMonthObj = YearMonth.parse(yearMonth);
        String startOfMonth = yearMonthObj.atDay(1).toString(); // yyyy-MM-01
        String endOfMonth = yearMonthObj.atEndOfMonth().toString(); // yyyy-MM-dd (ngày cuối cùng của tháng)


        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(0);

        searchSourceBuilder.query(
                QueryBuilders.rangeQuery("orderDate")
                        .from(startOfMonth)
                        .to(endOfMonth)
                        .format(FORMAT_DATE) // Định dạng ngày
        );

        searchSourceBuilder.aggregation(AggregationBuilders.terms("itemTypeAgg")
                .field("itemType.keyword")
                .subAggregation(AggregationBuilders.min(MIN_AGG)
                        .field(field)
                )
                .subAggregation(AggregationBuilders.topHits("topHitAgg")
                        .size(1)
                        .sort(SortBuilders.fieldSort(field).order(SortOrder.ASC))
                )
        );

        SearchResponse searchResponse = executeAggregationQuery(searchSourceBuilder);

        ParsedStringTerms itemTypeAgg = searchResponse.getAggregations().get("itemTypeAgg");

        List<MinMaxAggResponse> responseList = new ArrayList<>();
        for (Terms.Bucket bucket : itemTypeAgg.getBuckets()) {
            ParsedMin parsedMin = bucket.getAggregations().get(MIN_AGG);
            ParsedTopHits parsedTopHits = bucket.getAggregations().get("topHitAgg");

            SearchHit[] hits = parsedTopHits.getHits().getHits();
            String orderId = null;
            if (hits.length > 0) {
                Map<String, Object> source = hits[0].getSourceAsMap();
                orderId = (String) source.get("orderId");
            }

            responseList.add(MinMaxAggResponse.builder()
                    .item(bucket.getKeyAsString())
                    .value(parsedMin.getValue())
                    .orderId(orderId)
                    .build()
            );
        }
        return ResponseEntity.ok(responseList);
    }

    // Max Agg
    public ResponseEntity<List<MinMaxAggResponse>> maxAgg(String field, String yearMonth) throws IOException {
        YearMonth yearMonthObj = YearMonth.parse(yearMonth);
        String startOfMonth = yearMonthObj.atDay(1).toString(); // yyyy-MM-01
        String endOfMonth = yearMonthObj.atEndOfMonth().toString(); // yyyy-MM-dd (ngày cuối cùng của tháng)


        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(0);

        searchSourceBuilder.query(
                QueryBuilders.rangeQuery("orderDate")
                        .from(startOfMonth)
                        .to(endOfMonth)
                        .format(FORMAT_DATE) // Định dạng ngày
        );

        searchSourceBuilder.aggregation(AggregationBuilders.terms("itemTypeAgg")
                .field("itemType.keyword")
                .subAggregation(AggregationBuilders.max(MAX_AGG)
                        .field(field)
                )
                .subAggregation(AggregationBuilders.topHits("topHitAgg")
                        .size(1)
                        .sort(SortBuilders.fieldSort(field).order(SortOrder.DESC))
                )
        );

        SearchResponse searchResponse = executeAggregationQuery(searchSourceBuilder);

        ParsedStringTerms itemTypeAgg = searchResponse.getAggregations().get("itemTypeAgg");

        List<MinMaxAggResponse> responseList = new ArrayList<>();
        for (Terms.Bucket bucket : itemTypeAgg.getBuckets()) {
            ParsedMax parsedMax = bucket.getAggregations().get(MAX_AGG);
            ParsedTopHits parsedTopHits = bucket.getAggregations().get("topHitAgg");

            SearchHit[] hits = parsedTopHits.getHits().getHits();
            String orderId = null;
            if (hits.length > 0) {
                Map<String, Object> source = hits[0].getSourceAsMap();
                orderId = (String) source.get("orderId");
            }

            responseList.add(MinMaxAggResponse.builder()
                    .item(bucket.getKeyAsString())
                    .value(parsedMax.getValue())
                    .orderId(orderId)
                    .build()
            );
        }
        return ResponseEntity.ok(responseList);
    }

    // Cardinality Agg

    private SearchResponse executeAggregationQuery(SearchSourceBuilder searchSourceBuilder) throws IOException {
        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
        searchRequest.source(searchSourceBuilder);
        return client.search(searchRequest, RequestOptions.DEFAULT);
    }

    private String formatDate(String date) {
        LocalDate parsedDate = LocalDate.parse(date, INPUT_DATE_FORMATTER);
        return parsedDate.format(OUTPUT_DATE_FORMATTER);
    }
}
