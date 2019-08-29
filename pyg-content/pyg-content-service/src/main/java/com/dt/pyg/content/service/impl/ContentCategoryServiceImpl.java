package com.dt.pyg.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dt.pyg.common.pojo.PageResult;
import com.dt.pyg.content.service.ContentCategoryService;
import com.dt.pyg.mapper.ContentCategoryMapper;
import com.dt.pyg.pojo.ContentCategory;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 内容分类服务接口实现类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-05-21<p>
 */
@Service(interfaceName = "com.dt.pyg.content.service.ContentCategoryService")
@Transactional
public class ContentCategoryServiceImpl implements ContentCategoryService {
    @Autowired
    private ContentCategoryMapper contentCategoryMapper;

    /** 分页查询内容分类 */
    public PageResult findByPage(ContentCategory contentCategory,int page, int rows){
        try{
            /** 开始分页 */
            PageInfo<ContentCategory> pageInfo = PageHelper.startPage(page, rows)
                    .doSelectPageInfo(new ISelect() {
                        @Override
                        public void doSelect() {
//                            contentCategoryMapper.selectAll();
                            contentCategoryMapper.findAll(contentCategory);
                        }
                    });
            /** 创建PageResult */
            PageResult pageResult = new PageResult();
            pageResult.setTotal(pageInfo.getTotal());
            pageResult.setRows(pageInfo.getList());
            return pageResult;
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
    /** 添加内容分类  */
    public void saveContentCategory(ContentCategory contentCategory){
        try{
            contentCategoryMapper.insertSelective(contentCategory);
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
    /** 修改内容分类 */
    public void updateContentCategory(ContentCategory contentCategory){
        try{
            contentCategoryMapper.updateByPrimaryKeySelective(contentCategory);
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
    /**  删除内容分类 */
    public void deleteContentCategory(Long[] ids){
        try{
            for (Long id : ids){
                contentCategoryMapper.deleteByPrimaryKey(id);
            }
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<ContentCategory> findAll() {
        try{
            return contentCategoryMapper.selectAll();
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

}
