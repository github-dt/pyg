package com.dt.pyg.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dt.pyg.common.pojo.PageResult;
import com.dt.pyg.pojo.Specification;
import com.dt.pyg.pojo.SpecificationOption;
import com.dt.pyg.sellergoods.service.SpecificationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 规格处理器
 * @author LEE.SIU.WAH
 */
@RestController
@RequestMapping("/specification")
public class SpecificationController {

	@Reference(timeout = 10000)
	private SpecificationService specificationService;

	/** 分页查询规格 */
	@GetMapping("/findByPage")
	public PageResult findByPage(Specification specification,
								 Integer page, Integer rows){
		try {
			/** GET请求参数转码 */
			if (specification != null && StringUtils.
					isNoneBlank(specification.getSpecName())) {
				specification.setSpecName(new String(specification
						.getSpecName().getBytes("ISO8859-1"), "UTF-8"));
			}

			return specificationService.findByPage(specification, page, rows);
		}catch (Exception ex){
			ex.printStackTrace();
		}
		return null;
	}

	/** 添加规格与规格选项 */
	@PostMapping("/save")
	public boolean saveSpecification(@RequestBody Specification specification){
		try{
			specificationService.saveSpecification(specification);
			return true;
		}catch (Exception ex){
			ex.printStackTrace();
		}
		return false;
	}

	/** 根据规格id查询规格选项 */
	@GetMapping("/findOne")
	public List<SpecificationOption> findOne(Long id){
		return specificationService.findOne(id);
	}


    /** 修改规格与规格选项 */
    @PostMapping("/update")
    public boolean updateSpecification(@RequestBody Specification specification){
        try{
            specificationService.updateSpecification(specification);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /** 删除规格与规格选项 */
    @GetMapping("/delete")
    public boolean deleteSpecification(Long[] ids){
		try{
			specificationService.deleteSpecification(ids);
			return true;
		}catch (Exception ex){
			ex.printStackTrace();
		}
		return false;
	}

	/** 查询规格列表(id,name) */
	@GetMapping("/selectSpecList")
	public List<Map<String, Object>> selectSpecList(){
		return specificationService.findSpecByIdAndName();
	}
}
