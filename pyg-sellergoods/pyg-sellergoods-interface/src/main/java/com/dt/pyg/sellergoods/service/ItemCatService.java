package com.dt.pyg.sellergoods.service;

import com.dt.pyg.pojo.ItemCat;

import java.util.List;

/**
 * 服务层接口
 */
public interface ItemCatService {

    /** 根据父级id查询商品分类 */
    List<ItemCat> findItemCatByParentId(Long parentId);
}
