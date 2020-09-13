package com.xie.miaosha.service;

import com.xie.miaosha.domain.MiaoshaOrder;
import com.xie.miaosha.domain.MiaoshaUser;
import com.xie.miaosha.domain.OrderInfo;
import com.xie.miaosha.redis.GoodsKey;
import com.xie.miaosha.redis.MiaoshaKey;
import com.xie.miaosha.redis.RedisService;
import com.xie.miaosha.utils.MD5Util;
import com.xie.miaosha.utils.UUIDUtil;
import com.xie.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;


@Service
public class MiaoshaService {

    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    RedisService redisService;
    //原子操作
    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goodsVo) {
        //减库存
       if(goodsService.reduceStock(goodsVo)==1){
           //生成订单
           OrderInfo orderInfo = orderService.createOrder(user, goodsVo);
           return orderInfo;
       }
       else {//减库存失败,秒杀结束
           setGoodsOver(goodsVo.getId());
           return null;
       }
    }
    private void setGoodsOver(long goodsId) {
        redisService.set(GoodsKey.getGoodsOver,""+goodsId,true);
    }

    private boolean getGoodsOver(long goodsId){
        return redisService.exists(GoodsKey.getGoodsOver,""+goodsId);
    }

    /**
     *
     * @param userId s
     * @param goodsId s
     * @return 0 排队中　OrderId 秒杀成功　　－１ 秒杀结束
     *
     */
    public long getMiaoshaResult(long userId, long goodsId) {
        MiaoshaOrder order = orderService.getOrderByUserIdAndGoodsId(userId, goodsId);
        if (order!=null)//秒杀成功
        {
            return order.getOrderId();
        } else {
            boolean isOver = getGoodsOver(goodsId);
            if (isOver){
                return -1;
            }else {
                return 0;
            }
        }
    }

    /**
     * 校验ｐａｔｈ
     * @param userId
     * @param goodsId
     * @param path
     * @return
     */
    public boolean checkPath(long userId, long goodsId, String path) {
        String pathOld = redisService.get(MiaoshaKey.getMiaoshaPath,""+userId+"_"+goodsId,String.class);
        return path.equals(pathOld);
    }

    /**
     * 生成秒杀路径
     * @param userId
     * @param goodsId
     * @return
     */
    public String createMiaoshaPath(long userId, long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid()+"123dsfsf");
        redisService.set(MiaoshaKey.getMiaoshaPath,""+userId+"_"+goodsId,str);
        return str;
    }

    public boolean isMiaoshaStart(long goodsId) {
        long start = redisService.get(MiaoshaKey.getMiaoshaStartTime,""+goodsId, Long.class);
        long now = System.currentTimeMillis();
        return now>=start;
    }

    public BufferedImage createVerifyCode(MiaoshaUser user, long goodsId) {
        if(user == null || goodsId <=0) {
            return null;
        }
        int width = 100;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);//计算
        redisService.set(MiaoshaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId, rnd);
        //输出图片
        return image;
    }

    private int calc(String verifyCode)  {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (int) engine.eval(verifyCode);
        }catch (Exception e){
            return 0;
        }
    }

    private static char[] ops = new char[]{'+','-','*'};
    private String generateVerifyCode(Random rdm) {
        //产生１０以内的３个随机数字
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        //产生三个运算符号
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        char op3 = ops[rdm.nextInt(3)];
        String exp = ""+num1+op1+num2+op2+num3;
        return exp;
    }

    //校验验证码
    public boolean checkVerifyCode(MiaoshaUser user, long goodsId, int verifyCode) {
        if(user == null || goodsId <=0) {
            return false;
        }
        Integer oldCode = redisService.get(MiaoshaKey.getMiaoshaVerifyCode, user.getId() + "," + goodsId, Integer.class);
        redisService.delete(MiaoshaKey.getMiaoshaVerifyCode,user.getId() + "," + goodsId);
        return oldCode != null && oldCode == verifyCode;
    }
}
