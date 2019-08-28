package com.dt.pyg.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dt.pyg.common.pojo.PageResult;
import com.dt.pyg.pojo.Goods;
import com.dt.pyg.sellergoods.service.GoodsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * GoodsController
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference(timeout = 10000)
	private GoodsService goodsService;

	/** 搜索商品 */
	@GetMapping("/search")
	public PageResult search(Goods goods,
							 @RequestParam("page")int page,
							 @RequestParam("rows")int rows){
		try{
			/** 添加查询条件 */
			goods.setAuditStatus("0");
			/** GET请求中文转码 */
			if (StringUtils.isNoneBlank(goods.getGoodsName())){
				goods.setGoodsName(new String(
						goods.getGoodsName().getBytes("ISO8859-1"),"UTF-8"));
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		/** 调用服务层分页查询 */
		return goodsService.findGoodsByPage(goods, page, rows);
	}

	/** 商品审批，修改商品状态 */
	@GetMapping("/updateStatus")
	public boolean updateStatus(Long[] ids,String status){
		try {
			goodsService.updateStatus(ids, status);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/** 商品删除(修改删除状态) */
	@GetMapping("/delete")
	public boolean deleteGoods(Long[] ids){
		try {
			goodsService.deleteGoods(ids);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}


}
