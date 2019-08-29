package com.dt.pyg.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dt.pyg.common.pojo.PageResult;
import com.dt.pyg.content.service.ContentCategoryService;
import com.dt.pyg.pojo.ContentCategory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 内容分类控制器
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-05-21<p>
 */
@RestController
@RequestMapping("/contentCategory")
public class ContentCategoryController {
    /** 引用服务接口的代理对象 */
    @Reference(timeout = 10000)
    private ContentCategoryService contentCategoryService;

    /** 分页查询内容分类 */
    @GetMapping("/findByPage")
    public PageResult findByPage(ContentCategory contentCategory,@RequestParam("page")int page,
                                 @RequestParam("rows")int rows) throws Exception{
        if(contentCategory!=null && StringUtils.isNotBlank(contentCategory.getName())){
            contentCategory.setName(new String(contentCategory.getName().getBytes("ISO8859-1"),"UTF-8"));

        }

        return contentCategoryService.findByPage(contentCategory,page, rows);
    }
    /** 添加内容分类 */
    @PostMapping("/save")
    public boolean save(@RequestBody ContentCategory contentCategory){
        try{
            contentCategoryService.saveContentCategory(contentCategory);
            return true;
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
    /** 修改内容分类 */
    @PostMapping("/update")
    public boolean update(@RequestBody ContentCategory contentCategory){
        try{
            contentCategoryService.updateContentCategory(contentCategory);
            return true;
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
    /** 删除内容分类 */
    @GetMapping("/delete")
    public boolean delete(Long[] ids){
        try{
            contentCategoryService.deleteContentCategory(ids);
            return true;
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /*** 查询所有的内容分类 */
    @GetMapping("/findAll")
    public List<ContentCategory> findAll(){
        return contentCategoryService.findAll();
    }


}
