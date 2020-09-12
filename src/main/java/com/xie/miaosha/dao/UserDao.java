package com.xie.miaosha.dao;

import com.xie.miaosha.domain.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserDao {
    @Select("select * from t_user where id = #{id}")
    User getUserById(@Param("id") int id);

    @Insert("insert into t_user(name)values(#{user.name})")
    @SelectKey(keyColumn = "id",keyProperty = "user.id",before = false,resultType = int.class, statement="select last_insert_id()")
    int insertUser(@Param("user") User user);
}
