package com.datn.be.elasticsearch.model.response.aggregation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RangeAggResponse {
    private String key; // Tên mặt hàng
    private Double from;
    private Double to;
    private long count;  // Số lượng
}
