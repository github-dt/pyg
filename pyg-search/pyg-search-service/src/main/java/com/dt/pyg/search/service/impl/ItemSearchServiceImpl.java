package com.dt.pyg.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dt.pyg.search.service.ItemSearchService;
import com.dt.pyg.solr.SolrItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 搜索服务接口实现
 */
@Service // 不需要加事务，因为我们访问的是索引库
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;



    /**
     * 搜索方法
     */
    public Map<String, Object> search(Map<String, Object> params) {
        try {
            // 定义Map集合封装，最终返回的数据
            Map<String, Object> data = new HashMap<>();

            // 获取关键字
            String keywords = (String) params.get("keywords");

            /** 获取当前页码 */
            Integer page = (Integer)params.get("page");
            if(page == null){
                /** 默认第一页 */
                page = 1;
            }
            /** 获取每页显示的记录数 */
            Integer rows = (Integer)params.get("rows");
            if(rows == null){
                /** 默认20条记录 */
                rows = 20;
            }


            // 判断关键字
            /** 判断检索关键字是否为空 */
            if (StringUtils.isNoneBlank(keywords)) { // 高亮查询

                /** 关键字空格处理 */
                keywords = keywords.replace(" ", "");


                /** 创建高亮查询对象 */
                HighlightQuery highlightQuery = new SimpleHighlightQuery();
                /** 创建高亮选项对象 */
                HighlightOptions highlightOptions = new HighlightOptions();
                /** 设置高亮字段 */
                highlightOptions.addField("title");
                /** 设置高亮前缀 */
                highlightOptions.setSimplePrefix("<font color='red'>");
                /** 设置高亮后缀 */
                highlightOptions.setSimplePostfix("</font>");
                /** 设置高亮选项 */
                highlightQuery.setHighlightOptions(highlightOptions);
                /** 创建查询条件对象 */
                Criteria criteria = new Criteria("keywords").is(keywords);
                /** 添加查询条件(关键字) */
                highlightQuery.addCriteria(criteria);

                /** 按商品分类过滤 */
                if(!"".equals(params.get("category"))){
                    Criteria criteria1 = new Criteria("category")
                            .is(params.get("category"));
                    /** 添加过滤条件 */
                    highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
                }

                /** 按品牌过滤 */
                if(!"".equals(params.get("brand"))){
                    Criteria criteria1 = new Criteria("brand")
                            .is(params.get("brand"));
                    /** 添加过滤条件 */
                    highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
                }

                /** 按规格过滤 */
                if(params.get("spec") != null){
                    Map<String, String> specMap = (Map)params.get("spec");
                    for(String key : specMap.keySet()){
                        Criteria criteria1 =
                                new Criteria("spec_" + key).is(specMap.get(key));
                        /** 添加过滤条件 */
                        highlightQuery.addFilterQuery(new
                                SimpleFilterQuery(criteria1));
                    }
                }

                /** 按价格过滤 */
                if(!"".equals(params.get("price"))){
                    /** 得到价格范围数组 */
                    String[] price =
                            params.get("price").toString().split("-");
                    /** 如果价格区间起点不等于0 */
                    if(!price[0].equals("0")){
                        Criteria criteria1 = new
                                Criteria("price").greaterThanEqual(price[0]);
                        highlightQuery.addFilterQuery(new
                                SimpleFilterQuery(criteria1));
                    }
                    /** 如果价格区间终点不等于星号 */
                    if(!price[1].equals("*")){
                        Criteria criteria1 = new
                                Criteria("price").lessThanEqual(price[1]);
                        highlightQuery.addFilterQuery( new
                                SimpleFilterQuery(criteria1));
                    }
                }

                /** 添加排序 */
                String sortValue = (String) params.get("sort"); // ASC  DESC
                String sortField = (String) params.get("sortField"); // 排序字段
                if(StringUtils.isNoneBlank(sortValue)){
                    Sort sort = new Sort("ASC".equalsIgnoreCase(sortValue) ?
                            Sort.Direction.ASC : Sort.Direction.DESC, sortField);
                    /** 增加排序 */
                    highlightQuery.addSort(sort);
                }



                /** 设置起始记录查询数 */
                highlightQuery.setOffset((page - 1) * rows);
                /** 设置每页显示记录数 */
                highlightQuery.setRows(rows);


                /** 分页查询，得到高亮分页查询对象 */
                HighlightPage<SolrItem> highlightPage = solrTemplate
                        .queryForHighlightPage(highlightQuery, SolrItem.class);
                /** 循环高亮项集合 */
                for (HighlightEntry<SolrItem> he : highlightPage.getHighlighted()) {
                    /** 获取检索到的原实体 */
                    SolrItem solrItem = he.getEntity();
                    /** 判断高亮集合及集合中第一个Field的高亮内容 */
                    if (he.getHighlights().size() > 0
                            && he.getHighlights().get(0)
                            .getSnipplets().size() > 0) {
                        /** 设置高亮的结果 */
                        // 取第一个高亮Field高亮内容，再取第一个值
                        String title = he.getHighlights().get(0).getSnipplets().get(0);
                        System.out.println("高亮内容：" + title);
                        solrItem.setTitle(title);
                    }
                }


                /** 根据关键字分组查询商品分类 */
                List<String> categoryList = searchCategoryList(keywords);


                /** 获取商品分类名称 */
                String categoryName = (String)params.get("category");
                /** 判断商品分类名称 */
                if(StringUtils.isNoneBlank(categoryName)){
                /** 查询品牌和规格选项数据 */
                    data.putAll(searchBrandAndSpecList(categoryName));
                }else{
                    /** 查询品牌和规格选项数据 */
                    if(categoryList.size() > 0){
                        /** 只查询第一个商品分类名称对应的品牌与规格选项数据 */
                        data.putAll(searchBrandAndSpecList(categoryList.get(0)));
                    }
                }

                /** 设置总页数 */
                data.put("totalPages", highlightPage.getTotalPages());
                /** 设置总记录数 */
                data.put("total", highlightPage.getTotalElements());

                // 设置商品分类
                data.put("categoryList", categoryList);
                // 获取检索的结果
                data.put("rows", highlightPage.getContent());
            } else {

                // 创建查询对象
                Query query = new SimpleQuery("*:*");
                /** 设置起始记录查询数 */
                query.setOffset((page - 1) * rows);
                /** 设置每页显示记录数 */
                query.setRows(rows);

                /** 分页查询，得到分页对象 */
                ScoredPage scoredPage = solrTemplate.queryForPage(query, SolrItem.class);
                // 获取检索的结果
                data.put("rows", scoredPage.getContent());
                /** 设置总页数 */
                data.put("totalPages", scoredPage.getTotalPages());
                /** 设置总记录数 */
                data.put("total", scoredPage.getTotalElements());

//                data.put("categoryList", null);
            }
            return data;

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /** 添加或修改索引库 */
    @Override
    public void saveOrUpdate(List<SolrItem> solrItems) {
        /** 添加或修改 */
        UpdateResponse updateResponse = solrTemplate.saveBeans(solrItems);
        /** 判断状态码 */
        if (updateResponse.getStatus() == 0){
            solrTemplate.commit();
        }else{
            solrTemplate.rollback();
        }

    }

    @Override
    public void delete(List<Long> goodsIds) {
        System.out.println("删除商品SPU的ID：" + goodsIds);
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("goodsId").in(goodsIds);
        query.addCriteria(criteria);
        UpdateResponse updateResponse = solrTemplate.delete(query);
        if (updateResponse.getStatus() == 0){
            solrTemplate.commit();
        }else{
            solrTemplate.rollback();
        }
    }

    /** 根据分类名称查询品牌和规格选项数据(Redis) */
    private Map<String,Object> searchBrandAndSpecList(String category) {
        Map<String, Object> data = new HashMap<>();
        /** 根据分类名称从Redis中获取类型模版id */
        Long typeId = (Long)redisTemplate
                .boundHashOps("itemCats").get(category);
        if (typeId != null && typeId > 0) {
            /** 根据类型模版id从Redis中获取品牌数据 */
            List brandList = (List) redisTemplate
                    .boundHashOps("brandList").get(typeId);
            data.put("brandList", brandList);

            /** 根据类型模版id从Redis中获取规格选项数据 */
            List specList = (List) redisTemplate
                    .boundHashOps("specList").get(typeId);
            data.put("specList", specList);
        }
        return data;

    }

    /** 通过关键字分组查询商品分类数据 */
    private List<String> searchCategoryList(String keywords) {
        /** 创建集合封装商品分类数据 */
        List<String> categoryList = new ArrayList<>();
        /** 创建简单查询对象 */
        Query query = new SimpleQuery();
        /** 按关键字查询 */
        Criteria criteria = new Criteria("keywords").is(keywords);
        /** 设置查询条件 */
        query.addCriteria(criteria);
        /** 创建分组选项 */
        GroupOptions groupOptions = new GroupOptions()
                .addGroupByField("category");
        /** 设置分组选项 */
        query.setGroupOptions(groupOptions);
        /** 分组分页检索 */
        GroupPage<SolrItem> groupPage =
                solrTemplate.queryForGroupPage(query, SolrItem.class);
        /** 根据Field获取指定的分组结果集 */
        GroupResult<SolrItem> groupResult = groupPage
                .getGroupResult("category");
        /** 得到分组项的内容集合 */
        List<GroupEntry<SolrItem>> data = groupResult
                .getGroupEntries().getContent();
        /** 迭代分组项的元素 */
        for (GroupEntry<SolrItem> entry : data){
            /** 将分组值存入返回的集合中 */
            categoryList.add(entry.getGroupValue());
        }
        return categoryList;
    }
}
