package com.dt.pyg.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dt.pyg.mapper.GoodsMapper;
import com.dt.pyg.sellergoods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 */
@Service(interfaceName="com.dt.pyg.sellergoods.service.GoodsService")
@Transactional(readOnly=false)
public class GoodsServiceImpl implements GoodsService {
	
	/** 注入数据访问层代理对象 */
	@Autowired
	private GoodsMapper goodsMapper;
	
}