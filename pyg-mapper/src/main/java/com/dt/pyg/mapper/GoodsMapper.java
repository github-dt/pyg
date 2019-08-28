package com.dt.pyg.mapper;


import com.dt.pyg.pojo.Goods;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface GoodsMapper extends Mapper<Goods>{


    List<Map<String,Object>> findGoodsByWhere(@Param("goods") Goods goods);

    void updateStatus(@Param("ids")Long[] ids,@Param("status") String status);

    void updateDeleteStatus(@Param("ids")Long[] ids, @Param("isDelete")String isDelete);
}