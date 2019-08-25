package com.dt.pyg.sellergoods.service;


import com.dt.pyg.common.pojo.PageResult;
import com.dt.pyg.pojo.Specification;
import com.dt.pyg.pojo.SpecificationOption;

import java.util.List;
import java.util.Map;

/**
 * 规格服务层接口
 */
public interface SpecificationService {

    /**
     * 分页查询规格
     * @param specification 规格实体
     * @param page 当前页码
     * @param rows 每页显示的记录数
     * @return PageResult
     */
    PageResult findByPage(Specification specification,
                          Integer page, Integer rows);

    /** 添加规格与规格选项 */
    void saveSpecification(Specification specification);

    /** 根据规格id查询规格选项 */
    List<SpecificationOption> findOne(Long id);

    /** 修改规格与规格选项  */
    void updateSpecification(Specification specification);

    /** 删除规格与规格选项 */
    void deleteSpecification(Long[] ids);

    /** 查询规格列表(id,name) */
    List<Map<String,Object>> findSpecByIdAndName();
}
