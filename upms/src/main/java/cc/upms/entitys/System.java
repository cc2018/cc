package cc.upms.entitys;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="upms_system")
public class System implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="system_id")
    private Long systemId;

    private String basepath;
    private Byte status;
    private String name;
    private String title;
    private Byte description;
    @Column(name="create_time")
    private long createTime;
    private long orders;
}
