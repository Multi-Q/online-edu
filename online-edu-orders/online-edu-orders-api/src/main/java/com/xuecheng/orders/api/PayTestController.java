package com.xuecheng.orders.api;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.xuecheng.orders.config.AlipayConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author QRH
 * @date 2023/8/1 22:44
 * @description
 */
@Controller
public class PayTestController {
    @Value("${pay.alipay.APP_ID}")
    String APP_ID;
    @Value("${pay.alipay.APP_PRIVATE_KEY}")
    String APP_PRIVATE_KEY;
    @Value("${pay.alipay.ALIPAY_PUBLIC_KEY}")
    String ALIPAY_PUBLIC_KEY;

    @RequestMapping("/alipaytest")
    public void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AlipayApiException, IOException {
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
                "   \"out_trade_no\":\"20101001\"," +
                "   \"total_amount\":88.88," +
                "   \"subject\":\"HUAWEI MATE20\"," +
                "   \"product_code\":\"QUICK_WAP_WAY\"" +
                "   }"); //填充业务参数
        String form = alipayClient.pageExecute(alipayRequest).getBody();//调用sdk生成表单
        httpServletResponse.setContentType("text/html;charset=" + AlipayConfig.CHARSET);
        httpServletResponse.getWriter().write(form);//直接将完整的表单html输出到页面
        httpServletResponse.getWriter().flush();
    }

}
