package com.dt.pyg.ssm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class SmsController {
    @Autowired
    private JmsTemplate jmsTemplate;
    @GetMapping("/send")
    public void sendSms(){
        Map<String, String> map = new HashMap<>();
        map.put("phoneNum", "15217165869");
        map.put("templateCode", "SMS_136800077");
        map.put("signName", "湛江扛霸子");
        map.put("message", "{\"code\":\"520520\"}");
        jmsTemplate.convertAndSend("sms", map);
    }
}
