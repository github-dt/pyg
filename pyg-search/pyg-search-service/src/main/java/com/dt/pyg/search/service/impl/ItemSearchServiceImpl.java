package com.dt.pyg.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dt.pyg.search.service.ItemSearchService;
import com.dt.pyg.solr.SolrItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
            // 判断关键字
            /** 判断检索关键字是否为空 */
            if (StringUtils.isNoneBlank(keywords)) { // 高亮查询
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

                // 设置商品分类
                data.put("categoryList", categoryList);
                // 获取检索的结果
                data.put("rows", highlightPage.getContent());
            } else {

                // 创建查询对象
                Query query = new SimpleQuery("*:*");

                /** 分页查询，得到分页对象 */
                ScoredPage scoredPage = solrTemplate.queryForPage(query, SolrItem.class);
                // 获取检索的结果
                data.put("rows", scoredPage.getContent());
                data.put("categoryList", null);
            }
            return data;

        } catch (Exception ex) {
            throw new RuntimeException(ex);
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
