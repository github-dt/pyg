package com.dt.pyg.sellergoods.service;


import com.dt.pyg.common.pojo.PageResult;
import com.dt.pyg.pojo.Goods; /**
 * 服务层接口
 */
public interface GoodsService {

    void saveGoods(Goods goods);

    PageResult findGoodsByPage(Goods goods, Integer page, Integer rows);

    Goods findOne(Long id);

    void updateGoods(Goods goods);

    void updateStatus(Long[] ids, String status);

    void deleteGoods(Long[] ids);
}
