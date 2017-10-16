package cc.upms.domain.view;

public interface PermissionView {
    public Long getPermissionId();
    // public Long getSystemId();
    // public Long getPid();
    public String getName();
    public Byte getType();
    public String getPermissionValue();
    public String getUri();
    public Byte getStatus();
    public Long getOrders();
}