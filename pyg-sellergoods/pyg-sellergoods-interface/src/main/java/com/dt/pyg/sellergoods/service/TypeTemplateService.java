package com.dt.pyg.sellergoods.service;


import com.dt.pyg.common.pojo.PageResult;
import com.dt.pyg.pojo.TypeTemplate;

/**
 * 服务层接口
 */
public interface TypeTemplateService {

    /**
     * 分页查询类型模版
     * @param typeTemplate 模版实体
     * @param page 当前页码
     * @param rows 每页显示的记录数
     * @return PageResult
     */
    PageResult findByPage(TypeTemplate typeTemplate,
                          Integer page, Integer rows);

    /** 添加类型模版 */
    void saveTypeTemplate(TypeTemplate typeTemplate);

    /** 修改类型模版 */
    void updateTypeTemplate(TypeTemplate typeTemplate);

    /** 删除规格与规格选项 */
    void deleteTypeTemplate(Long[] ids);
}
