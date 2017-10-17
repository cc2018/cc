package cc.upms.repository;

import cc.upms.domain.UserPermission;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserPermissionRepository extends CrudRepository<UserPermission,Long> {
    /**
     * 根据userId获取UserPermission
     * @param userId
     * @return
     */
    public List<UserPermission> findUserPermissionsByUserId(Long userId);
}
