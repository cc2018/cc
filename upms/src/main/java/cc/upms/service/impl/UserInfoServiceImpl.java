package cc.upms.service.impl;

import cc.upms.dao.UserInfoDao;
import cc.upms.entitys.UserInfo;
import cc.upms.service.api.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl implements UserInfoService {
    @Autowired
    private UserInfoDao userInfoDao;

    @Override
    public UserInfo findByUserName(String userName) {
        System.out.println("UserInfoServiceImpl.findByUsername()");
        return userInfoDao.findByUserName(userName);
    }
}
