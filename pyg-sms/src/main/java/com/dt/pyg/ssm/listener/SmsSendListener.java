package com.dt.pyg.ssm.listener;

import com.dt.pyg.ssm.util.SmsSendUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/** 短信发送消息监听类 */
@Component
public class SmsSendListener {
    @Autowired
    private SmsSendUtil smsSendUtil;
    /** 监听消息，发送短信 */
    @JmsListener(destination = "sms")
    public void sendSms(Map<String, String> map){
        try {
            // 发送短信
            boolean success = smsSendUtil.send(
                    map.get("phoneNum"),
                    map.get("signName"),
                    map.get("templateCode"),
                    map.get("message"));
            System.out.println("是否发送成功：" + success);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
