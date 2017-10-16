package cc.upms.service.impl;

import cc.upms.repository.PermissionDao;
import cc.upms.repository.UserInfoDao;
import cc.upms.domain.Permission;
import cc.upms.domain.Role;
import cc.upms.domain.UserInfo;
import cc.upms.service.api.UserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService {
    private static Logger log = LoggerFactory.getLogger(UserInfoService.class);

    @Autowired
    private UserInfoDao userInfoDao;

    @Autowired
    private PermissionDao permissionDao;

    @Override
    public UserInfo findByUserName(String userName) {
        return userInfoDao.findByUserName(userName);
    }

    @Override
    public List<Role> findRoleByUserId(Long userId) {
        return null;
    }

    @Override
    public List<Permission> findAllPermissionsByUserId(Long userId) {
        // 用户不存在或锁定状态
        UserInfo user = userInfoDao.findByUserId(userId);
        if (null == user || 1 == user.getLocked()) {
            log.info("findRoleByUserId : userId={}", userId);
            return null;
        }

        // List<UpmsPermission> upmsPermissions = upmsApiMapper.selectUpmsPermissionByUpmsUserId(upmsUserId);
        return null;
    }

    @Override
    public List<Permission> findRolePermissionsByUserId(Long userId) {
        return null;
    }

    @Override
    public List<Permission> findUserPermissionsByUserId(Long userId) {
        // 用户不存在或锁定状态
        UserInfo user = userInfoDao.findByUserId(userId);
        if (null == user || 1 == user.getLocked()) {
            log.info("findRoleByUserId : userId={}", userId);
            return null;
        }

        // return permissionDao.findUserPermissionsByUserId(userId);
        List<Permission> permission = permissionDao.findAll();

        return null;
    }
}
