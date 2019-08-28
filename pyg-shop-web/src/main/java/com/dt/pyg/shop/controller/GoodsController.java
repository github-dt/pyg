package com.dt.pyg.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dt.pyg.common.pojo.PageResult;
import com.dt.pyg.pojo.Goods;
import com.dt.pyg.sellergoods.service.GoodsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * GoodsController
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference(timeout = 10000)
	private GoodsService goodsService;

	/** 添加商品 */
	@PostMapping("/save")
	public boolean save(@RequestBody Goods goods){
		try{
			/** 获取登录名 */
			String sellerId = SecurityContextHolder.getContext()
					.getAuthentication().getName();
			/** 设置商家ID */
			goods.setSellerId(sellerId);
			goodsService.saveGoods(goods);
			return true;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return false;
	}

	/** 多条件分页查询商品 */
	@GetMapping("/search")
	public PageResult search(Goods goods,
							 @RequestParam("page")Integer page,
							 @RequestParam("rows")Integer rows){
		try {
			/** 获取登录商家编号 */
			String sellerId = SecurityContextHolder.getContext()
					.getAuthentication().getName();
			/** 添加查询条件 */
			goods.setSellerId(sellerId);
			/** GET请求中文转码 */
			if (StringUtils.isNoneBlank(goods.getGoodsName())) {
				goods.setGoodsName(new String(goods
						.getGoodsName().getBytes("ISO8859-1"), "UTF-8"));
			}
			/** 调用服务层方法查询 */
			return goodsService.findGoodsByPage(goods, page, rows);
		}catch (Exception ex){
			ex.printStackTrace();
		}
		return null;
	}


    /** 根据主键id查询商品 */
    @GetMapping("/findOne")
    public Goods findOne(@RequestParam("id")Long id){
        return goodsService.findOne(id);
    }

    /** 修改商品 */
    @PostMapping("/update")
    public boolean update(@RequestBody Goods goods){
        try{
            goodsService.updateGoods(goods);
            return true;
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return false;
    }


}
