package com.mrxuyc.shop.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mrxuyc.shop.common.Const;
import com.mrxuyc.shop.common.ResponseCode;
import com.mrxuyc.shop.common.ServerResponse;
import com.mrxuyc.shop.dao.CategoryMapper;
import com.mrxuyc.shop.dao.ProductMapper;
import com.mrxuyc.shop.pojo.Category;
import com.mrxuyc.shop.pojo.Product;
import com.mrxuyc.shop.service.ICategoryService;
import com.mrxuyc.shop.service.IProductService;
import com.mrxuyc.shop.util.DateTimeUtil;
import com.mrxuyc.shop.util.PropertiesUtil;
import com.mrxuyc.shop.vo.ProductDetailVo;
import com.mrxuyc.shop.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements IProductService{
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ICategoryService categoryService;

    @Override
    public ServerResponse saveOrUpdateProduct(Product product) {
        if (product!=null){
            if(StringUtils.isNoneBlank(product.getSubImages())){
                String[] subImageArray = product.getSubImages().split(",");
                if(subImageArray.length>0){
                    product.setMainImage(subImageArray[0]);
                }
            }
            if (product.getId()!=null){
                int rowCount=productMapper.updateByPrimaryKey(product);
                if(rowCount>0){
                    return ServerResponse.createBySuccessMsg("更新产品成功！");
                }
                return ServerResponse.createByErrorMsg("更新产品失败！");
            }else {
                int rowCount=productMapper.insert(product);
                if(rowCount>0){
                    return ServerResponse.createBySuccessMsg("新增产品成功！");
                }
                return ServerResponse.createByErrorMsg("新增产品失败！");
            }
        }
        return ServerResponse.createByErrorMsg("新增或更新产品参数不正确！");
    }

    @Override
    public ServerResponse setSaleStatus(Integer productId, Integer status) {
        if(productId==null||status==null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product=new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount=productMapper.updateByPrimaryKey(product);
        if (rowCount>0){
            return ServerResponse.createBySuccessMsg("修改产品销售状态成功");
        }
        return ServerResponse.createByErrorMsg("修改产品销售状态失败");
    }

    @Override
    public ServerResponse managerProductDetail(Integer productId) {
        if(productId==null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product==null){
            return ServerResponse.createByErrorMsg("产品已下架或删除");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    @Override
    public ServerResponse getProductList(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Product> products=productMapper.selectList();
        List<ProductListVo> productListVos=new ArrayList<ProductListVo>();
        for (Product product : products) {
            ProductListVo productListVo=assembleProductListVo(product);
            productListVos.add(productListVo);
        }
        PageInfo pageResult=new PageInfo(products);
        pageResult.setList(productListVos);
        return ServerResponse.createBySuccess(pageResult);
    }

    @Override
    public ServerResponse searchProduct(String productName, Integer productId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        if (StringUtils.isNotBlank(productName)){
            productName=new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> products=productMapper.selectByNameAndProductId(productName,productId);
        List<ProductListVo> productListVos=new ArrayList<ProductListVo>();
        for (Product product : products) {
            ProductListVo productListVo=assembleProductListVo(product);
            productListVos.add(productListVo);
        }
        PageInfo pageResult=new PageInfo(products);
        pageResult.setList(productListVos);
        return ServerResponse.createBySuccess(pageResult);
    }

    @Override
    public ServerResponse getProductDetail(Integer productId) {
        if(productId==null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product==null){
            return ServerResponse.createByErrorMsg("产品已下架或删除");
        }
        if(product.getStatus()!= Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServerResponse.createByErrorMsg("产品已下架或删除");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    @Override
    public ServerResponse<PageInfo> getProductByKeyWordCategory(String keyword, Integer cateoryId, int pageNum, int pageSize, String orderBy) {
        if (StringUtils.isBlank(keyword)&&cateoryId==null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> categoryIdList=Lists.newArrayList();
        if(cateoryId!=null){
            Category category=categoryMapper.selectByPrimaryKey(cateoryId);
            if (category==null&StringUtils.isBlank(keyword)){
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVo> productListVoList= Lists.newArrayList();
                PageInfo pageInfo=new PageInfo(productListVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            categoryIdList= categoryService.getCategoryAndDeepChildrenById(cateoryId).getData();
        }
        if(StringUtils.isNotBlank(keyword)){
            keyword=new StringBuilder().append("%").append(keyword).append("%").toString();
        }
        PageHelper.startPage(pageNum,pageSize);
        //排序处理
        if(StringUtils.isNotBlank(orderBy)){
            //因为set的contains的时间复杂度为O^1 List的contains的时间复杂度为O^n
            if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
            }
        }
        List<Product> productList=productMapper.selectByNameAndCategoryId(StringUtils.isBlank(keyword)?null:keyword,categoryIdList.size()==0?null:categoryIdList);
        List<ProductListVo> productListVos=new ArrayList<ProductListVo>();
        for (Product product : productList) {
            ProductListVo productListVo=assembleProductListVo(product);
            productListVos.add(productListVo);
        }
        PageInfo pageResult=new PageInfo(productList);
        pageResult.setList(productListVos);
        return ServerResponse.createBySuccess(pageResult);
    }

    /**
     * product---->ProductListVo
     * @param product
     * @return
     */
    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo=new ProductListVo();
        BeanUtils.copyProperties(product,productListVo);
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.mrxyc.com/"));
        return productListVo;
    }

    /**
     *product---->productDetailVo
     * @param product
     * @return
     */
    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo=new ProductDetailVo();
        BeanUtils.copyProperties(product,productDetailVo);
        //imageHost
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.mrxyc.com/"));
        //parentCategoryId
        Category category=categoryMapper.selectByPrimaryKey(productDetailVo.getCategoryId());
        if(category==null){
            productDetailVo.setParentCategoryId(0);
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }
        //createTime 毫秒数
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        //updateTime  毫秒数
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }
}
