package com.datn.be.elasticsearch.model.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;

@Data
@Document(indexName = "sales-records")
public class SalesRecordES {

    @Id
    private String id;

    @Field(type = FieldType.Text, name = "Region")
    private String region;

    @Field(type = FieldType.Text, name = "Country")
    private String country;

    @Field(type = FieldType.Text, name = "Item Type")
    private String itemType;

    @Field(type = FieldType.Text, name = "Sales Channel")
    private String salesChannel;

    @Field(type = FieldType.Text, name = "Order Priority")
    private String orderPriority;

    @Field(type = FieldType.Date, name = "Order Date", format = {}, pattern = "MM/dd/yyyy")
    private LocalDate orderDate;

    @Field(type = FieldType.Keyword, name = "Order ID")
    private String orderID;

    @Field(type = FieldType.Date, name = "Ship Date", format = {}, pattern = "MM/dd/yyyy")
    private LocalDate shipDate;

    @Field(type = FieldType.Integer, name = "Units Sold")
    private Integer unitsSold;

    @Field(type = FieldType.Double, name = "Unit Price")
    private Double unitPrice;

    @Field(type = FieldType.Double, name = "Unit Cost")
    private Double unitCost;

    @Field(type = FieldType.Double, name = "Total Revenue")
    private Double totalRevenue;

    @Field(type = FieldType.Double, name = "Total Cost")
    private Double totalCost;

    @Field(type = FieldType.Double, name = "Total Profit")
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
