package com.dt.pyg.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dt.pyg.common.pojo.PageResult;
import com.dt.pyg.content.service.ContentService;
import com.dt.pyg.pojo.Content;
import org.springframework.web.bind.annotation.*;

/**
 * 内容控制器
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-05-21<p>
 */
@RestController
@RequestMapping("/content")
public class ContentController {

    /** 引用服务接口 */
    @Reference(timeout = 10000)
    private ContentService contentService;

    /** 分页查询广告 */
    @GetMapping("/findByPage")
    public PageResult findByPage(@RequestParam("page")int page,
                                 @RequestParam("rows")int rows) throws Exception{
        return contentService.findByPage(page, rows);
    }
    /** 添加广告 */
    @PostMapping("/save")
    public boolean save(@RequestBody Content content){
        try{
            contentService.saveContent(content);
            return true;
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
    /** 修改广告 */
    @PostMapping("/update")
    public boolean update(@RequestBody Content content){
        try{
            contentService.updateContent(content);
            return true;
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
    /** 删除广告 */
    @GetMapping("/delete")
    public boolean delete(Long[] ids){
        try{
            contentService.deleteContent(ids);
            return true;
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return false;
    }


}