package com.mrxuyc.shop.controller.backend;

import com.google.common.collect.Maps;
import com.mrxuyc.shop.common.Const;
import com.mrxuyc.shop.common.ResponseCode;
import com.mrxuyc.shop.common.ServerResponse;
import com.mrxuyc.shop.pojo.Product;
import com.mrxuyc.shop.pojo.User;
import com.mrxuyc.shop.service.IFileService;
import com.mrxuyc.shop.service.IProductService;
import com.mrxuyc.shop.service.IUserService;
import com.mrxuyc.shop.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/manager/product/")
public class ProductManagerController {

    @Autowired
    private IProductService productService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IFileService fileService;

    @RequestMapping(value = "product_save.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //校验一下是否是管理员
        if(userService.checkAdminRole(user).isSuccess()){
            return productService.saveOrUpdateProduct(product);
        }else{
            return ServerResponse.createByErrorMsg("无权限操作，需要管理员操作！");
        }
    }

    @RequestMapping(value = "set_sale_status.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId,Integer status){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //校验一下是否是管理员
        if(userService.checkAdminRole(user).isSuccess()){
            return productService.setSaleStatus(productId,status);
        }else{
            return ServerResponse.createByErrorMsg("无权限操作，需要管理员操作！");
        }
    }

    @RequestMapping(value = "detail.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getDetail(HttpSession session, Integer productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //校验一下是否是管理员
        if(userService.checkAdminRole(user).isSuccess()){
            return productService.managerProductDetail(productId);
        }else{
            return ServerResponse.createByErrorMsg("无权限操作，需要管理员操作！");
        }
    }

    @RequestMapping(value = "list.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getList(HttpSession session,@RequestParam(value = "pageNum" ,defaultValue = "1") int pageNum,@RequestParam(value = "pageSize" ,defaultValue = "10")int pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //校验一下是否是管理员
        if(userService.checkAdminRole(user).isSuccess()){
            return productService.getProductList(pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMsg("无权限操作，需要管理员操作！");
        }
    }

    @RequestMapping(value = "search.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse searchProduct(HttpSession session ,String productName,Integer productId,@RequestParam(value = "pageNum" ,defaultValue = "1") int pageNum,@RequestParam(value = "pageSize" ,defaultValue = "10")int pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //校验一下是否是管理员
        if(userService.checkAdminRole(user).isSuccess()){
            return productService.searchProduct(productName,productId,pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMsg("无权限操作，需要管理员操作！");
        }
    }

    @RequestMapping(value = "upload.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse upload(HttpSession session ,@RequestParam(value = "upload_file",required = false) MultipartFile file,HttpServletRequest request){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //校验一下是否是管理员
        if(userService.checkAdminRole(user).isSuccess()){
            //本地文件路径，上传到ftp会删除
            String path=request.getSession().getServletContext().getRealPath("upload");
            String targetFileName=fileService.upload(file,path);
            String url= PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            Map fileMap= Maps.newHashMap();
            fileMap.put("uri",targetFileName);
            fileMap.put("url",url);
            return ServerResponse.createBySuccess(fileMap);
        }else{
            return ServerResponse.createByErrorMsg("无权限操作，需要管理员操作！");
        }
    }

    @RequestMapping(value = "richtext_img_upload.do",method = RequestMethod.POST)
    @ResponseBody
    public Map richtextImgUpload(HttpServletResponse response,HttpSession session , @RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        Map fileMap= Maps.newHashMap();
        fileMap.put("success",false);
        fileMap.put("msg","上传失败");
        if(user==null){
            fileMap.put("msg","用户未登录，请登录");
            return fileMap;
        }
        //校验一下是否是管理员
        if(userService.checkAdminRole(user).isSuccess()){
            //本地文件路径，上传到ftp会删除
            String path=request.getSession().getServletContext().getRealPath("upload");
            String targetFileName=fileService.upload(file,path);
            String url= PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            if(StringUtils.isNotBlank(targetFileName)){
                fileMap.put("file_path",url);
                fileMap.put("success",true);
                fileMap.put("msg","上传成功");
                response.addHeader("Access-Control-Allow-Headers","X-File-Name");
            }
            return fileMap;
        }else{
            fileMap.put("msg","无权限操作，需要管理员操作！");
            return fileMap;
        }
    }

}
