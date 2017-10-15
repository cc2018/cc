package cc.upms.entitys;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="upms_role_permission")
public class RolePermission implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="role_permission_id")
    private Long rolePermissionId;

    @Column(name="role_id")
    private Long roleId;

    @Column(name="permission_id")
    private Long permissionId;
}
