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


/**
 * @author 14423
 */
@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    MiaoshaUserService miaoshaUserService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> parameterType = parameter.getParameterType();
        return parameterType == MiaoshaUser.class;
    }

    /**
     * 解析controller参数
     * @param parameter
     * @param mavContainer
     * @param webRequest
     * @param binderFactory
     * @return
     * @throws Exception
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        //获取请求的参数
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        //从request中获取参数
        assert request != null;
        String paramToken = request.getParameter(MiaoshaUserService.COOKI_NAME_TOKEN);
        String cookieToken = getCookieValue(request);
        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        return miaoshaUserService.getByToken(response,token);
    }

    private String getCookieValue(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if(cookies==null||cookies.length<=0) {
            return null;
        }
        for (Cookie cookie:cookies){
            if (cookie.getName().equals(MiaoshaUserService.COOKI_NAME_TOKEN)){
                return cookie.getValue();
            }
        }
        return null;
    }
}
