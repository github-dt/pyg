package com.dt.pyg.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dt.pyg.common.pojo.PageResult;
import com.dt.pyg.pojo.Seller;
import com.dt.pyg.sellergoods.service.SellerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * SellerController
 */
@RestController
@RequestMapping("/seller")
public class SellerController {

	@Reference(timeout = 10000)
	private SellerService sellerService;

	/** 分页查询未审核的商家 */
	@GetMapping("/findByPage")
	public PageResult findByPage(Seller seller,
								 Integer page, Integer rows){
		try{
			/** GET请求中文转码 */
			if (seller != null && StringUtils.isNoneBlank(seller.getName())){
				seller.setName(new String(seller.getName()
						.getBytes("ISO8859-1"),"UTF-8"));
			}
			if (seller != null && StringUtils.isNoneBlank(seller.getNickName())){
				seller.setNickName(new String(seller.getNickName()
						.getBytes("ISO8859-1"),"UTF-8"));
			}
		}catch (Exception ex){
			ex.printStackTrace();
		}
		return sellerService.findByPage(seller, page, rows);
	}

	/** 审核商家(修改商家的状态) */
	@GetMapping("/upddateStatus")
	public boolean upddateStatus(String sellerId, String status){
		try{
			sellerService.updateStatus(sellerId, status);
			return true;
		}catch (Exception ex){
			ex.printStackTrace();
		}
		return false;
	}

	/** 添加商家 */
	@PostMapping("/save")
	public boolean save(@RequestBody Seller seller){
		try{
			/** 密码加密 */
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String password = passwordEncoder.encode(seller.getPassword());
			seller.setPassword(password);
			sellerService.saveSeller(seller);
			return true;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return false;
	}

}