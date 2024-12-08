package com.datn.be.elasticsearch.model.response.aggregation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MinMaxAggResponse {
    private String item;
    private Double value;
    private String orderId;
}
