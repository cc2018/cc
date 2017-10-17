package cc.upms.repository;

import cc.upms.domain.Permission;
import cc.upms.domain.view.PermissionView;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PermissionRepository extends CrudRepository<Permission,Long> {
    String findUserPermission = "select p.permissionId as permissionId, p.name as name, p.type as type,"
            + " p.permissionValue as permissionValue, p.uri as uri, p.status as status, p.orders as orders"
            + " from Permission p where p.permissionId in (select up.permissionId from UserPermission up where up.userId=?1)";

    /**
     * 根据permissionId获取Permission
     * @param permissionId
     * @return
     */
    Permission findByPermissionId(Long permissionId);

    /**
     * 获取所有权限
     * @example Page<Permission> permissions = permissionDao.findAll(new PageRequest(1, 20));
     * @return
     */
    List<Permission> findAll();

    /**
     * 根据userId获取Permission
     * <p>HQL语句中表名应该是ORM映射的类名，字段也为成员变量，并且要as重命名为和view里面字段一一对应<br>
     * @param userId
     * @return permission view 集合
     */
    //
    @Query(findUserPermission)
    List<PermissionView> findUserPermissionsByUserId(Long userId);
}
