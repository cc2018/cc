package cc.upms.entitys;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="upms_role")
public class Role implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="role_id")
    private Long roleId;

    private String name;
    private String title;
    private Byte description;
    @Column(name="create_time")
    private long createTime;
    private long orders;
}
