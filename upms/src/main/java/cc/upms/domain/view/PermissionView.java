package cc.upms.domain.view;

public interface PermissionView {
    Long getPermissionId();
    String getName();
    Byte getType();
    String getPermissionValue();
    String getUri();
    Byte getStatus();
    Long getOrders();
}