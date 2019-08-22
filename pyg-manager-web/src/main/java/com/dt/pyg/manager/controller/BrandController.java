package com.dt.pyg.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dt.pyg.pojo.Brand;
import com.dt.pyg.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BrandController {
    /**
     * 引用服务接口代理对象
     * timeout: 调用服务接口方法超时时间毫秒数
     */

    /** 引用服务 timeout:调用服务方法超时的毫秒数*/
    @Reference(timeout = 10000)
    private BrandService brandService;

    @GetMapping("/brand/findAll")
    public List<Brand> findAll(){
        return brandService.findAll();
    }

}
