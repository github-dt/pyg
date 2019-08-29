package com.dt.pyg.content.service;

import com.dt.pyg.common.pojo.PageResult;
import com.dt.pyg.pojo.Content;

import java.util.List;

/**
 * 内容服务接口
 *
 */
public interface ContentService {
    /** 分页查询广告 */
    PageResult findByPage(int page, int rows);
    /** 添加广告 */
    void saveContent(Content content);
    /** 修改广告 */
    void updateContent(Content content);
    /** 删除广告 */
    void deleteContent(Long[] ids);

    List<Content> findContentByCategoryId(Long categoryId);
}
