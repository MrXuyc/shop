package com.mrxuyc.shop.service;

import com.github.pagehelper.PageInfo;
import com.mrxuyc.shop.common.ServerResponse;
import com.mrxuyc.shop.pojo.Product;

public interface IProductService {
    ServerResponse saveOrUpdateProduct(Product product);

    ServerResponse setSaleStatus(Integer productId, Integer status);

    ServerResponse managerProductDetail(Integer productId);

    ServerResponse getProductList(int pageNum, int pageSize);

    ServerResponse searchProduct(String productName, Integer productId, int pageNum, int pageSize);

    ServerResponse getProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductByKeyWordCategory(String keyword, Integer cateoryId, int pageNum, int pageSize, String orderBy);
}
