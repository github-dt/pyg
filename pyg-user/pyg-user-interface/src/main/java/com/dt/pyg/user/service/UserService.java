package com.dt.pyg.user.service;

import com.dt.pyg.pojo.User;

public interface UserService {
    void save(User user);

    void sendSmsCode(String phone);

    boolean checkSmsCode(String phone, String smsCode);
}
