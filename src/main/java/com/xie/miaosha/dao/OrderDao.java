package com.xie.miaosha.dao;

import com.xie.miaosha.domain.MiaoshaOrder;
import com.xie.miaosha.domain.OrderInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface OrderDao {

    @Select("select * from miaosha_order where user_id = #{userId} and goods_id = #{goodsId}")
   MiaoshaOrder getByUserIdAndGoodsId(@Param("userId") long userId, @Param("goodsId") long goodsId);

    //插入订单信息
    @Insert("insert into order_info(user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_date)values("
            + "#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel},#{status},#{createDate} )")
    @SelectKey(keyColumn="id", keyProperty="id", resultType=long.class, statement="select last_insert_id()", before = false)
    long insertOrderInfo(OrderInfo orderInfo);

    //插入秒杀订单
    @Insert("insert into miaosha_order(user_id,order_id,goods_id)values(#{userId},#{orderId},#{goodsId})")
    void insertMiaoshaOrder(MiaoshaOrder miaoshaOrder);

    //获取订单信息
    @Select("select * from order_info where id = #{orderId}")
    OrderInfo getOrderById(@Param("orderId") long orderId);
}
