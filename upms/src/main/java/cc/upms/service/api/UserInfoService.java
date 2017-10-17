package cc.upms.service.api;

import cc.upms.domain.Permission;
import cc.upms.domain.Role;
import cc.upms.domain.UserInfo;
import cc.upms.domain.view.PermissionView;
import cc.upms.domain.view.RoleView;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserInfoService {
    /**
     * 根据userName获取UserInfo
     * @param userName
     * @return
     */
    public UserInfo findByUserName(String userName);

    /**
     * 根据用户id获取所属的角色
     * @param userId
     * @return
     */
    List<Role> findRoleByUserId(Long userId);

    /**
     * 根据用户id获取所拥有的权限(用户和角色权限合集)
     * @param userId
     * @return
     */
    List<Permission> findAllPermissionsByUserId(Long userId);

    /**
     * 根据角色id获取所拥有的角色权限
     * @param userId
     * @return
     */
    List<Permission> findRolePermissionsByUserId(Long userId);

    /**
     * 根据用户id获取所拥有的权限
     * @param userId
     * @return
     */
    List<PermissionView> findUserPermissionsByUserId(Long userId);

    /**
     * 根据userId获取Role
     * @param userId
     * @return
     */
    //
    List<RoleView> findUserRoleByUserId(Long userId);
}
