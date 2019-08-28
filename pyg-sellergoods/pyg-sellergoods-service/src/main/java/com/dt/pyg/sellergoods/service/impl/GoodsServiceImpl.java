package com.dt.pyg.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.dt.pyg.common.pojo.PageResult;
import com.dt.pyg.mapper.*;
import com.dt.pyg.pojo.Goods;
import com.dt.pyg.pojo.Item;
import com.dt.pyg.pojo.ItemCat;
import com.dt.pyg.sellergoods.service.GoodsService;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 */
@Service(interfaceName="com.dt.pyg.sellergoods.service.GoodsService")
/**
 * readOnly=false：该服务类中所有方法CRUD都可以做。
 readOnly=true：该服务类中所有方法只能做查询。
 rollbackFor=RuntimeException.class：该服务类中所有方法出现RuntimeException异常时事务才有效。

 */
@Transactional(readOnly=false,rollbackFor = RuntimeException.class)
public class GoodsServiceImpl implements GoodsService {

	/** 注入数据访问层代理对象 */
	@Autowired
	private GoodsMapper goodsMapper;
	@Autowired
	private GoodsDescMapper goodsDescMapper;
	@Autowired
	private ItemMapper itemMapper;
	@Autowired
	private BrandMapper brandMapper;
	@Autowired
	private ItemCatMapper itemCatMapper;
	@Autowired
	private SellerMapper sellerMapper;



	@Override
	public void saveGoods(Goods goods) {
		try{
			/** 设置未申核状态 */
			goods.setAuditStatus("0");
			goodsMapper.insertSelective(goods);

			goods.getGoodsDesc().setGoodsId(goods.getId());
			goodsDescMapper.insertSelective(goods.getGoodsDesc());

            /** 添加sku商品信息 */
            saveItems(goods);
		}catch(Exception ex){
			throw new RuntimeException(ex);
		}

	}

    @Override
    public PageResult findGoodsByPage(Goods goods, Integer page, Integer rows) {
        try{
            /** 开始分页 */
            PageInfo<Map<String,Object>> pageInfo =
                    PageHelper.startPage(page, rows)
                            .doSelectPageInfo(new ISelect() {
                                @Override
                                public void doSelect() {
                                    goodsMapper.findGoodsByWhere(goods);
                                }
                            });
            /** 循环查询到的商品 */
            for (Map<String,Object> map : pageInfo.getList()){
                ItemCat itemCat1 =
                        itemCatMapper.selectByPrimaryKey(map.get("category1Id"));
                map.put("category1Name", itemCat1 != null ? itemCat1.getName() : "");
                ItemCat itemCat2 =
                        itemCatMapper.selectByPrimaryKey(map.get("category2Id"));
                map.put("category2Name", itemCat2 != null ? itemCat2.getName() : "");
                ItemCat itemCat3 =
                        itemCatMapper.selectByPrimaryKey(map.get("category3Id"));
                map.put("category3Name", itemCat3 != null ? itemCat3.getName() : "");
            }
            return new PageResult(pageInfo.getTotal(),pageInfo.getList());
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }

    }

	@Override
	public Goods findOne(Long id) {
		try{
			Goods goods = goodsMapper.selectByPrimaryKey(id);
			goods.setGoodsDesc(goodsDescMapper.selectByPrimaryKey(id));

            /** 创建Item对象封装查询条件 */
            Item item = new Item();
            item.setGoodsId(id);
            List<Item> items = itemMapper.select(item);
            goods.setItems(items);

            return goods;
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}

	}

    @Override
    public void updateGoods(Goods goods) {
        try{
            /** 设置审核状态为：未审核 */
            goods.setAuditStatus("0");
            /** 修改SPU商品表 */
            goodsMapper.updateByPrimaryKeySelective(goods);
            /** 修改商品描述表 */
            goodsDescMapper
                    .updateByPrimaryKeySelective(goods.getGoodsDesc());
            /** 删除原有SKU具体商品数据   */
            Item item = new Item();
            item.setGoodsId(goods.getId());
            itemMapper.delete(item);
            /** 重新添加SKU具体商品数据  */
            saveItems(goods);
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }

    }

