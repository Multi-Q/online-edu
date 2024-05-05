package com.xuecheng.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author QRH
 * @date 2023/6/18 23:15
 * @description 跨域请求配置类
 */
@Configuration
public class GlobalCorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //允许白名单进行跨域调用
        corsConfiguration.addAllowedOrigin("*");
        //允许跨域发送cookie
        corsConfiguration.setAllowCredentials(true);
        //放行全部原始头信息
        corsConfiguration.addAllowedHeader("*");
        //允许所有请求跨域调用
        corsConfiguration.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }
}
