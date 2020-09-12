package com.xie.miaosha.config;

import com.xie.miaosha.domain.MiaoshaUser;
import com.xie.miaosha.service.MiaoshaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    MiaoshaUserService miaoshaUserService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> parameterType = parameter.getParameterType();
        return parameterType == MiaoshaUser.class;//参数类型与秒杀用户是否一致,有MiaoshaUser这个参数才做下一步处理
    }

    //解析controller参数
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        //获取请求的参数
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        //从request中获取参数
        String paramToken = request.getParameter(MiaoshaUserService.COOKI_NAME_TOKEN);
        String cookieToken = getCookieValue(request,MiaoshaUserService.COOKI_NAME_TOKEN);//从请求中获得名字为COOKI_NAME_TOKEN的ｖａｌｕｅ
        if (StringUtils.isEmpty(cookieToken)&&StringUtils.isEmpty(paramToken)){//如果都为空,返回登录页面
            return null;
        }
        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        //得到token后就可以从redis中取出用户信息
        MiaoshaUser user = miaoshaUserService.getByToken(response,token);
        return user;
    }

    private String getCookieValue(HttpServletRequest request, String cookieName){
        Cookie[] cookies = request.getCookies();
        if(cookies==null||cookies.length<=0)
            return null;
        for (Cookie cookie:cookies){
            if (cookie.getName().equals(cookieName)){
                return cookie.getValue();
            }
        }
        return null;
    }
}
