package cc.upms.constant;

public class UpmsConstant {

    // type
    public static final String UPMS_TYPE = "upms.type";
    // 会话key
    public final static String UPMS_SHIRO_SESSION_ID= "upms-shiro-session-id";
    // 全局会话key
    public final static String UPMS_SERVER_SESSION_ID = "upms-server-session-id";
    // 全局会话列表key
    public final static String UPMS_SERVER_SESSION_IDS = "upms-server-sessions-ids";
    // code key
    public final static String UPMS_SERVER_CODE = "upms-server-code";
    // 局部会话key
    public final static String UPMS_CLIENT_SESSION_ID = "upms-client-session-id";
    // 单点同一个code所有局部会话key
    public final static String UPMS_CLIENT_SESSION_IDS = "upms-client-sessions-ids";

    /*public static enum SessionType {
        session_server("server"), session_client("client");
        private final String info;

        private SessionType(String info) {
            this.info = info;
        }

        public String getInfo() {
            return info;
        }
    }*/
}
