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

    @Field(type = FieldType.Text, name = "region")
    private String region;

    @Field(type = FieldType.Text, name = "country")
    private String country;

    @Field(type = FieldType.Text, name = "itemType")
    private String itemType;

    @Field(type = FieldType.Text, name = "salesChannel")
    private String salesChannel;

    @Field(type = FieldType.Text, name = "orderPriority")
    private String orderPriority;

    @Field(type = FieldType.Date, name = "orderDate", format = {}, pattern = "MM/dd/yyyy")
    private LocalDate orderDate;

    @Field(type = FieldType.Keyword, name = "orderId")
    private String orderId;

    @Field(type = FieldType.Date, name = "shipDate", format = {}, pattern = "MM/dd/yyyy")
    private LocalDate shipDate;

    @Field(type = FieldType.Integer, name = "unitsSold")
    private Integer unitsSold;

    @Field(type = FieldType.Double, name = "unitPrice")
    private Double unitPrice;

    @Field(type = FieldType.Double, name = "unitCost")
    private Double unitCost;

    @Field(type = FieldType.Double, name = "totalRevenue")
    private Double totalRevenue;

    @Field(type = FieldType.Double, name = "totalCost")
    private Double totalCost;

    @Field(type = FieldType.Double, name = "totalProfit")
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
