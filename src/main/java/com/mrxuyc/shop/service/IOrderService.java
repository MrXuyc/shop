package com.mrxuyc.shop.service;

import com.github.pagehelper.PageInfo;
import com.mrxuyc.shop.common.ServerResponse;
import com.mrxuyc.shop.vo.OrderVo;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: mrxuyc
 * Date: 2018-04-13
 * Time: 15:51
 */
public interface IOrderService {
    ServerResponse pay(Long orderNo, Integer id, String path);

    ServerResponse alipayCallback(Map<String, String> param);

    ServerResponse queryOrderPayStatus(Long orderNo, Integer id);

    ServerResponse createOrder(Integer id, Integer shippingId);

    ServerResponse<String> cancel(Integer userId,Long orderNo);

    ServerResponse getOrderCartProduct(Integer id);

    ServerResponse getOrderDetail(Integer id, Long orderNo);

    ServerResponse<PageInfo> getOrderList(Integer userId, int pageNum, int pageSize);

    ServerResponse<PageInfo> manageList(int pageNum, int pageSize);

    ServerResponse<OrderVo> manageDetail(Long orderNo);

    ServerResponse<PageInfo> manageSearch(Long orderNo, int pageNum, int pageSize);

    ServerResponse<String> manageSendGoods(Long orderNo);
}
