package com.dt.pyg.mapper;


import com.dt.pyg.pojo.Seller;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SellerMapper extends Mapper<Seller>{


    List<Seller> findAll(@Param("seller")Seller seller);
}