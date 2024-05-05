package com.xuecheng.orders.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.utils.IdWorkerUtils;
import com.xuecheng.base.utils.QRCodeUtil;
import com.xuecheng.orders.config.AlipayConfig;
import com.xuecheng.orders.mapper.XcOrdersGoodsMapper;
import com.xuecheng.orders.mapper.XcOrdersMapper;
import com.xuecheng.orders.mapper.XcPayRecordMapper;
import com.xuecheng.orders.model.dto.AddOrderDto;
import com.xuecheng.orders.model.dto.PayRecordDto;
import com.xuecheng.orders.model.dto.PayStatusDto;
import com.xuecheng.orders.model.po.XcOrders;
import com.xuecheng.orders.model.po.XcOrdersGoods;
import com.xuecheng.orders.model.po.XcPayRecord;
import com.xuecheng.orders.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author QRH
 * @date 2023/8/3 10:43
 * @description TODO
 */
@Service
@Slf4j
public class OrdersServiceImpl implements OrdersService {
    @Autowired
    private XcOrdersMapper xcOrdersMapper;
    @Autowired
    private XcOrdersGoodsMapper xcOrdersGoodsMapper;
    @Autowired
    private XcPayRecordMapper xcPayRecordMapper;
    @Autowired
    private OrdersServiceImpl currentProxy;

    @Value("${pay.qrcodeurl}")
    private String qrCodeUrl;
    @Value("${pay.alipay.APP_ID}")
    String APP_ID;
    @Value("${pay.alipay.APP_PRIVATE_KEY}")
    String APP_PRIVATE_KEY;
    @Value("${pay.alipay.ALIPAY_PUBLIC_KEY}")
    String ALIPAY_PUBLIC_KEY;

    @Override
    public PayRecordDto createOrder(String userId, AddOrderDto addOrderDto) {

        //插入订单表
        XcOrders xcOrders = saveXcOrders(userId, addOrderDto);
        //插入支付记录
        XcPayRecord payRecord = createPayRecord(xcOrders);
        //生成二维码
        QRCodeUtil qrCodeUtil = new QRCodeUtil();
        String url = String.format(qrCodeUrl, payRecord.getPayNo());
        String qrCode = null;
        try {
            qrCode = qrCodeUtil.createQRCode(url, 200, 200);
        } catch (IOException e) {
            XueChengPlusException.cast("生成二维码失败");
        }
        PayRecordDto payRecordDto = new PayRecordDto();
        BeanUtils.copyProperties(payRecord, payRecordDto);
        payRecordDto.setQrcode(qrCode);
        return payRecordDto;
    }

    @Override
    public XcPayRecord getPayRecordPayNo(String payNo) {
        return xcPayRecordMapper.selectOne(new LambdaQueryWrapper<XcPayRecord>()
                .eq(XcPayRecord::getPayNo, payNo));
    }

    @Override
    public PayRecordDto queryPayResult(String payNo) {
        //调用支付宝接口查询支付结果
        PayStatusDto payStatusDto = queryPayResultFromAlipay(payNo);
        //拿到支付结果更新支付记录和订单表的字符状态
        saveAliPayStatus(payStatusDto);
        return null;
    }

    @Override
    @Transactional
    public void saveAliPayStatus(PayStatusDto payStatusDto) {
        //支付流水号
        String payNo = payStatusDto.getOut_trade_no();
        XcPayRecord payRecord = getPayRecordPayNo(payNo);
        if (payRecord == null) {
            XueChengPlusException.cast("支付记录找不到");
        }
        //支付结果
        String trade_status = payStatusDto.getTrade_status();
        log.debug("收到支付结果:{},支付记录:{}}", payStatusDto.toString(), payRecord.toString());
        if (trade_status.equals("TRADE_SUCCESS")) {

            //支付金额变为分
            Float totalPrice = payRecord.getTotalPrice() * 100;
            Float total_amount = Float.parseFloat(payStatusDto.getTotal_amount()) * 100;
            //校验是否一致
            if (totalPrice.intValue() != total_amount.intValue()) {
                //校验失败
                log.info("校验支付结果失败,支付记录:{},APP_ID:{},totalPrice:{}", payRecord.toString(), payStatusDto.getApp_id(), total_amount.intValue());
                XueChengPlusException.cast("校验支付结果失败");
            }
            log.debug("更新支付结果,支付交易流水号:{},支付结果:{}", payNo, trade_status);
            XcPayRecord payRecord_u = new XcPayRecord();
            payRecord_u.setStatus("601002");//支付成功
            payRecord_u.setOutPayChannel("Alipay");
            payRecord_u.setOutPayNo(payStatusDto.getTrade_no());//支付宝交易号
            payRecord_u.setPaySuccessTime(LocalDateTime.now());//通知时间
            int update1 = xcPayRecordMapper.update(payRecord_u, new LambdaQueryWrapper<XcPayRecord>().eq(XcPayRecord::getPayNo, payNo));
            if (update1 > 0) {
                log.info("更新支付记录状态成功:{}", payRecord_u.toString());
            } else {
                log.info("更新支付记录状态失败:{}", payRecord_u.toString());
                XueChengPlusException.cast("更新支付记录状态失败");
            }
            //关联的订单号
            Long orderId = payRecord.getOrderId();
            XcOrders orders = xcOrdersMapper.selectById(orderId);
            if (orders == null) {
                log.info("根据支付记录[{}}]找不到订单", payRecord_u.toString());
                XueChengPlusException.cast("根据支付记录找不到订单");
            }
            XcOrders order_u = new XcOrders();
            order_u.setStatus("600002");//支付成功
            int update = xcOrdersMapper.update(order_u, new LambdaQueryWrapper<XcOrders>().eq(XcOrders::getId, orderId));
            if (update > 0) {
                log.info("更新订单表状态成功,订单号:{}", orderId);
            } else {
                log.info("更新订单表状态失败,订单号:{}", orderId);
                XueChengPlusException.cast("更新订单表状态失败");
            }
        }

    }

