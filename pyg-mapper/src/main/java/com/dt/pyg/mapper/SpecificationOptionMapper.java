package com.dt.pyg.mapper;


import com.dt.pyg.pojo.Specification;
import com.dt.pyg.pojo.SpecificationOption;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SpecificationOptionMapper  extends Mapper<SpecificationOption>{


    void save(Specification specification);

    @Select("select * from tb_specification_option"
            + " where spec_id = #{id} order by orders asc")
    List<SpecificationOption> findBySpecId(Long id);

    @Override
    int delete(@Param("specificationOption") SpecificationOption specificationOption);
}