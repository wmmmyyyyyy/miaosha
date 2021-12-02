package com.miaoshaproject.service.model;


import java.io.Serializable;
import java.math.BigDecimal;

//用户下单的交易模型
public class OrderModel implements Serializable {
    //交易号 2018102100012828，是一个明显的String类型
    private String id;

    //用户id
    private Integer userId;

    //商品id
    private Integer itemId;

    //秒杀商品的id，若非空，则表示是秒杀商品方式下单
    private Integer promoId;

    //购买商品的单价，若promoId非空，则表示秒杀商品价格
    private BigDecimal itemPrice;

    //购买商品的件数
    private Integer amount;

    //购买的金额，若promoId非空，则表示秒杀商品价格
    private BigDecimal orderPrice;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }

    public Integer getPromoId() {
        return promoId;
    }

    public void setPromoId(Integer promoId) {
        this.promoId = promoId;
    }

    public OrderModel() {
    }
}
