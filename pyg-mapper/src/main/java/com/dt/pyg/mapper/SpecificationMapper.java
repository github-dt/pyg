package com.dt.pyg.mapper;


import com.dt.pyg.pojo.Specification;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface SpecificationMapper extends Mapper<Specification>{


    List<Specification> findAll(@Param("specification") Specification specification);

    /** 查询所有的规格 */
    @Select("select id, spec_name as text from tb_specification order by id asc")
    List<Map<String,Object>> findAllByIdAndName();
}