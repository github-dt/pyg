package com.dt.pyg.search.listener;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.dt.pyg.pojo.Item;
import com.dt.pyg.search.service.ItemSearchService;
import com.dt.pyg.sellergoods.service.GoodsService;
import com.dt.pyg.solr.SolrItem;
import org.springframework.jms.listener.SessionAwareMessageListener;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemMessageListener implements SessionAwareMessageListener<MapMessage>{

    @Reference(timeout = 30000)
    private GoodsService goodsService;

    @Reference(timeout = 30000)
    private ItemSearchService itemSearchService;


    @Override
    public void onMessage(MapMessage mapMessage, Session session) throws JMSException {
        List<Long> ids = (List<Long>) mapMessage.getObject("ids");
        String status = mapMessage.getString("status");
        System.out.println("===ItemMessageListener===");
        System.out.println("ids:" + ids);
        System.out.println("status:" + status);

        // 获取审核通过的SKU商品数据       findItemsByGoodsIdAndStatus
        List<Item> itemList = goodsService.findItemsByGoodsIdAndStatus(
                ids.toArray(new Long[ids.size()]), status);

        // 判断集合
        if (itemList.size() > 0){
            // 把List<Item>转化成List<SolrItem>
            List<SolrItem> solrItems = new ArrayList<>();
            for (Item item : itemList){
                SolrItem solrItem = new SolrItem();
                solrItem.setTitle(item.getTitle());
                solrItem.setSpecMap(JSON
                        .parseObject(item.getSpec(),Map.class));
                solrItem.setUpdateTime(item.getUpdateTime());
                solrItem.setSeller(item.getSeller());
                solrItem.setPrice(item.getPrice());
                solrItem.setImage(item.getImage());
                solrItem.setGoodsId(item.getGoodsId());
                solrItem.setCategory(item.getCategory());
                solrItem.setBrand(item.getBrand());
                solrItem.setId(item.getId());
                solrItems.add(solrItem);
            }
            // 把SKU商品数据同步到索引库
            itemSearchService.saveOrUpdate(solrItems);
        }

    }
}
