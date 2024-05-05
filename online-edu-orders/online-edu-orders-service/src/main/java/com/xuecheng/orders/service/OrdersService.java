package com.xuecheng.orders.service;

import com.xuecheng.orders.model.dto.AddOrderDto;
import com.xuecheng.orders.model.dto.PayRecordDto;
import com.xuecheng.orders.model.dto.PayStatusDto;
import com.xuecheng.orders.model.po.XcPayRecord;

/**
 * @author QRH
 * @date 2023/8/3 10:41
 * @description TODO
 */
public interface OrdersService {
    /**
     * 创建商品订单
     *
     * @param userId      用户id
     * @param addOrderDto
     * @return
     */
    public PayRecordDto createOrder(String userId, AddOrderDto addOrderDto);

    /**
     * 查询支付记录
     *
     * @param payNo
     * @return
     */
    public XcPayRecord getPayRecordPayNo(String payNo);

    public PayRecordDto queryPayResult(String pageNo);

    /**
     * 保存支付宝支付结果
     * @param payStatusDto
     */
    public void saveAliPayStatus(PayStatusDto payStatusDto);
}
