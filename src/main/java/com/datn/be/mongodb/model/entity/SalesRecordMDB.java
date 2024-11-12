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

    @Field("Region")
    private String region;

    @Field("Country")
    private String country;

    @Field("Item Type")
    private String itemType;

    @Field("Sales Channel")
    private String salesChannel;

    @Field("Order Priority")
    private String orderPriority;

    @Field("Order Date")
    private LocalDate orderDate;

    @Field("Order ID")
    private String orderID;

    @Field("Ship Date")
    private LocalDate shipDate;

    @Field("Units Sold")
    private Integer unitsSold;

    @Field("Unit Price")
    private Double unitPrice;

    @Field("Unit Cost")
    private Double unitCost;

    @Field("Total Revenue")
    private Double totalRevenue;

    @Field("Total Cost")
    private Double totalCost;

    @Field("Total Profit")
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
