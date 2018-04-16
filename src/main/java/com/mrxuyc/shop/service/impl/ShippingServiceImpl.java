package com.mrxuyc.shop.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mrxuyc.shop.common.ServerResponse;
import com.mrxuyc.shop.dao.ShippingMapper;
import com.mrxuyc.shop.pojo.Shipping;
import com.mrxuyc.shop.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: mrxuyc
 * Date: 2018-04-12
 * Time: 15:32
 */
@Service
public class ShippingServiceImpl implements IShippingService {
    @Autowired
    private ShippingMapper shippingMapper;
    @Override
    public ServerResponse addShipping(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int rowCount =shippingMapper.insert(shipping);
        if(rowCount>0){
            Map result= Maps.newHashMap();
            result.put("shippingId" ,shipping.getId());
            return ServerResponse.createBySuccess("新增地址成功",result);
        }
        return ServerResponse.createByErrorMsg("新增地址失败");
    }

    @Override
    public ServerResponse deleteShipping(Integer userId, Integer shippingId) {
        int resultRow = shippingMapper.deleteByShippingIdAndUserId(userId,shippingId);
        if (resultRow>0){
            return ServerResponse.createBySuccessMsg("删除地址成功");
        }
        return ServerResponse.createByErrorMsg("删除地址失败");
    }

    @Override
    public ServerResponse updateShipping(Integer userId, Shipping shipping) {
        //需要从登录用户获取userId，否则可能出现模拟userId
        shipping.setUserId(userId);
        int rowCount =shippingMapper.updateByShipping(shipping);
        if(rowCount>0){
            return ServerResponse.createBySuccessMsg("更新地址成功");
        }
        return ServerResponse.createByErrorMsg("更新地址失败");
    }

    @Override
    public ServerResponse<Shipping> selectShipping(Integer userId, Integer shippingId) {
        Shipping shipping = shippingMapper.selectByShippingIdAndUserId(shippingId, userId);
        if (shipping==null){
            return ServerResponse.createByErrorMsg("无法查询到该地址");
        }
        return ServerResponse.createBySuccess(shipping);
    }

    @Override
    public ServerResponse<PageInfo> listShipping(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList=shippingMapper.selectByUserId(userId);
        PageInfo result=new PageInfo(shippingList);
        return ServerResponse.createBySuccess(result);
    }

}
