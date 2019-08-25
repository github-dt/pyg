package com.dt.pyg.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dt.pyg.common.pojo.PageResult;
import com.dt.pyg.mapper.SellerMapper;
import com.dt.pyg.pojo.Seller;
import com.dt.pyg.sellergoods.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


/**
 * 服务实现层
 */
@Service(interfaceName="com.dt.pyg.sellergoods.service.SellerService")
@Transactional(readOnly=false)
public class SellerServiceImpl implements SellerService {
	
	/** 注入数据访问层代理对象 */
	@Autowired
	private SellerMapper sellerMapper;

	/** 添加商家 */
	public void saveSeller(Seller seller){
		try {
			
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/**
	 * 分页查询未审核的商家
	 * @param seller 商家实体
	 * @param page 当前页码
	 * @param rows 每页显示的记录数
	 * @return 分页结果
	 */
	public PageResult findByPage(Seller seller, Integer page, Integer rows){
		try {
			
			return null;
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/**
	 * 修改商家状态
	 * @param sellerId
	 * @param status
	 */
	public void updateStatus(String sellerId, String status){
		try {
			
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 根据sellerId查询商家对象 */
	public Seller findOne(String username){
		try {
			return null;
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}
}