package com.xie.miaosha.controller;

import com.xie.miaosha.access.AccessLimit;
import com.xie.miaosha.domain.MiaoshaOrder;
import com.xie.miaosha.domain.MiaoshaUser;
import com.xie.miaosha.rabbitmq.MQSender;
import com.xie.miaosha.rabbitmq.MiaoshaMessage;
import com.xie.miaosha.redis.*;
import com.xie.miaosha.result.CodeMsg;
import com.xie.miaosha.result.Result;
import com.xie.miaosha.service.GoodsService;
import com.xie.miaosha.service.OrderService;
import com.xie.miaosha.service.MiaoshaService;
import com.xie.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {//实现InitializingBean接口,系统初始化

    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    MiaoshaService miaoshaService;
    @Autowired
    RedisService redisService;
    @Autowired
    MQSender sender;

    //内存标记,标记每个商品是否秒杀结束了
    Map<Long, Boolean> localOverMap = new HashMap<Long, Boolean>();

    /**
     * 系统初始化
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVoList = goodsService.getGoodsVoList();
        if(goodsVoList==null)
            return;
        for (GoodsVo goodsVo:goodsVoList){//将每个秒杀商品的库存和开始时间放入ｒｅｄｉｓ
            redisService.set(GoodsKey.getGoodsStock,""+goodsVo.getId(),goodsVo.getStockCount());
            redisService.set(MiaoshaKey.getMiaoshaStartTime,""+goodsVo.getId(),goodsVo.getStartDate().getTime());
            localOverMap.put(goodsVo.getId(),false);//设置每个商品没有秒杀完
        }

    }

    @RequestMapping(value = "/{path}/do_miaosha",method = RequestMethod.GET )
    @ResponseBody
    public Result<Integer> doMiaosha(MiaoshaUser user, @RequestParam("goodsId") long goodsId,
                                     @PathVariable("path") String path) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);//用户没有登录
        }
        //检查秒杀是否开始
        boolean isStart = miaoshaService.isMiaoshaStart(goodsId);
        if (!isStart){
            return Result.error(CodeMsg.MIAOSHA_WAIT);
        }
        //校验path,前端发过来的path和ｒｅｄｉｓ中的是否一致
        boolean check = miaoshaService.checkPath(user.getId(),goodsId,path);
        if (!check){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        //内存标记，减小redis访问
        boolean isOver = localOverMap.get(goodsId);
        if (isOver){//该商品的标记为ｔrue，已经抢完
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }
        //预减库存
        long stock = redisService.decr(GoodsKey.getGoodsStock, "" + goodsId);
        if (stock < 0) {
            localOverMap.put(goodsId,true);
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }
        //判断是否已经秒杀过
        MiaoshaOrder miaoshaOrder = redisService.get(OrderKey.getByUidAndGid, "" + user.getId() + "_" + goodsId, MiaoshaOrder.class);
        if (miaoshaOrder!=null){
            return Result.error(CodeMsg.MIAOSHA_REPEATED);
        }
        //消息入队
        MiaoshaMessage miaoshaMessage = new MiaoshaMessage();
        miaoshaMessage.setMiaoshaUser(user);
        miaoshaMessage.setGoodsId(goodsId);
        sender.sendMiaoshaMessage(miaoshaMessage);
        return Result.success(0);
    }


    @RequestMapping(value = "/result",method = RequestMethod.GET )
    @ResponseBody
    public Result<Long> getMiaoshaResult(MiaoshaUser user, @RequestParam("goodsId") long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);//用户没有登录
        }
        long result = miaoshaService.getMiaoshaResult(user.getId(),goodsId);
        return Result.success(result);
    }

    /**
     * 获取秒杀地址
     * @param user
     * @param goodsId
     * @return
     */
    @AccessLimit(seconds = 5, maxCount = 10,needLogin = true)
    @RequestMapping(value = "/path",method = RequestMethod.GET )
    @ResponseBody
    public Result<String> getMiaoshaPath(MiaoshaUser user, HttpServletRequest request,
                                         @RequestParam("goodsId") long goodsId, @RequestParam(value = "verifyCode",defaultValue = "1") int verifyCode) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);//用户没有登录
        }
        //校验验证码
        boolean isRightVerifyCode = miaoshaService.checkVerifyCode(user,goodsId,verifyCode);
        if (!isRightVerifyCode){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        String path = miaoshaService.createMiaoshaPath(user.getId(),goodsId);
        return Result.success(path);
    }


    /**
     * 验证码
     * @param response
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value="/verifyCode", method=RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaVerifyCod(HttpServletResponse response, MiaoshaUser user,
                                              @RequestParam("goodsId")long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        try {
            BufferedImage image = miaoshaService.createVerifyCode(user,goodsId);
            OutputStream out = response.getOutputStream();
            ImageIO.write(image,"JPEG",out);
            out.flush();
            out.close();
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return Result.error(CodeMsg.MIAO_SHA_FAIL);
        }
    }
}
