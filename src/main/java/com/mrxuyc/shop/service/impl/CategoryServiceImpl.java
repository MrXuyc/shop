package com.mrxuyc.shop.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mrxuyc.shop.common.ServerResponse;
import com.mrxuyc.shop.dao.CategoryMapper;
import com.mrxuyc.shop.pojo.Category;
import com.mrxuyc.shop.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class CategoryServiceImpl implements ICategoryService {

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ServerResponse addCategory(String categoryName, Integer parentId) {
        if(parentId==null|| StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMsg("参数错误,新建品类失败！");
        }
        Category category=new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);//状态可用
        int rowCount =categoryMapper.insert(category);
        if(rowCount>0){
            return ServerResponse.createBySuccessMsg("添加品类成功！");
        }
        return ServerResponse.createByErrorMsg("添加品类失败！");
    }

    @Override
    public ServerResponse setCategoryName(String categoryName, Integer categoryId) {
        if(categoryId==null|| StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMsg("参数错误,更新品类失败！");
        }
        Category category=new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int rowCount=categoryMapper.updateByPrimaryKeySelective(category);
        if(rowCount>0){
            return ServerResponse.createBySuccessMsg("更新品类成功！");
        }
        return ServerResponse.createByErrorMsg("更新品类失败！");
    }

    @Override
    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId) {
        List<Category> categories=categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if(CollectionUtils.isEmpty(categories)){
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categories);
    }

    @Override
    public ServerResponse<List<Integer>> getCategoryAndDeepChildrenById(Integer categoryId) {
        Set<Category> categories= Sets.newHashSet();
        findChildCategory(categories,categoryId);
        List<Integer> categoryIdList = Lists.newArrayList();
        if (categoryId!=null){
            for (Category category : categories) {
                categoryIdList.add(category.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }

    /**
     * 递归查找品类对象
     * @param categorySet
     * @param categoryId
     * @return
     */
    private Set<Category> findChildCategory(Set<Category> categorySet,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category!=null){
            categorySet.add(category);
        }
        List<Category> categories = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for (Category categoryItem:categories) {
            findChildCategory(categorySet,categoryItem.getId());
        }
        return categorySet;
    }
}
