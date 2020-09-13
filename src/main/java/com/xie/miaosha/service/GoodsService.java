package com.xie.miaosha.service;

import com.xie.miaosha.dao.GoodsDao;
import com.xie.miaosha.domain.Goods;
import com.xie.miaosha.domain.MiaoshaGoods;
import com.xie.miaosha.domain.MiaoshaUser;
import com.xie.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {
    @Autowired
    GoodsDao goodsDao;

    public List<GoodsVo> getGoodsVoList(){
        return goodsDao.getGoodsVoList();
    }
    public GoodsVo getGoodsVoByGoodsId(long goodsId){
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }
    public int reduceStock(GoodsVo goodsVo) {
        MiaoshaGoods goods = new MiaoshaGoods();
        goods.setGoodsId(goodsVo.getId());
        return goodsDao.reduceStock(goods);
    }
}
