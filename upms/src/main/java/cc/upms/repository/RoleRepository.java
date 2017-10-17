package cc.upms.repository;

import cc.upms.domain.Role;
import cc.upms.domain.view.RoleView;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RoleRepository extends CrudRepository<Role,Long> {
    String findUserRole = "select r.roleId as roleId, r.name as name, r.title as title, r.description as description, r.orders as orders"
            + " from Role r where r.roleId in (select ur.roleId from UserRole ur where ur.userId=?1)";
    /**
     * 根据roleId获取Role
     * @param roleId
     * @return
     */
    public Role findByRoleId(Long roleId);

    /**
     * 根据userId获取Role
     * <p>HQL语句中表名应该是ORM映射的类名，字段也为成员变量，并且要as重命名为和view里面字段一一对应<br>
     * @param userId
     * @return
     */
    //
    @Query(findUserRole)
    List<RoleView> findUserRoleByUserId(Long userId);
}
