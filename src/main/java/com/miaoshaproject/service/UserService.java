package com.miaoshaproject.service;

import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.service.model.UserModel;

public interface UserService {

    //根据用户id返回UserModel
    UserModel getUserById(Integer id);

    //通过缓存获取用户对象
    UserModel getUserByIdInCache(Integer id);

    //用户注册
    void register(UserModel userModel) throws BusinessException;

    //用户登录验证
    UserModel validateLogin(String telphone,String encrptPassword) throws BusinessException;
}
