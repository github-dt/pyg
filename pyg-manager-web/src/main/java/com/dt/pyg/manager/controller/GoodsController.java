package com.dt.pyg.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.dt.pyg.common.pojo.PageResult;
import com.dt.pyg.pojo.Goods;
import com.dt.pyg.pojo.Item;
import com.dt.pyg.search.service.ItemSearchService;
import com.dt.pyg.sellergoods.service.GoodsService;
import com.dt.pyg.solr.SolrItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * GoodsController
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference(timeout = 10000)
	private GoodsService goodsService;

	@Reference(timeout=30000)
	private ItemSearchService itemSearchService;


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

			/** 按照SPU ID查询 SKU数据(状态为1) */
			if(status.equals("1")){//审核通过
/** 查询审核通过的SKU具体商品数据 */
				List<Item> itemList = goodsService
						.findItemsByGoodsIdAndStatus(ids,status);
				if(itemList.size() > 0){
					/** 调用搜索服务实现数据同步到索引库 */
					List<SolrItem> solrItems = new ArrayList<>();
					for(Item item1 : itemList){
						SolrItem solrItem = new SolrItem();
						solrItem.setId(item1.getId());
						solrItem.setBrand(item1.getBrand());
						solrItem.setCategory(item1.getCategory());
						solrItem.setGoodsId(item1.getGoodsId());
						solrItem.setImage(item1.getImage());
						solrItem.setPrice(item1.getPrice());
						solrItem.setSeller(item1.getSeller());
						solrItem.setTitle(item1.getTitle());
						/** 将spec字段中的json字符串转换为map */
						Map specMap = JSON.parseObject(item1.getSpec());
						/** 动态字段赋值 */
						solrItem.setSpecMap(specMap);
						solrItem.setUpdateTime(item1.getUpdateTime());
						solrItems.add(solrItem);
					}
					itemSearchService.saveOrUpdate(solrItems);
				}
			}


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

			/** 删除索引库数据 */
			itemSearchService.delete(Arrays.asList(ids));

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}


}
