package com.dt.pyg.sellergoods.service;

import com.dt.pyg.common.pojo.PageResult;
import com.dt.pyg.pojo.Seller;

/**
 * 服务层接口
 */
public interface SellerService {

    /** 添加商家 */
    void saveSeller(Seller seller);

    /**
     * 分页查询未审核的商家
     * @param seller 商家实体
     * @param page 当前页码
     * @param rows 每页显示的记录数
     * @return 分页结果
     */
    PageResult findByPage(Seller seller, Integer page, Integer rows);

    /**
     * 修改商家状态
     * @param sellerId
     * @param status
     */
    void updateStatus(String sellerId, String status);

    /** 根据sellerId查询商家对象 */
    Seller findOne(String username);
}
