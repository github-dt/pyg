package com.dt.pyg.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.dt.pyg.common.pojo.PageResult;
import com.dt.pyg.mapper.SpecificationOptionMapper;
import com.dt.pyg.mapper.TypeTemplateMapper;
import com.dt.pyg.pojo.Specification;
import com.dt.pyg.pojo.SpecificationOption;
import com.dt.pyg.pojo.TypeTemplate;
import com.dt.pyg.sellergoods.service.TypeTemplateService;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 服务实现层
 *
 */
@Service(interfaceName="com.dt.pyg.sellergoods.service.TypeTemplateService")
@Transactional(readOnly=false)
public class TypeTemplateServiceImpl implements TypeTemplateService {
	
	@Autowired
	private TypeTemplateMapper typeTemplateMapper;

	@Autowired
	private SpecificationOptionMapper specificationOptionMapper;

	@Autowired
	private RedisTemplate redisTemplate;


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

	@Override
	public TypeTemplate findTypeTemplateById(Long id) {
		try{
			return typeTemplateMapper.selectByPrimaryKey(id);
		}catch(Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/**
	 * 根据模版id查询所有的规格与规格选项
	 * @param id 模版id
	 * @return List
	 */

	@Override
	public List<Map> findSpecByTemplateId(Long id) {
		try{
			/** 根据主键id查询模版 */
			TypeTemplate typeTemplate = findTypeTemplateById(id);
			/**
			 * [{"id":33,"text":"电视屏幕尺寸"}]
			 * 获取模版中所有的规格，转化成  List<Map>
			 */
			List<Map> specLists =
					JSON.parseArray(typeTemplate.getSpecIds(), Map.class);
			/** 迭代模版中所有的规格 */
			for (Map map : specLists){
				/** 创建查询条件对象 */
				SpecificationOption so = new SpecificationOption();
				so.setSpecId(Long.valueOf(map.get("id").toString()));
				/** 通过规格id，查询规格选项数据 */
				List<SpecificationOption> specOptions =
						specificationOptionMapper.select(so);
				map.put("options", specOptions);
			}
			return specLists;
		}catch(Exception ex){
			throw new RuntimeException(ex);
		}

	}

	@Override
	public void saveToRedis() {
		/** 查询全部类型模版数据 */
		List<TypeTemplate> typeTemplateList = typeTemplateMapper.selectAll();
		/** 循环集合 */
		for (TypeTemplate typeTemplate : typeTemplateList){
			/** 存储品牌列表 */
			List<Map> brandList = JSON.
					parseArray(typeTemplate.getBrandIds(), Map.class);
			redisTemplate.boundHashOps("brandList")
					.put(typeTemplate.getId(),brandList);

			/** 根据模版id查询规格与规格选项 */
			List<Map> specList = findSpecByTemplateId(typeTemplate.getId());
			/** 存储规格与规格选项列表 */
			redisTemplate.boundHashOps("specList")
					.put(typeTemplate.getId(), specList);
		}
	}

}