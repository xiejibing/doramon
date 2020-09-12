package com.xie.miaosha.access;

import com.alibaba.fastjson.JSON;
import com.xie.miaosha.domain.MiaoshaUser;
import com.xie.miaosha.redis.AccessKey;
import com.xie.miaosha.redis.RedisService;
import com.xie.miaosha.result.CodeMsg;
import com.xie.miaosha.result.Result;
import com.xie.miaosha.service.MiaoshaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Component
public class AccessInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    RedisService redisService;
    @Autowired
    MiaoshaUserService miaoshaUserService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (handler instanceof HandlerMethod){
            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);//获取方法的注解
            if (accessLimit == null){
                return true;//如果没有访问限制注解，直接放行
            }
            //获取user对象
            MiaoshaUser user = getUser(request,response);
            UserContext.setUser(user);
            int maxCount = accessLimit.maxCount();
            int seconds = accessLimit.seconds();
            boolean needLogin = accessLimit.needLogin();
            String uri = request.getRequestURI();
            String key = uri;
            if (needLogin){//如果需要登录
                if (user==null)
                {//没有获取到ｕｓｅｒ
                    render(response,CodeMsg.SESSION_ERROR);
                    return false;
                }
                key += "_"+user.getId();
            }

            Integer count = redisService.get(AccessKey.getAccessCount(seconds), key, Integer.class);
            if (count==null){
                redisService.set(AccessKey.getAccessCount(seconds),key,1);
            }
            else if (count<maxCount){
                redisService.incr(AccessKey.getAccessCount(seconds),key);
            }else {
                render(response,CodeMsg.REQUEST_TOO_FREQUENT);
                return false;
            }
        }
        return true;
    }

    //渲染页面
    private void render(HttpServletResponse response, CodeMsg codeMsg) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        OutputStream outputStream = response.getOutputStream();//获取输出流,返回给请求
        String str = JSON.toJSONString(Result.error(codeMsg));
        outputStream.write(str.getBytes());
        outputStream.flush();
        outputStream.close();
    }

    private MiaoshaUser getUser(HttpServletRequest request, HttpServletResponse response){
        //从request中获取参数
        String paramToken = request.getParameter(MiaoshaUserService.COOKI_NAME_TOKEN);
        String cookieToken = getCookieValue(request);//从请求中获得名字为COOKI_NAME_TOKEN的ｖａｌｕｅ
        if (StringUtils.isEmpty(cookieToken)&&StringUtils.isEmpty(paramToken)){//如果都为空,返回登录页面
            return null;
        }
        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        //得到token后就可以从redis中取出用户信息
        MiaoshaUser user = miaoshaUserService.getByToken(response,token);
        return user;
    }
    private String getCookieValue(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if(cookies==null||cookies.length<=0)
            return null;
        for (Cookie cookie:cookies){
            if (cookie.getName().equals(MiaoshaUserService.COOKI_NAME_TOKEN)){
                return cookie.getValue();
            }
        }
        return null;
    }

}
