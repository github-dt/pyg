package com.dt.pyg.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dt.pyg.common.pojo.PageResult;
import com.dt.pyg.mapper.TypeTemplateMapper;
import com.dt.pyg.pojo.Specification;
import com.dt.pyg.pojo.TypeTemplate;
import com.dt.pyg.sellergoods.service.TypeTemplateService;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;


/**
 * 服务实现层
 *
 */
@Service(interfaceName="com.dt.pyg.sellergoods.service.TypeTemplateService")
@Transactional(readOnly=false)
public class TypeTemplateServiceImpl implements TypeTemplateService {
	
	@Autowired
	private TypeTemplateMapper typeTemplateMapper;
	/**
	 * 分页查询类型模版
	 * @param typeTemplate 模版实体
	 * @param page 当前页码
	 * @param rows 每页显示的记录数
	 * @return PageResult
	 */
	public PageResult findByPage(TypeTemplate typeTemplate,
								 Integer page, Integer rows){
		try{

			/** 开始分页 */
			PageInfo<Specification> pageInfo = PageHelper.startPage(page, rows)
					.doSelectPageInfo(new ISelect() {
						@Override
						public void doSelect() {
							typeTemplateMapper.findAll(typeTemplate);
						}
					});
			PageResult pageResult = new PageResult();
			pageResult.setTotal(pageInfo.getTotal());
			pageResult.setRows(pageInfo.getList());
			return pageResult;

		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 添加类型模版 */
	public void saveTypeTemplate(TypeTemplate typeTemplate){
		try{
			typeTemplateMapper.insertSelective(typeTemplate);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 修改类型模版 */
	public void updateTypeTemplate(TypeTemplate typeTemplate){
		try{
			typeTemplateMapper.updateByPrimaryKeySelective(typeTemplate);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 删除类型模版 */
	public void deleteTypeTemplate(Long[] ids){
		try{
			// 创建Example
			Example example = new Example(TypeTemplate.class);
			// 创建条件对象
			Example.Criteria criteria = example.createCriteria();
			// 添加in 条件 where in (?,?)
			criteria.andIn("id", Arrays.asList(ids));
			typeTemplateMapper.deleteByExample(example);

		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}
	
}