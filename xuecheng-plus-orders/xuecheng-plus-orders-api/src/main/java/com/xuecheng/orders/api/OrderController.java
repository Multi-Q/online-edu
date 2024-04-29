package com.xuecheng.orders.api;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.orders.config.AlipayConfig;
import com.xuecheng.orders.model.dto.AddOrderDto;
import com.xuecheng.orders.model.dto.PayRecordDto;
import com.xuecheng.orders.model.dto.PayStatusDto;
import com.xuecheng.orders.model.po.XcPayRecord;
import com.xuecheng.orders.service.OrdersService;
import com.xuecheng.orders.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author QRH
 * @date 2023/8/3 10:13
 * @description TODO
 */
@Api(value = "OrderController", tags = "订单支付Controller")
@Slf4j
@Controller
public class OrderController {
    @Autowired
    private OrdersService ordersService;

    @Value("${pay.alipay.APP_ID}")
    String APP_ID;
    @Value("${pay.alipay.APP_PRIVATE_KEY}")
    String APP_PRIVATE_KEY;
    @Value("${pay.alipay.ALIPAY_PUBLIC_KEY}")
    String ALIPAY_PUBLIC_KEY;

    @ApiOperation("生成支付二维码")
    @PostMapping("/generatepaycode")
    @ResponseBody
    public PayRecordDto generatePayCode(@RequestBody AddOrderDto addOrderDto) {
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        String userId = user.getId();
        PayRecordDto order = ordersService.createOrder(userId, addOrderDto);
        return order;
    }

    @ApiOperation("扫码支付下单")
    @GetMapping("/requestpay")
    public void requestPay(String payNo, HttpServletResponse httpServletResponse) throws AlipayApiException, IOException {
        //请求支付宝下单
        XcPayRecord payRecordPayNo = ordersService.getPayRecordPayNo(payNo);
        if (payRecordPayNo == null) {
            XueChengPlusException.cast("支付记录不存在");
        }
        if ("601002".equals(payRecordPayNo.getStatus())) {
            XueChengPlusException.cast("已支付，无需重复支付");
        }

        AlipayClient alipayClient = new DefaultAlipayClient(
                AlipayConfig.URL,
                APP_ID,
                APP_PRIVATE_KEY,
                AlipayConfig.FORMAT,
                AlipayConfig.CHARSET,
                ALIPAY_PUBLIC_KEY,
                AlipayConfig.SIGNTYPE
        );  //获得初始化的AlipayClient
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();//创建api对应的request
        alipayRequest.setReturnUrl("http");
        alipayRequest.setNotifyUrl("");//在公共参数中社会回调和通知地址
        alipayRequest.setBizContent("{" +
                "   \"out_trade_no\":\"" + payNo + "\"," +
                "   \"total_amount\":" + payRecordPayNo.getTotalPrice() + "," +
                "   \"subject\":\"" + payRecordPayNo.getOrderName() + "\"," +
                "   \"product_code\":\"QUICK_WAP_WAY\"" +
                "   }"); //填充业务参数
        String form = alipayClient.pageExecute(alipayRequest).getBody();//调用sdk生成表单
        httpServletResponse.setContentType("text/html;charset=" + AlipayConfig.CHARSET);
        httpServletResponse.getWriter().write(form);//直接将完整的表单html输出到页面
        httpServletResponse.getWriter().flush();
    }

    @ApiOperation("查询支付宝结果")
    @GetMapping("/payresult")
    @ResponseBody
    public PayRecordDto payResult(String payNo) {
        //查询支付结果
        return ordersService.queryPayResult(payNo);
    }

    @ApiOperation("接收支付结果通知")
    @PostMapping("/receivenotify")
    public void receiveNotify(HttpServletRequest request, HttpServletResponse out) throws UnsupportedEncodingException, AlipayApiException {
        Map<String, String> params = new HashMap<String, String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        //验签
        boolean verify_result = AlipaySignature.rsaCheckV1(params, ALIPAY_PUBLIC_KEY, AlipayConfig.CHARSET, "RSA2");
        if (verify_result) {//验证成功
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
            String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
            String app_id = new String(request.getParameter("app_id").getBytes("ISO-8859-1"), "UTF-8");
            String total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8");

            //交易成功处理
            if (trade_status.equals("TRADE_SUCCESS")) {
                //更新支付记录表的支付状态为成功，订单表的状态为成功
                PayStatusDto payStatusDto = new PayStatusDto();
                payStatusDto.setTrade_status(trade_status);
                payStatusDto.setTrade_no(trade_no);
                payStatusDto.setOut_trade_no(out_trade_no);
                payStatusDto.setTotal_amount(total_amount);
                payStatusDto.setApp_id(APP_ID);
                ordersService.saveAliPayStatus(payStatusDto);
            }
        }

    }
}
