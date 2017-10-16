package cc.upms.repository;

import cc.upms.domain.Permission;
import cc.upms.domain.view.PermissionView;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface PermissionDao extends PagingAndSortingRepository<Permission,Long> {
    /**
     * 根据permissionId获取Permission
     * @param permissionId
     * @return
     */
    public Permission findByPermissionId(Long permissionId);

    /**
     * 获取所有权限
     * @example Page<Permission> permissions = permissionDao.findAll(new PageRequest(1, 20));
     * @return
     */
    List<Permission> findAll();

    /**
     * 根据userId获取Permission
     * @param userId
     * @return
     */
    @Query("select p.permission_id, p.name, p.type, p.permission_value, p.uri, p.status, p.orders from upms_permission p where p.permission_id in (select up.permission_id from upms_user_permission up where up.user_id = ?1)")
    public List<PermissionView> findUserPermissionsByUserId(Long userId);
}
