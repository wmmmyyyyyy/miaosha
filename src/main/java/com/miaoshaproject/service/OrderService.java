package com.miaoshaproject.service;

import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.service.model.OrderModel;

public interface OrderService {
    //创建订单
    //1.通过前端urls上传过来秒杀活动id，然后下单接口内校验对应id是否属于对应商品且活动已开始
    //2.直接在下单接口内判断对应的商品是否存在秒杀活动，若存在进行中的，则以秒杀价格下单
    //方式1更具有优势，原因：（1）用户可能从不同的url下单，可以通过不同的url设置不同的秒杀活动
    //（2）如果在订单接口里还要判断订单活动的秒杀模型的话，那么平销的商品也要再查一次秒杀活动的信息，这会伤下单链路接口性能
    OrderModel createOrder(Integer userId,Integer itemId,Integer promoId,Integer amount,String stockLogId) throws BusinessException;
//    OrderModel createOrder(Integer itemId,Integer promoId,Integer amount) throws BusinessException;
}
