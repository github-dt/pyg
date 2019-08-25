package com.dt.pyg.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dt.pyg.common.pojo.PageResult;
import com.dt.pyg.pojo.Brand;
import com.dt.pyg.sellergoods.service.BrandService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {
    /**
     * 引用服务接口代理对象
     * timeout: 调用服务接口方法超时时间毫秒数
     */

    /** 引用服务 timeout:调用服务方法超时的毫秒数*/
    @Reference(timeout = 10000)
    private BrandService brandService;

    @GetMapping("/findAll")
    public List<Brand> findAll(Brand brand){
        return brandService.findAll(brand);
    }

    /** 分页查询品牌 */
    @GetMapping("/findByPage")
    public PageResult findByPage(Brand brand,@RequestParam("page") Integer page,
                                 @RequestParam("rows") Integer rows) throws Exception {
        if(brand!=null && StringUtils.isNotBlank(brand.getName())){
            brand.setName(new String(brand.getName().getBytes("ISO8859-1"),"UTF-8"));

        }

        return brandService.findByPage(brand,page,rows);

    }

    /** 添加品牌 */
    @PostMapping("/save")
    public boolean save(@RequestBody Brand brand){
        try{
            brandService.saveBrand(brand);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /** 修改品牌 */
    @PostMapping("/update")
    public boolean update(@RequestBody Brand brand){
        try{
            brandService.updateBrand(brand);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /** 批量删除 */
    @GetMapping("/delete")
    public boolean delete(Long[] ids){
        try{
            brandService.deleteBrand(ids);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }


    /** 查询所有的品牌 */
    @GetMapping("/selectBrandList")
    public List<Map<String, Object>> selectBrandList(){
        return brandService.findAllByIdAndName();
    }

}
