package com.mrxuyc.shop.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mrxuyc.shop.common.Const;
import com.mrxuyc.shop.common.ResponseCode;
import com.mrxuyc.shop.common.ServerResponse;
import com.mrxuyc.shop.pojo.User;
import com.mrxuyc.shop.service.IOrderService;
import com.mrxuyc.shop.service.IUserService;
import com.mrxuyc.shop.vo.OrderVo;
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
 * Date: 2018-04-16
 * Time: 15:10
 */
@Controller
@RequestMapping("/manager/order/")
public class OrderManagerController {
    @Autowired
    private IOrderService orderService;

    @Autowired
    private IUserService userService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> orderList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                              @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        //填充我们增加产品的业务逻辑
        return orderService.manageList(pageNum,pageSize);

    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<OrderVo> orderDetail(HttpSession session, Long orderNo){
        //填充我们增加产品的业务逻辑
        return orderService.manageDetail(orderNo);
    }



    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> orderSearch(HttpSession session, Long orderNo,@RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                                @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
        return orderService.manageSearch(orderNo,pageNum,pageSize);
    }



    @RequestMapping("send_goods.do")
    @ResponseBody
    public ServerResponse<String> orderSendGoods(HttpSession session, Long orderNo){
            //填充我们增加产品的业务逻辑
        return orderService.manageSendGoods(orderNo);
    }
}
