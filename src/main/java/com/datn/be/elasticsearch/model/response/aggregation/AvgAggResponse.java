package com.datn.be.elasticsearch.model.response.aggregation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AvgAggResponse {
    private String item;
    private Double avgValue;
}
