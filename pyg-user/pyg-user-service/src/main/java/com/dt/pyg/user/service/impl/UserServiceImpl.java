package com.dt.pyg.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dt.pyg.mapper.UserMapper;
import com.dt.pyg.pojo.User;
import com.dt.pyg.user.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.*;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service(interfaceName = "com.dt.pyg.user.service.UserService")
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination smsQueue;
    @Value("${templateCode}")
    private String templateCode;
    @Value("${signName}")
    private String signName;


    @Override
    public void save(User user) {
        try {
            // 创建日期
            user.setCreated(new Date());
            // 修改日期
            user.setUpdated(user.getCreated());
            // 密码加密
            user.setPassword(DigestUtils.md5Hex(user.getPassword()));
            userMapper.insertSelective(user);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void sendSmsCode(String phone) {
        String code = UUID.randomUUID().toString()
                .replaceAll("-", "")
                .replaceAll("[a-z|A-Z]", "")
                .substring(0, 6);
        System.out.println("验证码是： "+code);
        //发送验证码到手机
        //发送消息到消息中间件

        jmsTemplate.send(smsQueue, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("phoneNum", phone);
                mapMessage.setString("templateCode",templateCode );
                mapMessage.setString("signName",signName );
                mapMessage.setString("message","{\"code\":\""+code+"\"}" );
                return mapMessage;
            }
        });

        // 把验证码存入redis(90秒)
        redisTemplate.boundValueOps(phone).set(code, 90, TimeUnit.SECONDS);

    }

    @Override
    public boolean checkSmsCode(String phone, String code) {
        /** 获取Redis中存储的验证码 */
        String redisCode = (String) redisTemplate.boundValueOps(phone).get();
        return StringUtils.isNoneBlank(redisCode) && redisCode.equals(code);

    }

}

