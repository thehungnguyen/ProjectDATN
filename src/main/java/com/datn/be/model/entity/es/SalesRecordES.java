package com.datn.be.model.entity.es;

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

    @Field(type = FieldType.Text)
    private String region;

    @Field(type = FieldType.Text)
    private String country;

    @Field(type = FieldType.Text, name = "item_type")
    private String itemType;

    @Field(type = FieldType.Text, name = "sales_channel")
    private String salesChannel;

    @Field(type = FieldType.Text, name = "order_priority")
    private String orderPriority;

    @Field(type = FieldType.Date, name = "order_date")
    private LocalDate orderDate;

    @Field(type = FieldType.Keyword, name = "order_id")
    private String orderId;

    @Field(type = FieldType.Date, name = "ship_date")
    private LocalDate shipDate;

    @Field(type = FieldType.Integer, name = "units_sold")
    private Integer unitsSold;

    @Field(type = FieldType.Double, name = "unit_price")
    private Double unitPrice;

    @Field(type = FieldType.Double, name = "unit_cost")
    private Double unitCost;

    @Field(type = FieldType.Double, name = "total_revenue")
    private Double totalRevenue;

    @Field(type = FieldType.Double, name = "total_cost")
    private Double totalCost;

    @Field(type = FieldType.Double, name = "total_profit")
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
