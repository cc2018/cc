package cc.upms.entitys;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="upms_permission")
public class Permission implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="permission_id")
    private Long permissionId;

    private Long system_id;
    private Long pid;
    private String name;
    private Byte type;
    @Column(name="permission_value")
    private String permissionValue;
    private String uri;
    private Byte status;
    @Column(name="create_time")
    private long createTime;
    private long orders;
}
