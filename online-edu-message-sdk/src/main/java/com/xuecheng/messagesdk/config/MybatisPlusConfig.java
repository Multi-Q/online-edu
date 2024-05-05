package com.xuecheng.messagesdk.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author QRH
 * @date 2023/7/6 22:02
 * @description
 */
@Configuration("messagesdk_mpconfig")
@MapperScan("com.xuecheng.messagesdk.mapper")
public class MybatisPlusConfig {


}