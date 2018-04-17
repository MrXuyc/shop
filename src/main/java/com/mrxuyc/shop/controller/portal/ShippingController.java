package com.mrxuyc.shop.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mrxuyc.shop.common.Const;
import com.mrxuyc.shop.common.ResponseCode;
import com.mrxuyc.shop.common.ServerResponse;
import com.mrxuyc.shop.pojo.Shipping;
import com.mrxuyc.shop.pojo.User;
import com.mrxuyc.shop.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: mrxuyc
 * Date: 2018-04-12
 * Time: 15:31
 */
@Controller
@RequestMapping("/shipping/")
public class ShippingController {
    @Autowired
    private IShippingService shippingService;

    @RequestMapping(value = "add.do")
    @ResponseBody
    public ServerResponse add (HttpSession session, Shipping shipping){
        User user = (User) session.getAttribute(Const.CURRENT_USER);

        return shippingService.addShipping(user.getId(),shipping);
    }

    @RequestMapping(value = "delete.do")
    @ResponseBody
    public ServerResponse delete (HttpSession session, Integer shippingId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);

        return shippingService.deleteShipping(user.getId(),shippingId);
    }

    @RequestMapping(value = "update.do")
    @ResponseBody
    public ServerResponse update (HttpSession session, Shipping shipping){
        User user = (User) session.getAttribute(Const.CURRENT_USER);

        return shippingService.updateShipping(user.getId(),shipping);
    }

    @RequestMapping(value = "select.do")
    @ResponseBody
    public ServerResponse select (HttpSession session, Integer shippingId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);

        return shippingService.selectShipping(user.getId(),shippingId);
    }

    @RequestMapping(value = "list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list (HttpSession session,
              @RequestParam(value = "pageNum" ,defaultValue = "1") int pageNum,
              @RequestParam(value = "pageSize" ,defaultValue = "10") int pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);

        return shippingService.listShipping(user.getId(),pageNum,pageSize);
    }
}
