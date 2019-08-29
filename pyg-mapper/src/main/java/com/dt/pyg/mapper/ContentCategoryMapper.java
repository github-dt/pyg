package com.dt.pyg.mapper;


import com.dt.pyg.pojo.ContentCategory;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ContentCategoryMapper extends Mapper<ContentCategory>{

    List<ContentCategory> findAll(@Param("contentCategory") ContentCategory contentCategory);
}