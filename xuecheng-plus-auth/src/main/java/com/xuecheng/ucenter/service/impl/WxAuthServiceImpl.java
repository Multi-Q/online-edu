package com.xuecheng.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.AuthService;
import com.xuecheng.ucenter.service.WxAuthService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author QRH
 * @date 2023/7/12 17:41
 * @description
 */
@Service("wx_authservice")
public class WxAuthServiceImpl implements AuthService, WxAuthService {
    @Autowired
    private XcUserMapper xcUserMapper;
    @Value("${weixin.appid}")
    private String appId;
    @Value("${weixin.secret}")
    private String secret;
    @Autowired
    private RestTemplate    restTemplate;

    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        XcUserExt userExt = new XcUserExt();
        XcUser user = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getName, userExt.getUsername()));

        BeanUtils.copyProperties(user, userExt);

        return userExt;
    }

    @Override
    public XcUser wxAuth(String code) {
        //申请令牌
        Map<String, String> accessToken = getAccessToken(code);
        String access_token = accessToken.get("access_token");
        String openId = accessToken.get("openid");
        //携带令牌查询用户信息
        Map<String, String> userInfo = getUserInfo(access_token, openId);


        return null;
    }

    /**
     * 鞋带授权码申请令牌
     *
     * @param code 授权码
     * @return
     */
    private Map<String, String> getAccessToken(String code) {
        String url_template= "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
        //最终的请求路径
        String url = String.format(url_template, appId, secret, code);
        //远程调用url
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, null, String.class);
        //获取响应的结果
        String result = exchange.getBody();
        //将result转成map
        Map<String,String> map = JSON.parseObject(result, Map.class);
        return map;
    }

    /**
     * 用户信息
     * @param accessToken
     * @param openId
     * @return
     */
    private Map<String,String> getUserInfo(String accessToken,String openId){

        String wxUrl_template = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s";
        String url = String.format(wxUrl_template, accessToken, openId);
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, null, String.class);
        //获取响应结果
        String result = exchange.getBody();
        //将result转成map
        Map<String,String> map = JSON.parseObject(result, Map.class);
        return map;
    }
}
