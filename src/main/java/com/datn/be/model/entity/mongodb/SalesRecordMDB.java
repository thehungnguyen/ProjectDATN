package com.datn.be.model.entity.mongodb;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document(collection = "sales-records")
public class SalesRecordMDB {
    @Id
    private String id;

    private String region;

    private String country;

    private String itemType;

    private String salesChannel;

    private String orderPriority;

    private LocalDate orderDate;

    private String orderId;

    private LocalDate shipDate;

    private Integer unitsSold;

    private Double unitPrice;

    private Double unitCost;

    private Double totalRevenue;

    private Double totalCost;

    private Double totalProfit;

    // Phương thức tính toán doanh thu
    public void calculateTotalRevenue() {
        this.totalRevenue = this.unitsSold * this.unitPrice;
    }

    // Phương thức tính toán chi phí
    public void calculateTotalCost() {
        this.totalCost = this.unitsSold * this.unitCost;
    }

    // Phương thức tính toán lợi nhuận
    public void calculateTotalProfit() {
        this.totalProfit = this.totalRevenue - this.totalCost;
    }
}

