package com.dt.pyg.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dt.pyg.common.pojo.PageResult;
import com.dt.pyg.mapper.BrandMapper;
import com.dt.pyg.pojo.Brand;
import com.dt.pyg.sellergoods.service.BrandService;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service(interfaceName = "com.dt.pyg.sellergoods.service.BrandService")
@Transactional
public class BrandServiceImpl implements BrandService{
    @Autowired
    private BrandMapper brandMapper;

    @Override
    public List<Brand> findAll(Brand brand) {
        PageInfo<Brand> pageInfo = PageHelper.startPage(1, 30).doSelectPageInfo(new ISelect() {
            @Override
            public void doSelect() {
//                brandMapper.findAll(brand);
                brandMapper.selectAll();//继承mapper所带的方法
            }
        });

        System.out.println("总记录数:" +pageInfo.getTotal());
        System.out.println("总页数:" +pageInfo.getPages());
        return pageInfo.getList();
//        return brandMapper.findAll();
    }

    @Override
    public PageResult findByPage(Brand brand,Integer page, Integer rows) {
        try{
            // 开始分页
            PageInfo<Brand> pageInfo = PageHelper.startPage(page, rows)
                    .doSelectPageInfo(new ISelect() {
                        @Override
                        public void doSelect() {
//                            brandMapper.selectAll();
                            brandMapper.findAll(brand);
                        }
                    });
//            System.out.println("pageInfo.getList: " + pageInfo.getList());
            // pageInfo.getList() --> PageInfo对象 --> Page
            PageResult pageResult = new PageResult();
            pageResult.setTotal(pageInfo.getTotal());
            pageResult.setRows(pageInfo.getList());

//            return new PageResult(pageInfo.getTotal(), pageInfo.getList());

            return pageResult;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void deleteBrand(Long[] ids) {
        try{
            // delete from 表 where id in(?,?,?)
            brandMapper.deleteAll(ids);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void updateBrand(Brand brand) {
        try{
            // 通用Mapper中的修改数据方法
            brandMapper.updateByPrimaryKeySelective(brand);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void saveBrand(Brand brand) {
        try{
            // 通用Mapper中的保存数据方法
            brandMapper.insertSelective(brand);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<Map<String, Object>> findAllByIdAndName() {
        try{
            return brandMapper.findAllByIdAndName();
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }

    }
}
