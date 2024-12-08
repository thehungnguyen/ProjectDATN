package com.datn.be.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalesRecordResponse {
    private String id;

    private String region;

    private String country;

    private String itemType;

    private String salesChannel;

    private String orderPriority;

    private String orderDate;

    private String orderId;

    private String shipDate;

    private Integer unitsSold;

    private Double unitPrice;

    private Double unitCost;

    private Double totalRevenue;

    private Double totalCost;

    private Double totalProfit;
}
