package com.dt.pyg.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dt.pyg.pojo.TypeTemplate;
import com.dt.pyg.sellergoods.service.TypeTemplateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {
    @Reference(timeout=10000)
    private TypeTemplateService typeTemplateService;
    /** 根据主键id查询模版  */
    @GetMapping("/findOne")
    public TypeTemplate findOne(@RequestParam("id")Long id){
        try{
            return typeTemplateService.findTypeTemplateById(id);
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 根据模版id查询所有的规格与规格选项 */
    @GetMapping("/findSpecByTemplateId")
    public List<Map> findSpecByTemplateId(@RequestParam("id")Long id){
        return typeTemplateService.findSpecByTemplateId(id);
    }


}
