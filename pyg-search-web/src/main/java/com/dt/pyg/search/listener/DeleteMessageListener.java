package com.dt.pyg.search.listener;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dt.pyg.search.service.ItemSearchService;
import org.springframework.jms.listener.SessionAwareMessageListener;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.util.Arrays;

/** 商品消息监听器(删除) */
public class DeleteMessageListener implements SessionAwareMessageListener<ObjectMessage> {
    @Reference(timeout=30000)
    private ItemSearchService itemSearchService;
    @Override
    public void onMessage(ObjectMessage objectMessage,
                          Session session) throws JMSException {
        Long[] ids = (Long[])objectMessage.getObject();
        System.out.println("===DeleteMessageListener===");
        System.out.println("ids:" + ids);
        //删除solr索引库
        itemSearchService.delete(Arrays.asList(ids));
    }

}
