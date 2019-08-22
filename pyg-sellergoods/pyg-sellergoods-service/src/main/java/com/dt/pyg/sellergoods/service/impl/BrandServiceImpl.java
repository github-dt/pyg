package com.dt.pyg.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dt.pyg.mapper.BrandMapper;
import com.dt.pyg.pojo.Brand;
import com.dt.pyg.sellergoods.service.BrandService;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service(interfaceName = "com.dt.pyg.sellergoods.service.BrandService")
@Transactional
public class BrandServiceImpl implements BrandService{
    @Autowired
    private BrandMapper brandMapper;

    @Override
    public List<Brand> findAll() {
        PageInfo<Brand> pageInfo = PageHelper.startPage(1, 10).doSelectPageInfo(new ISelect() {
            @Override
            public void doSelect() {
//                brandMapper.findAll();
                brandMapper.selectAll();//继承mapper所带的方法
            }
        });

        System.out.println("总记录数:" +pageInfo.getTotal());
        System.out.println("总页数:" +pageInfo.getPages());
        return pageInfo.getList();
    }
}
