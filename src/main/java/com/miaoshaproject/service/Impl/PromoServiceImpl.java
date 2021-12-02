package com.miaoshaproject.service.Impl;

import com.miaoshaproject.dao.PromoDOMapper;
import com.miaoshaproject.dataobject.PromoDO;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.service.ItemService;
import com.miaoshaproject.service.PromoService;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.ItemModel;
import com.miaoshaproject.service.model.PromoModel;
import com.miaoshaproject.service.model.UserModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class PromoServiceImpl implements PromoService {
    @Autowired
    private PromoDOMapper promoDOMapper;

    @Autowired
    private ItemService itemService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @Override
    public PromoModel getPromoByItemId(Integer itemId) {
        //获取对应商品的秒杀活动信息
        PromoDO promoDO = promoDOMapper.selectByItemId(itemId);

        //dataobject->model
        PromoModel promoModel = convertFromDataObject(promoDO);
        if(promoModel==null){
            return null;
        }

        //判断当前时间是否秒杀活动即将开始或者正在进行
        if(promoModel.getStartDate().isAfterNow()){
            promoModel.setStatus(1);//活动还未开始
        }else if(promoModel.getEndDate().isBeforeNow()){
            promoModel.setStatus(3);//活动已经结束
        }else{
            promoModel.setStatus(2);//活动正在进行中
        }

        return promoModel;
    }

    @Override
    public void publishPromo(Integer promoId) {
        PromoDO promoDO = promoDOMapper.selectByPrimaryKey(promoId);
        if(promoDO.getItemId()==null || promoDO.getItemId().intValue()==0){
            return;
        }
        ItemModel itemModel = itemService.getItemById(promoDO.getItemId());
         //将库存同步到redis内
        System.out.println("1:"+redisTemplate.opsForValue().get("promo_item_stock_"+itemModel.getId()));

        //将库存同步到redis内
        redisTemplate.opsForValue().set("promo_item_stock_"+itemModel.getId(),itemModel.getStock());

        //将大闸的限制数字设到redis内
        redisTemplate.opsForValue().set("promo_door_count_"+promoId,itemModel.getStock().intValue()*5);

    }

    @Override
    public String generateSecondKillToken(Integer promoId,Integer itemId,Integer userId) {

        //判断是否库存已售罄，若对应的售罄key存在，则直接返回下单失败
        if(redisTemplate.hasKey("promo_item_stock_invalid_"+itemId)){
            return null;
        }

        PromoDO promoDO = promoDOMapper.selectByPrimaryKey(promoId);

        //dataobject->model
        PromoModel promoModel = convertFromDataObject(promoDO);
        if(promoModel==null){
            return null;
        }

        //判断当前时间是否秒杀活动即将开始或者正在进行
        if(promoModel.getStartDate().isAfterNow()){
            promoModel.setStatus(1);//活动还未开始
        }else if(promoModel.getEndDate().isBeforeNow()){
            promoModel.setStatus(3);//活动已经结束
        }else{
            promoModel.setStatus(2);//活动正在进行中
        }
        //判断活动是否正在进行
        if(promoModel.getStatus().intValue()!=2){
            return null;
        }
        //判断item信息是否存在
        ItemModel itemModel = itemService.getItemByIdInCache(itemId);
        if (itemModel == null) {
            return null;
        }
        //判断user是否存在
        UserModel userModel = userService.getUserByIdInCache(userId);
        if (userModel == null) {
            return null;
        }

        //获取秒杀大闸的count的数量
        long result = redisTemplate.opsForValue().increment("promo_door_count_" + promoId, -1);
        if(result<0){
            return null;
        }

        //生成token并且存入redis内并给一个5分钟的有效期
        String token= UUID.randomUUID().toString().replace("-","");

        redisTemplate.opsForValue().set("promo_token_"+promoId+"_userid_"+userId+"_itemid_"+itemId,token);
        redisTemplate.expire("promo_token_"+promoId+"_userid_"+userId+"_itemid_"+itemId,5, TimeUnit.MINUTES);

        return token;
    }

    private PromoModel convertFromDataObject(PromoDO promoDO){
        if(promoDO==null){
            return null;
        }
        PromoModel promoModel=new PromoModel();
        BeanUtils.copyProperties(promoDO,promoModel);
        promoModel.setPromoItemPrice(new BigDecimal(promoDO.getPromoItemPrice()));
        promoModel.setStartDate(new DateTime(promoDO.getStartDate()));
        promoModel.setEndDate(new DateTime(promoDO.getEndDate()));
        return promoModel;
    }
}
