package cc.upms.entitys;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="upms_user_permission")
public class UserPermission implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="user_permission_id")
    private Long userPermissionId;

    @Column(name="user_id")
    private Long userId;

    @Column(name="permission_id")
    private Long permissionId;

    private Byte type;
}
