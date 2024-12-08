package com.datn.be.mongodb.model.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@Data
@Document(collection = "sales-records")
public class SalesRecordMDB {
    @Id
    private String id;

    @Field("region")
    private String region;

    @Field("country")
    private String country;

    @Field("itemType")
    private String itemType;

    @Field("salesChannel")
    private String salesChannel;

    @Field("orderPriority")
    private String orderPriority;

    @Field("orderDate")
    private LocalDate orderDate;

    @Field("orderId")
    private String orderId;

    @Field("shipDate")
    private LocalDate shipDate;

    @Field("unitsSold")
    private Integer unitsSold;

    @Field("unitPrice")
    private Double unitPrice;

    @Field("unitCost")
    private Double unitCost;

    @Field("totalRevenue")
    private Double totalRevenue;

    @Field("totalCost")
    private Double totalCost;

    @Field("totalProfit")
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
