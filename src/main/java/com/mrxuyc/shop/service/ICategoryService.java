package com.mrxuyc.shop.service;

import com.mrxuyc.shop.common.ServerResponse;
import com.mrxuyc.shop.pojo.Category;

import java.util.List;

public interface ICategoryService {
    ServerResponse addCategory(String categoryName, Integer parentId);

    ServerResponse setCategoryName(String categoryName, Integer categoryId);

    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

    ServerResponse<List<Integer>> getCategoryAndDeepChildrenById(Integer categoryId);
}
