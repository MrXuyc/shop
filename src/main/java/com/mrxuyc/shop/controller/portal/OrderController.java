package com.mrxuyc.shop.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mrxuyc.shop.common.Const;
import com.mrxuyc.shop.common.ResponseCode;
import com.mrxuyc.shop.common.ServerResponse;
import com.mrxuyc.shop.pojo.User;
import com.mrxuyc.shop.service.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: mrxuyc
 * Date: 2018-04-12
 * Time: 10:22
 */
@Controller
@RequestMapping("/order/")
public class OrderController {

    private static  final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService orderService;


    @RequestMapping(value = "create.do" ,method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse create(HttpSession session, Integer shippingId ){
        User user = (User) session.getAttribute(Const.CURRENT_USER);

        return  orderService.createOrder(user.getId(),shippingId);
    }

    @RequestMapping(value = "pay.do" ,method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse pay(HttpSession session, Long orderNo, HttpServletRequest request){
        User user = (User) session.getAttribute(Const.CURRENT_USER);

        String path=request.getSession().getServletContext().getRealPath("upload");
        return  orderService.pay(orderNo,user.getId(),path);
    }

    @RequestMapping(value = "alipay_callback.do" ,method = RequestMethod.POST)
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String,String> param= Maps.newHashMap();
        for(Iterator iter = parameterMap.keySet().iterator();iter.hasNext();){
            String name = (String) iter.next();
            String[] values = parameterMap.get(name);
            String valueStr="";
            for (int i =0;i<values.length;i++){
                valueStr=(i==values.length-1)?valueStr+values[i]:valueStr+values[i]+",";
            }
            param.put(name,valueStr);
        }
        logger.info("支付宝回调,sign:{}，trade_status:{}，参数:{}",param.get("sign"),param.get("trade_status"),param.toString());
        //验证回调 避免重复通知
        param.remove("sign_type");
        try {
            boolean alipayRESCheckV2= AlipaySignature.rsaCheckV2(param, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());
            if(!alipayRESCheckV2){
                return ServerResponse.createByErrorMsg("非法请求");
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝验证异常");
            e.printStackTrace();
        }
        //todo 验证数据

        ServerResponse serverResponse=  orderService.alipayCallback(param);
        if (serverResponse.isSuccess()){
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }

    @RequestMapping(value = "query_order_pay_status.do" ,method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse queryOrderPayStatus(HttpSession session, Long orderNo){
        User user = (User) session.getAttribute(Const.CURRENT_USER);

        ServerResponse serverResponse=  orderService.queryOrderPayStatus(orderNo,user.getId());
        if (serverResponse.isSuccess()){
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }

    @RequestMapping(value = "cancel.do" ,method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse cancel(HttpSession session, Long orderNo){
        User user = (User) session.getAttribute(Const.CURRENT_USER);

        return  orderService.cancel(user.getId(),orderNo);
    }


    @RequestMapping(value = "get_order_cart_product.do" ,method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getOrderCartProduct(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);

        return  orderService.getOrderCartProduct(user.getId());
    }

    @RequestMapping(value = "detail.do" ,method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse detail(HttpSession session, Long orderNo){
        User user = (User) session.getAttribute(Const.CURRENT_USER);

        return  orderService.getOrderDetail(user.getId(),orderNo);
    }


    @RequestMapping(value = "list.do" ,method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse list(HttpSession session,
                               @RequestParam(value = "pageNum",defaultValue = "1") int pageNum ,
                               @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);

        return  orderService.getOrderList(user.getId(),pageNum,pageSize);
    }

}
