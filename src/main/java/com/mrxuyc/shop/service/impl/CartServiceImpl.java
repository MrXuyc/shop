package com.mrxuyc.shop.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mrxuyc.shop.common.Const;
import com.mrxuyc.shop.common.ResponseCode;
import com.mrxuyc.shop.common.ServerResponse;
import com.mrxuyc.shop.dao.CartMapper;
import com.mrxuyc.shop.dao.ProductMapper;
import com.mrxuyc.shop.pojo.Cart;
import com.mrxuyc.shop.pojo.Product;
import com.mrxuyc.shop.service.ICartService;
import com.mrxuyc.shop.util.BigDecimalUtil;
import com.mrxuyc.shop.util.PropertiesUtil;
import com.mrxuyc.shop.vo.CartProductVo;
import com.mrxuyc.shop.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartServiceImpl implements ICartService{
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;
    @Override
    public ServerResponse addCart(Integer userId, Integer productId, Integer count) {
        if(productId==null||count==null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart=cartMapper.selectCartByUserIdAndProductId(userId,productId);
        if (cart==null){
            Cart cartItem  =new Cart();
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartMapper.insert(cartItem);
        }else{
            count = cart.getQuantity()+count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.listCart(userId);
    }

    @Override
    public ServerResponse<CartVo> updateCart(Integer userId, Integer productId, Integer count) {
        if(productId==null||count==null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart=cartMapper.selectCartByUserIdAndProductId(userId,productId);
        if (cart!=null){
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKeySelective(cart);
        return this.listCart(userId);
    }

    @Override
    public ServerResponse<CartVo> deleteCartProduct(Integer userId, String productIds) {
        //替代split 在转成list的操作
        List<String> productIdList= Splitter.on(",").splitToList(productIds);
        if(CollectionUtils.isEmpty(productIdList)){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteCartByUserIdAndProductIds(userId,productIdList);
        return this.listCart(userId);
    }

    @Override
    public ServerResponse<CartVo> listCart(Integer userId) {
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    @Override
    public ServerResponse<CartVo> selectOrUnSelect(Integer userId,Integer checked,Integer productId) {
        int rowCount=cartMapper.checkedOrUnCheckedAllProduct(userId,checked,productId);
        return this.listCart(userId);
    }

    @Override
    public ServerResponse<Integer> getCartProductCount(Integer userId) {
        int cartCount=cartMapper.selectCartProductCountByUserId(userId);
        return ServerResponse.createBySuccess(cartCount);
    }

    /**
     * 返回购物车整体数据
     * @param userId
     * @return
     */
    private CartVo getCartVoLimit(Integer userId){
        CartVo cartVo=new CartVo();
        List<Cart> carts=cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList=Lists.newArrayList();
        //避免浮点型丢失精度问题
        BigDecimal cartTotalPrice=new BigDecimal("0");
        if (CollectionUtils.isNotEmpty(carts)){
            for (Cart cart : carts) {
                CartProductVo cartProductVo=new CartProductVo();
                cartProductVo.setId(cart.getId());
                cartProductVo.setUserId(cart.getUserId());
                cartProductVo.setProductId(cart.getProductId());
                Product product=productMapper.selectByPrimaryKey(cart.getProductId());
                if(product!=null){
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());
                    //判断库存
                    int buyLimitCount=0;
                    if (product.getStock()>=cart.getQuantity()){
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else{
                        buyLimitCount=product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        //更新购物车有效库存
                        Cart cartForUpdateQuantity=new Cart();
                        cartForUpdateQuantity.setId(cart.getId());
                        cartForUpdateQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForUpdateQuantity);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算当前单一商品的总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVo.getQuantity()));
                    cartProductVo.setProductChecked(cart.getChecked());
                }
                if(cart.getChecked()==Const.Cart.CHECKED){
                    cartTotalPrice=BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return cartVo;
    }

    /**
     *返回是否全选状态
     * @param userId
     * @return
     */
    private boolean getAllCheckedStatus(Integer userId){
        if (userId==null){
            return false;
        }
        return cartMapper.selectCartProductCheckedStatusByUserId(userId)==0;
    }
}
