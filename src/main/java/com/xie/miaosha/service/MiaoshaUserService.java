package com.xie.miaosha.service;

import com.sun.deploy.net.HttpResponse;
import com.xie.miaosha.dao.MiaoshaUserDao;
import com.xie.miaosha.domain.MiaoshaUser;
import com.xie.miaosha.exception.GlobalException;
import com.xie.miaosha.redis.MiaoshaUserKey;
import com.xie.miaosha.redis.RedisService;
import com.xie.miaosha.result.CodeMsg;
import com.xie.miaosha.utils.MD5Util;
import com.xie.miaosha.utils.UUIDUtil;
import com.xie.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class MiaoshaUserService {

    @Autowired
    MiaoshaUserDao userDao;
    @Autowired
    RedisService redisService;

    public static final String COOKI_NAME_TOKEN = "token";

    /**
     * @param response
     * @param token
     * @return
     */
    public MiaoshaUser getByToken(HttpServletResponse response, String token){
        if (StringUtils.isEmpty(token)){
            return null;
        }
        MiaoshaUser miaoshaUser = redisService.get(MiaoshaUserKey.getByToken, token, MiaoshaUser.class);
        //先不急返回，延长有效期，因为有效期应该从当前开始计算，而不是ｒｅｄｉｓ缓存里的
        //因此需要，更新redis里面的expireSeconds:
        if (miaoshaUser!=null){
            addCookie(response,token,miaoshaUser);
        }
        return miaoshaUser;
    }


    public MiaoshaUser getById(long id){
        //取缓存
        MiaoshaUser miaoshaUser = redisService.get(MiaoshaUserKey.getById, "" + id, MiaoshaUser.class);
        if (miaoshaUser!=null) {
            return miaoshaUser;
        }
        //从数据库中查询
        miaoshaUser =  userDao.getById(id);
        if (miaoshaUser!=null) {
            redisService.set(MiaoshaUserKey.getById,""+id,miaoshaUser);
        }
        return miaoshaUser;
    }

    /**
     * 更新密码
     * @param id
     * @param token
     * @param formPassword 用户提交的密码，经过一次ＭＤ５
     * @return
     */
    public boolean updatePassword(long id, String token, String formPassword){
        //查询用户
        MiaoshaUser miaoshaUser = getById(id);
        if (miaoshaUser == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST_ERROR);//用户不存在
        }
        MiaoshaUser userUpdate = new MiaoshaUser();
        userUpdate.setId(id);
        userUpdate.setPassword(MD5Util.formPassToDBPass(formPassword,miaoshaUser.getSalt()));
        miaoshaUser.setPassword(userUpdate.getPassword());//修改密码
        //更新数据库
        userDao.update(userUpdate);
        //修改缓存
        redisService.delete(MiaoshaUserKey.getById,""+id);//删除
        redisService.set(MiaoshaUserKey.getById,""+id,miaoshaUser);
        redisService.set(MiaoshaUserKey.getByToken,token,miaoshaUser);
        return true;
    }

    //用户登录
    public String login(LoginVo loginVo, HttpServletResponse response){
        if (loginVo == null){
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPassword = loginVo.getPassword();
        //判断用户是否存在
        MiaoshaUser miaoshaUser = getById(Long.parseLong(mobile));
        if (miaoshaUser == null){//用户不存在
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST_ERROR);
        }
        //校验密码是否正确
        String DBSalt = miaoshaUser.getSalt();
        String calcPassword = MD5Util.formPassToDBPass(formPassword,DBSalt);
        //todo:直接放行
        //根据从数据库的ｓａｌｔ计算出来的ｐａｓｓword
//        if (!calcPassword.equals(miaoshaUser.getPassword())){
//            throw new GlobalException( CodeMsg.PASSWORD_ERROR);
//        }
        //生成cookie
        String token = UUIDUtil.uuid();
        addCookie(response,token,miaoshaUser);
        return token;
    }

    public void addCookie(HttpServletResponse response,String token, MiaoshaUser miaoshaUser){
        redisService.set(MiaoshaUserKey.getByToken,token,miaoshaUser);//缓存到redis
        Cookie cookie = new Cookie(COOKI_NAME_TOKEN,token);
        cookie.setMaxAge(MiaoshaUserKey.getByToken.expireSeconds());//设置生命周期和redis中key的一样
        cookie.setPath("/");//设置到网站的根目录
        response.addCookie(cookie);
    }
}
