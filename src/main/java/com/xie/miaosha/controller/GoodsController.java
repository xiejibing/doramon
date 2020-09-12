package com.xie.miaosha.controller;

import com.xie.miaosha.domain.MiaoshaUser;
import com.xie.miaosha.redis.GoodsKey;
import com.xie.miaosha.redis.RedisService;
import com.xie.miaosha.result.Result;
import com.xie.miaosha.service.GoodsService;
import com.xie.miaosha.service.MiaoshaUserService;
import com.xie.miaosha.vo.GoodsDetailVo;
import com.xie.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.context.webflux.SpringWebFluxEngineContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    MiaoshaUserService miaoshaUserService;
    @Autowired
    RedisService redisService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;


//    @RequestMapping(value = "/to_list")
//    public String toGoodsList(Model model, MiaoshaUser user) {
//        model.addAttribute("user",user);
//        //查询商品列表
//        List<GoodsVo> goodsVoList = goodsService.getGoodsVoList();
//        model.addAttribute("goodsList",goodsVoList);
//        return "goods_list";
//    }

    /**
     * 使用页面缓存进行优化
     * @param model
     * @param user
     * @return 返回一个ｈｔｍｌ
     */
    @RequestMapping(value = "/to_list",produces = "text/html")
    @ResponseBody
    public String toGoodsList(Model model, MiaoshaUser user, HttpServletRequest request, HttpServletResponse response) {
        model.addAttribute("user",user);
        //从缓存中获取goods_list.html
        String html = redisService.get(GoodsKey.getGoodsList,"",String.class);
        if(!StringUtils.isEmpty(html)){
            return html;//如果缓存中有，则直接返回
        }
        //查询商品列表
        List<GoodsVo> goodsVoList = goodsService.getGoodsVoList();
        model.addAttribute("goodsList",goodsVoList);
        WebContext ctx = new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
        //手动渲染
         html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
         //写入数据库
         if (!StringUtils.isEmpty(html)){
             redisService.set(GoodsKey.getGoodsList,"",html);
         }
        return html;
    }

    //页面静态化
    @RequestMapping("/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> toDetail(Model model, MiaoshaUser user, @PathVariable(value = "goodsId") long goodsId, HttpServletRequest request, HttpServletResponse response ) {

        //根据goodsId查询秒杀详情
        GoodsVo goods= goodsService.getGoodsVoByGoodsId(goodsId);
        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();//当前时间
        int status = 0;
        long remainSeconds = 0;//剩余时间
        if (now<startAt){
            //秒杀未开始
            status = 0;
            remainSeconds = (startAt-now)/1000;
        }else if (now > endAt){
            //秒杀结束
            status = 2;
            remainSeconds = -1;
        }else {//正在秒杀
            status = 1;
            remainSeconds = 0;
        }
        GoodsDetailVo goodsDetailVo = new GoodsDetailVo();
        goodsDetailVo.setUser(user);
        goodsDetailVo.setGoods(goods);
        goodsDetailVo.setMiaoshaStatus(status);
        goodsDetailVo.setRemainSeconds(remainSeconds);
        return Result.success(goodsDetailVo);
    }

    ///----------------------使用页面缓存技术-----------------------
    @RequestMapping("/to_detail2/{goodsId}")
    @ResponseBody
    public String toDetail2(Model model, MiaoshaUser user, @PathVariable(value = "goodsId") long goodsId,HttpServletRequest request, HttpServletResponse response ) {
        //页面缓存
        //从缓存中获取goods_detail.html
        String html = redisService.get(GoodsKey.getGoodsDetail,"",String.class);
        if(!StringUtils.isEmpty(html)){
            return html;//如果缓存中有，则直接返回
        }
        model.addAttribute("user",user);
        //根据goodsId查询秒杀详情
        GoodsVo goods= goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods",goods);
        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();//当前时间
        int status = 0;
        long remainSeconds = 0;//剩余时间
        if (now<startAt){
            //秒杀未开始
            status = 0;
            remainSeconds = (startAt-now)/1000;
        }else if (now > endAt){
            //秒杀结束
            status = 2;
            remainSeconds = -1;
        }else {//正在秒杀
            status = 1;
            remainSeconds = 0;
        }
        model.addAttribute("miaoshaStatus",status);
        model.addAttribute("remainSeconds",remainSeconds);

        WebContext ctx = new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
        //手动渲染
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);
        //写入数据库
        if (!StringUtils.isEmpty(html)){
            redisService.set(GoodsKey.getGoodsDetail,"",html);
        }
        return html;

    }

}
