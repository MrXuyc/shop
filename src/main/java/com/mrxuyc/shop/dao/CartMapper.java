package com.mrxuyc.shop.dao;

import com.mrxuyc.shop.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectCartByUserId(@Param("userId") Integer userId,@Param("productId") Integer productId);

    List<Cart> selectCartByUserId(Integer userId);

    int selectCartProductCheckedStatusByUserId(Integer userId);

    int deleteCartByUserIdAndProductIds(@Param("userId")Integer userId,@Param("productIdList")List<String> productIdList);

    int checkedOrUnCheckedAllProduct(@Param("userId")Integer userId, @Param("checked")Integer checked,@Param("productId") Integer productId);

    int selectCartProductCountByUserId(Integer userId);

    List<Cart> selectSelectCartByUserId(Integer userId);
}