package com.dt.pyg.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dt.pyg.common.pojo.PageResult;
import com.dt.pyg.mapper.SpecificationMapper;
import com.dt.pyg.mapper.SpecificationOptionMapper;
import com.dt.pyg.pojo.Specification;
import com.dt.pyg.pojo.SpecificationOption;
import com.dt.pyg.sellergoods.service.SpecificationService;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 规格服务接口实现层
 */
@Service(interfaceName="com.dt.pyg.sellergoods.service.SpecificationService")
@Transactional(readOnly=false)
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private SpecificationMapper specificationMapper;
	@Autowired
	private SpecificationOptionMapper specificationOptionMapper;

	/**
	 * 分页查询规格
	 * @param specification 规格实体
	 * @param page 当前页码
	 * @param rows 每页显示的记录数
	 * @return PageResult
	 */
	public PageResult findByPage(Specification specification,
								 Integer page, Integer rows){
		try{
			PageInfo<Object> pageInfo = PageHelper.startPage(page, rows).doSelectPageInfo(new ISelect() {
				@Override
				public void doSelect() {
					specificationMapper.findAll(specification);
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
	/** 添加规格与规格选项 */
	public void saveSpecification(Specification specification){
		try{
			specificationMapper.insertSelective(specification);
			//规格选项不为空时才去插入
			if(specification.getSpecificationOptions().size() != 0){
                specificationOptionMapper.save(specification);
            }
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 根据规格id查询规格选项 */
	public List<SpecificationOption> findOne(Long id){
		try{
			return specificationOptionMapper.findBySpecId(id);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

    /** 修改规格与规格选项  */
    public void updateSpecification(Specification specification){
        try{
			// 修改往规格表数据
			specificationMapper.updateByPrimaryKeySelective(specification);
			/**########### 修改规格选项表数据 ###########*/

			// 第一步：删除规格选项表中的数据 spec_id
			// delete from tb_specification_option where spec_id = ?
			// 创建规格选项对象，封装删除条件 通用Mapper
			SpecificationOption so = new SpecificationOption();
			so.setSpecId(specification.getId());
			specificationOptionMapper.delete(so);

			// 第二步：往规格选项表插入数据(如果规格选项为空就不插入)
			if(specification.getSpecificationOptions().size() != 0){
				specificationOptionMapper.save(specification);
			}

		}catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 删除规格与规格选项 */
    public void deleteSpecification(Long[] ids){
        try{
			for (Long id : ids){
				SpecificationOption specificationOption =
						new SpecificationOption();
				specificationOption.setSpecId(id);
				specificationOptionMapper.delete(specificationOption);
				specificationMapper.deleteByPrimaryKey(id);
			}
		}catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

	/** 查询规格列表(id,name) */
	public List<Map<String,Object>> findSpecByIdAndName(){
		try{
			try{
				return specificationMapper.findAllByIdAndName();
			}catch(Exception ex){
				throw new RuntimeException(ex);
			}

		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}
}