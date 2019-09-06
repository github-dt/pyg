package com.dt.pyg.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dt.pyg.common.pojo.PageResult;
import com.dt.pyg.pojo.Goods;
import com.dt.pyg.sellergoods.service.GoodsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.*;
import java.util.Arrays;

/**
 * GoodsController
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference(timeout = 10000)
    private GoodsService goodsService;

    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination solrQueue;
    @Autowired
    private Destination solrDeleteQueue;
    @Autowired
    private Destination pageTopic;
    @Autowired
    private Destination pageDeleteTopic;



    /**
     * 搜索商品
     */
    @GetMapping("/search")
    public PageResult search(Goods goods,
                             @RequestParam("page") int page,
                             @RequestParam("rows") int rows) {
        try {
            /** 添加查询条件 */
            goods.setAuditStatus("0");
            /** GET请求中文转码 */
            if (StringUtils.isNoneBlank(goods.getGoodsName())) {
                goods.setGoodsName(new String(
                        goods.getGoodsName().getBytes("ISO8859-1"), "UTF-8"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        /** 调用服务层分页查询 */
        return goodsService.findGoodsByPage(goods, page, rows);
    }

    /**
     * 商品审批，修改商品状态
     */
    @GetMapping("/updateStatus")
    public boolean updateStatus(Long[] ids, String status) {
        try {
            goodsService.updateStatus(ids, status);

            /** 按照SPU ID查询 SKU数据(状态为1) */
            if (status.equals("1")) {//审核通过
                jmsTemplate.send(solrQueue
                        , new MessageCreator() {
                            @Override
                            public Message createMessage(Session session) throws JMSException {
                                MapMessage mapMessage = session.createMapMessage();
                                mapMessage.setObjectProperty("ids", Arrays.asList(ids));
                                mapMessage.setString("status", status);
                                return mapMessage;
                            }
                        });
            }
            /** 静态网页生成 */
            for(Long goodsId : ids){
                jmsTemplate.send(pageTopic, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session)
                            throws JMSException {
                        return session.createTextMessage(goodsId.toString());
                    }
                });
            }



            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 商品删除(修改删除状态)
     */
    @GetMapping("/delete")
    public boolean deleteGoods(Long[] ids) {
        try {
            /** 删除数据库数据 */
            goodsService.deleteGoods(ids);
            jmsTemplate.send(solrDeleteQueue, new MessageCreator() {
                @Override
                public Message createMessage(Session session)
                        throws JMSException {
                    return session.createObjectMessage(ids);
                }
            });

            /** 删除静态html页面 */
            jmsTemplate.send(pageDeleteTopic, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createObjectMessage(ids);
                }
            });


            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
