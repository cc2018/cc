package cc.upms.service.api;

import cc.upms.entitys.UserInfo;

public interface UserInfoService {
    /**通过username查找用户信息;*/
    public UserInfo findByUserName(String userName);
}
