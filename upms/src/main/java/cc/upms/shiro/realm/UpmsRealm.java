package cc.upms.shiro.realm;

import cc.upms.domain.UserInfo;
import cc.upms.domain.view.PermissionView;
import cc.upms.domain.view.RoleView;
import cc.upms.service.api.UserInfoService;
import cc.upms.util.MD5Util;
import cc.upms.util.PropertiesFileUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.lang.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UpmsRealm extends AuthorizingRealm {

    private static Logger _log = LoggerFactory.getLogger(UpmsRealm.class);

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 授权：验证权限时调用
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String userName = (String) principalCollection.getPrimaryPrincipal();
        UserInfo userInfo = userInfoService.findByUserName(userName);

        // 当前用户所有角色
        List<RoleView> roleViews = userInfoService.findUserRoleByUserId(userInfo.getUserId());
        Set<String> roles = new HashSet<>();
        for (RoleView role : roleViews) {
            if (StringUtils.isNotBlank(role.getName())) {
                roles.add(role.getName());
            }
        }

        // 当前用户所有权限
        List<PermissionView> permissionViews = userInfoService.findUserPermissionsByUserId(userInfo.getUserId());
        Set<String> permissions = new HashSet<>();
        for (PermissionView permission : permissionViews) {
            if (StringUtils.isNotBlank(permission.getPermissionValue())) {
                permissions.add(permission.getPermissionValue());
            }
        }

        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.setStringPermissions(permissions);
        simpleAuthorizationInfo.setRoles(roles);
        return simpleAuthorizationInfo;
    }

    /**
     * 认证：登录时调用
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String userName = (String) authenticationToken.getPrincipal();
        String password = new String((char[]) authenticationToken.getCredentials());
        // client无密认证
        String upmsType = PropertiesFileUtil.getInstance("upms").get("upms.type");
        if ("client".equals(upmsType)) {
            return new SimpleAuthenticationInfo(userName, password, getName());
        }

        // 查询用户信息
        UserInfo upmsUser = userInfoService.findByUserName(userName);

        if (null == upmsUser) {
            throw new UnknownAccountException();
        }
        if (!upmsUser.getPassword().equals(MD5Util.MD5(password + upmsUser.getSalt()))) {
            throw new IncorrectCredentialsException();
        }
        if (upmsUser.getLocked() == 1) {
            throw new LockedAccountException();
        }

        return new SimpleAuthenticationInfo(userName, password, getName());
    }

}
