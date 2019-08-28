package com.dt.pyg.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dt.pyg.pojo.ItemCat;
import com.dt.pyg.sellergoods.service.ItemCatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/itemCat")
public class ItemController {
    /** 定义商品分类服务接口 */
    @Reference(timeout=10000)
    private ItemCatService itemCatService;
    /** 根据父级id查询商品分类 */
    @GetMapping("/findItemCatByParentId")
    public List<ItemCat> findItemCatByParentId(
            @RequestParam(value="parentId",
                    defaultValue="0")Long parentId){
        return itemCatService.findItemCatByParentId(parentId);
    }

}
