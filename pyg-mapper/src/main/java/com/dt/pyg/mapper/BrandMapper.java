package com.dt.pyg.mapper;

import com.dt.pyg.pojo.Brand;

import java.util.List;

/**
 * 品牌数据访问接口
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2017-12-28<p>
 */
public interface BrandMapper {

    /** 查询全部品牌 */
//    @Select("select * from tb_brand order by id asc")
    List<Brand> findAll();
}
