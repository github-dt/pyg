package com.dt.pyg.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dt.pyg.sellergoods.service.GoodsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * GoodsController
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	
}
