package com.dt.pyg.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dt.pyg.sellergoods.service.GoodsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller
public class ItemController {
    @Reference(timeout=10000)
    private GoodsService goodsService;
    /** 根据id查询商品信息 */
    @GetMapping("/{goodsId}")
    public String getGoods(@PathVariable("goodsId")Long goodsId,
                           Model model){
        Map<String, Object> data = goodsService.getItem(goodsId);
        model.addAllAttributes(data);
        return "item";
    }

}