    /**
     * 批量修改状态
     * @param ids 多个商品id
     * @param status 状态码
     */

    @Override
    public void updateStatus(Long[] ids, String status) {
        try{
            goodsMapper.updateStatus(ids, status);
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }

    }

    /** 批量删除商品(修改删除状态) */
    @Override
    public void deleteGoods(Long[] ids) {
        try{
/** 修改删除状态 */
            goodsMapper.updateDeleteStatus(ids, "1");
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 保存sku具体商品信息 */
    private void saveItems(Goods goods) {
        // 判断是否启用规格
        if ("1".equals(goods.getIsEnableSpec())) { // 启用规格

            /** 往tb_item表插入数据 (SKU表) goods.items */
            for (Item item : goods.getItems()) {
                // item: {spec:{}, price:0, num:9999, status:'0', isDefault:'0'}

                // 设置SKU商品的标题 : Apple iPhone 8 Plus (A1864) 联通4G 64G
                StringBuilder title = new StringBuilder(goods.getGoodsName());
                // spec: {"网络":"联通4G","机身内存":"64G"}
                // 把spec的json字符串转化成map集合
                Map specMap = JSON.parseObject(item.getSpec());
                // 循环map集合的value
                for (Object spec : specMap.values()) {
                    title.append(" " + spec.toString());
                }
                item.setTitle(title.toString());

                // 设置SKU商品的其它信息
                setItemInfo(goods, item);

                // 往tb_item表插入数据（SKU表）
                itemMapper.insertSelective(item);
            }
        }else{ // 没有启用规格

            // 创建Item
            Item item = new Item();
            // {spec:{}, price:0, num:9999, status:'0', isDefault:'0'}
            // 设置SKU商品价格
            item.setPrice(goods.getPrice());
            // 设置SKU商品库存数据
            item.setNum(100);
            // 设置SKU商品的状态码
            item.setStatus("1");
            // 设置SKU商品是否为默认
            item.setIsDefault("1");
            // 设置SKU商品的规格
            item.setSpec("{}");
            // 设置SKU商品标题
            item.setTitle(goods.getGoodsName());

            // 设置SKU商品的其它信息
            setItemInfo(goods, item);

            // 往tb_item表插入数据（SKU表）
            itemMapper.insertSelective(item);
        }
    }

    /** 设置SKU商品信息 */
	private void setItemInfo(Goods goods,Item item) {
		/** 设置SKU商品图片地址 */
		List<Map> imageList = JSON.parseArray(
				goods.getGoodsDesc().getItemImages(), Map.class);
		if (imageList != null && imageList.size() > 0){
			/** 取第一张图片 */
			item.setImage((String)imageList.get(0).get("url"));
		}
		/** 设置SKU商品的分类(三级分类) */
		item.setCategoryid(goods.getCategory3Id());
		/** 设置SKU商品的创建时间 */
		item.setCreateTime(new Date());
		/** 设置SKU商品的修改时间 */
		item.setUpdateTime(item.getCreateTime());
		/** 设置SPU商品的编号 */
		item.setGoodsId(goods.getId());
		/** 设置商家编号 */
		item.setSellerId(goods.getSellerId());
		/** 设置分类名称 */
		item.setCategory(itemCatMapper
				.selectByPrimaryKey(goods.getCategory3Id()).getName());
		/** 设置品牌名称 */
		item.setBrand(brandMapper
				.selectByPrimaryKey(goods.getBrandId()).getName());
		/** 设置商家店铺名称 */
		item.setSeller(sellerMapper.selectByPrimaryKey(
				goods.getSellerId()).getNickName());

	}
}