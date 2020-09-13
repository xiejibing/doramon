package com.xie.miaosha.service;

import com.sun.deploy.net.HttpResponse;
import com.xie.miaosha.dao.UserDao;
import com.xie.miaosha.domain.User;
import com.xie.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 14423
 */
@Service
public class UserService {

    @Autowired
    UserDao userDao;

    public User getUserById(int id){
        return userDao.getUserById(id);
    }

    public int insert(){
        User user = new User();
        user.setName("王志坚");
        userDao.insertUser(user);
        return user.getId();
    }

}
