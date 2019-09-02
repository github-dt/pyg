package com.dt.pyg.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dt.pyg.pojo.ItemCat;
import com.dt.pyg.sellergoods.service.ItemCatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ItemCatController
 */
@RestController
@RequestMapping("/itemCat")
public class ItemCatController {

	@Reference
	private ItemCatService itemCatService;

	/** 根据父级id查询商品分类 */
	@GetMapping("/findItemCatByParentId")
	public List<ItemCat> findItemCatByParentId(@RequestParam(value = "parentId",
			defaultValue = "0")Long parentId){
		return itemCatService.findItemCatByParentId(parentId);
	}

	/** 更新商品分类的缓存数据 */
	@GetMapping("/updateRedis")
	public boolean updateRedis() {
		try {
			itemCatService.saveToRedis();
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

}