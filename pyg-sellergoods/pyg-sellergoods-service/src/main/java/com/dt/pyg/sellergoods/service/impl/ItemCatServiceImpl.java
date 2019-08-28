package com.dt.pyg.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dt.pyg.mapper.ItemCatMapper;
import com.dt.pyg.pojo.ItemCat;
import com.dt.pyg.sellergoods.service.ItemCatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 服务实现层
 */
@Service(interfaceName="com.dt.pyg.sellergoods.service.ItemCatService")
@Transactional(readOnly=false)
public class ItemCatServiceImpl implements ItemCatService {
	
	/** 注入数据访问层代理对象 */
	@Autowired
	private ItemCatMapper itemCatMapper;

	/** 根据父级id查询商品分类 */
	public List<ItemCat> findItemCatByParentId(Long parentId){
		try{
			/** 创建ItemCat封装查询条件 */
			ItemCat itemCat = new ItemCat();
			itemCat.setParentId(parentId);
			return itemCatMapper.select(itemCat);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}
}
