package com.dt.pyg.sellergoods.service;

import com.dt.pyg.common.pojo.PageResult;
import com.dt.pyg.pojo.Brand;

import java.util.List;
import java.util.Map;

public interface BrandService {
    List<Brand> findAll(Brand brand);

    PageResult findByPage(Brand brand,Integer page, Integer rows);

    void deleteBrand(Long[] ids);

    void updateBrand(Brand brand);

    void saveBrand(Brand brand);

    List<Map<String,Object>> findAllByIdAndName();
}
