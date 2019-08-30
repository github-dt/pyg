package com.dt.pyg.search.service;

import java.util.Map; /**
 * 搜索服务接口
 *
 */
public interface ItemSearchService {

    /** 搜索方法 */
    Map<String,Object> search(Map<String, Object> params);
}
