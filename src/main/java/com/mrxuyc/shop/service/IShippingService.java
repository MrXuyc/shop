package com.mrxuyc.shop.service;

import com.github.pagehelper.PageInfo;
import com.mrxuyc.shop.common.ServerResponse;
import com.mrxuyc.shop.pojo.Shipping; /**
 * Created with IntelliJ IDEA.
 * Description:
 * User: mrxuyc
 * Date: 2018-04-12
 * Time: 15:32
 */
public interface IShippingService {
    ServerResponse addShipping(Integer userId, Shipping shipping);

    ServerResponse deleteShipping(Integer id, Integer shippingId);

    ServerResponse updateShipping(Integer id, Shipping shipping);

    ServerResponse<Shipping> selectShipping(Integer id, Integer shippingId);

    ServerResponse<PageInfo> listShipping(Integer id, int pageNum, int pageSize);
}
