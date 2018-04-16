package com.mrxuyc.shop.service;

import com.mrxuyc.shop.common.ServerResponse;
import com.mrxuyc.shop.vo.CartVo;

public interface ICartService {
    ServerResponse addCart(Integer userId, Integer productId, Integer count);

    ServerResponse<CartVo> updateCart(Integer userId, Integer productId, Integer count);

    ServerResponse<CartVo> deleteCartProduct(Integer userId, String productIds);

    ServerResponse<CartVo> listCart(Integer userId);

    ServerResponse<CartVo> selectOrUnSelect(Integer userId,Integer checked,Integer productId);

    ServerResponse<Integer> getCartProductCount(Integer id);
}
