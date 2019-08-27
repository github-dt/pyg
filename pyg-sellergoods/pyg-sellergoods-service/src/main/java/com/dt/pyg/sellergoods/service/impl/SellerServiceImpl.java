package com.dt.pyg.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dt.pyg.common.pojo.PageResult;
import com.dt.pyg.mapper.SellerMapper;
import com.dt.pyg.pojo.Seller;
import com.dt.pyg.sellergoods.service.SellerService;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


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
			seller.setStatus("0");
			seller.setCreateTime(new Date());
			sellerMapper.insertSelective(seller);

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
			PageInfo<Seller> pageInfo = PageHelper.startPage(page, rows).doSelectPageInfo(new ISelect() {
				@Override
				public void doSelect() {
					sellerMapper.findAll(seller);
				}
			});
			PageResult pageResult = new PageResult();
			pageResult.setTotal(pageInfo.getTotal());
			pageResult.setRows(pageInfo.getList());
			return pageResult;
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
			Seller seller = new Seller();
			seller.setSellerId(sellerId);
			seller.setStatus(status);
			sellerMapper.updateByPrimaryKeySelective(seller);

		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 根据sellerId查询商家对象 */
	public Seller findOne(String username){
		try {
			return sellerMapper.selectByPrimaryKey(username);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}
}