package com.xie.miaosha.redis;

import com.xie.miaosha.domain.Goods;

public class GoodsKey extends BasePrefix {



    public GoodsKey(int expireSeconds, String prefix) {
        super(expireSeconds,prefix);
    }
    public static GoodsKey getGoodsList = new GoodsKey(60,"goods_list");
    public static GoodsKey getGoodsDetail = new GoodsKey(60,"goods_detail");
    public static GoodsKey getGoodsStock = new GoodsKey(0,"goods_stock");
    public static GoodsKey getGoodsOver = new GoodsKey(0,"isGoodsOver");
}
