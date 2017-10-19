package cc.upms.config;

import cc.upms.constant.UpmsConstant;
import cc.upms.util.PropertiesFileUtil;

public class UpmsConfig {

    private static String type = PropertiesFileUtil.getInstance("upms").get("upms.type");
    private static Integer sessionTime = PropertiesFileUtil.getInstance("upms").getInt("upms.sessionTime");;
    private static String successUrl = PropertiesFileUtil.getInstance("upms").get("upms.successUrl");;
    private static String unauthorizedUrl = PropertiesFileUtil.getInstance("upms").get("upms.unauthorizedUrl");;
    private static Integer rememberMeTime = PropertiesFileUtil.getInstance("upms").getInt("upms.rememberMeTime");;

    public static String getType() {
        return type;
    }
    public static String getSessionId() {
        if (isClientType()) {
            return UpmsConstant.UPMS_CLIENT_SESSION_ID;
        } else {
            return UpmsConstant.UPMS_SERVER_SESSION_ID;
        }
    }
    public static Integer getSessionTime() {
        return sessionTime;
    }
    public static String getSuccessUrl() {
        return successUrl;
    }
    public static String getUnauthorizedUrl() {
        return unauthorizedUrl;
    }
    public static Integer getRememberMeTime() {
        return rememberMeTime;
    }
    public static boolean isClientType () {
        return type.equals("client") ;
    }
    public static boolean isServerType () {
        return type.equals("server") ;
    }
}
