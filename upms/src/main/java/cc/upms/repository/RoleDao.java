package cc.upms.repository;

import cc.upms.domain.Role;
import org.springframework.data.repository.CrudRepository;

public interface RoleDao extends CrudRepository<Role,Long> {
    /**
     * 根据roleId获取Role
     * @param roleId
     * @return
     */
    public Role findByRoleId(Long roleId);
}
