package com.dt.pyg.content.service;

import com.dt.pyg.common.pojo.PageResult;
import com.dt.pyg.pojo.ContentCategory;

import java.util.List;

/**
 * 内容分类服务接口
 *
 */
public interface ContentCategoryService {
    /** 分页查询内容分类 */
    PageResult findByPage(ContentCategory contentCategory,int page, int rows);
    /** 添加内容分类  */
    void saveContentCategory(ContentCategory contentCategory);
    /** 修改内容分类 */
    void updateContentCategory(ContentCategory contentCategory);
    /**  删除内容分类 */
    void deleteContentCategory(Long[] ids);

    List<ContentCategory> findAll();
}
