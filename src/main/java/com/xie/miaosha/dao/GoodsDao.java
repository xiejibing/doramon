package com.xie.miaosha.dao;

import com.xie.miaosha.domain.MiaoshaGoods;
import com.xie.miaosha.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface GoodsDao {

    @Select("select g.*, mg.miaosha_price, mg.stock_count, mg.start_date, mg.end_date from miaosha_goods mg left join goods g on mg.goods_id = g.id ")
    List<GoodsVo> getGoodsVoList();

    @Select("select g.*, mg.miaosha_price, mg.stock_count, mg.start_date, mg.end_date from miaosha_goods mg " +
            "left join goods g on mg.goods_id = g.id" +
            " where g.id = #{goodsId}")
    GoodsVo getGoodsVoByGoodsId(@Param("goodsId") long goodsId);

    //更新库存
    @Update("update miaosha_goods set stock_count = stock_count - 1 where goods_id = #{goodsId} and stock_count>0")
    int  reduceStock(MiaoshaGoods goods);

}
