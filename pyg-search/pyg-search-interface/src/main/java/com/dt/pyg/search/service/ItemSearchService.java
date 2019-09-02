package com.dt.pyg.search.service;

import com.dt.pyg.solr.SolrItem;

import java.util.List;
import java.util.Map; /**
 * 搜索服务接口
 *
 */
public interface ItemSearchService {

    /** 搜索方法 */
    Map<String,Object> search(Map<String, Object> params);

    void saveOrUpdate(List<SolrItem> solrItems);

    void delete(List<Long> longs);
}
