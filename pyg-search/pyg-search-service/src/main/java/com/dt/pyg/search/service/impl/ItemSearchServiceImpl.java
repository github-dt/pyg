package com.dt.pyg.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dt.pyg.search.service.ItemSearchService;
import com.dt.pyg.solr.SolrItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;

import java.util.HashMap;
import java.util.Map;

/**
 * 搜索服务接口实现
 *
 */
@Service // 不需要加事务，因为我们访问的是索引库
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;


    /** 搜索方法 */
    public Map<String,Object> search(Map<String, Object> params){
        try{
            // 定义Map集合封装，最终返回的数据
            Map<String,Object> data = new HashMap<>();

            // 创建查询对象
            Query query = new SimpleQuery("*:*");

            // 获取关键字
            String keywords = (String)params.get("keywords");
            // 判断关键字
            if (StringUtils.isNoneBlank(keywords)){
                // 创建查询条件 keywords:keywords
                Criteria criteria = new Criteria("keywords").is(keywords);
                // 添加查询条件
                query.addCriteria(criteria);
            }

            /** 分页查询，得到分页对象 */
            ScoredPage scoredPage = solrTemplate.queryForPage(query, SolrItem.class);
            // 获取检索的结果
            data.put("rows", scoredPage.getContent());

            return data;

        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