    /**
     * 请求支付宝查询支付结果
     *
     * @param
     * @return
     */
    private PayStatusDto queryPayResultFromAlipay(String payNo) {
        AlipayClient alipayClient = new DefaultAlipayClient(
                AlipayConfig.URL,
                APP_ID,
                APP_PRIVATE_KEY,
                AlipayConfig.FORMAT,
                AlipayConfig.CHARSET,
                ALIPAY_PUBLIC_KEY,
                AlipayConfig.SIGNTYPE
        );  //获得初始化的AlipayClient
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", payNo);
        request.setBizContent(bizContent.toString());
        AlipayTradeQueryResponse response = null;
        String body = null;
        try {
            response = alipayClient.execute(request);
            if (!response.isSuccess()) {//交易不成功
                XueChengPlusException.cast("请求支付宝查询支付结果失败");
            }
            body = response.getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
            XueChengPlusException.cast("请求支付查询支付结果异常");
        }
        //解析支付结果
        PayStatusDto payStatusDto = new PayStatusDto();
        Map<String, String> bodyMap = JSON.parseObject(body, Map.class);
        payStatusDto.setOut_trade_no(payNo);
        payStatusDto.setTrade_no(bodyMap.get("trade_no"));
        payStatusDto.setTrade_status(bodyMap.get("trade_status"));
        payStatusDto.setApp_id(APP_ID);
        payStatusDto.setTotal_amount(bodyMap.get("total_amount"));
        return payStatusDto;
    }

    /**
     * 创建订单记录信息
     *
     * @param orders
     * @return
     */
    private XcPayRecord createPayRecord(XcOrders orders) {
        Long ordersId = orders.getId();
        XcOrders xcOrders = xcOrdersMapper.selectById(ordersId);
        if (xcOrders == null) {
            XueChengPlusException.cast("订单不存在");
        }
        //订单存在
        String status = xcOrders.getStatus();

        //如果此订单不存在不能添加支付记录
        if ("601002".equals(status)) {//支付成功
            XueChengPlusException.cast("此订单已支付");
        }
        //添加支付记录
        XcPayRecord xcPayRecord = new XcPayRecord();
        xcPayRecord.setPayNo(IdWorkerUtils.getInstance().nextId());
        xcPayRecord.setOrderId(ordersId);
        xcPayRecord.setOrderName(xcOrders.getOrderName());
        xcPayRecord.setTotalPrice(xcOrders.getTotalPrice());
        xcPayRecord.setCurrency("CNY");
        xcPayRecord.setCreateDate(LocalDateTime.now());
        xcPayRecord.setStatus("601001");//未支付
        xcPayRecord.setUserId(xcOrders.getUserId());
        int insert = xcPayRecordMapper.insert(xcPayRecord);
        if (insert <= 0) {
            XueChengPlusException.cast("插入支付记录失败");
        }
        //如果订单支付结果为成功，不再添加支付记录，避免重复支付
        return xcPayRecord;
    }

    /**
     * @param userId
     * @param addOrderDto
     * @return
     */
    private XcOrders saveXcOrders(String userId, AddOrderDto addOrderDto) {
        //插入订单，订单主表，订单明细表
        XcOrders xcOrders = getOrderByBusinessId(addOrderDto.getOutBusinessId());
        if (xcOrders != null) {
            return xcOrders;
        }
        //插入订单主表
        xcOrders = new XcOrders();
        //使用雪花算法生成订单号
        xcOrders.setId(IdWorkerUtils.getInstance().nextId());
        xcOrders.setTotalPrice(addOrderDto.getTotalPrice());
        xcOrders.setCreateDate(LocalDateTime.now());
        xcOrders.setStatus("600001"); //未支付
        xcOrders.setUserId(userId);
        xcOrders.setOrderType("60201");//订单类型
        xcOrders.setOrderDescrip(addOrderDto.getOrderDescrip());
        xcOrders.setOrderName(addOrderDto.getOrderName());
        xcOrders.setOrderDetail(addOrderDto.getOrderDetail());
        xcOrders.setOutBusinessId(addOrderDto.getOutBusinessId());//吐过是选课这里记录选课表的id
        int insert = xcOrdersMapper.insert(xcOrders);
        if (insert <= 0) {
            XueChengPlusException.cast("添加订单失败");
        }
        Long ordersId = xcOrders.getId();
        //插入订单明细表
        //将前端传入的明细json转成list
        String orderDetailJson = addOrderDto.getOrderDetail();
        List<XcOrdersGoods> xcOrdersGoods = JSON.parseArray(orderDetailJson, XcOrdersGoods.class);
        //便利xcOrdersGoods插入订单明细表
        xcOrdersGoods.forEach(goods -> {
            XcOrdersGoods xcOrdersGoods1 = new XcOrdersGoods();
            BeanUtils.copyProperties(goods, xcOrdersGoods1);
            xcOrdersGoods1.setOrderId(ordersId);//订单号
            int i = xcOrdersGoodsMapper.insert(goods);
        });
        return xcOrders;
    }

    /**
     * 根据businessId查询订单
     *
     * @param businessId
     * @return
     */
    private XcOrders getOrderByBusinessId(String businessId) {
        XcOrders orders = xcOrdersMapper.selectOne(new LambdaQueryWrapper<XcOrders>()
                .eq(XcOrders::getOutBusinessId, businessId)
        );
        return orders;
    }
}
