package cc.upms.shiro.session;

import cc.upms.config.UpmsConfig;
import cc.upms.constant.UpmsConstant;
import cc.upms.util.RedisUtil;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.*;

public class UpmsSessionDao extends CachingSessionDAO {

    @Autowired
    RedisUtil redisUtil;

    private static Logger _log = LoggerFactory.getLogger(UpmsSessionDao.class);

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        redisUtil.set(UpmsConstant.UPMS_SHIRO_SESSION_ID + "_" + sessionId, UpmsSession.serialize(session), (int)session.getTimeout() / 1000);
        _log.debug("doCreate >>>>> sessionId={}", sessionId);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        String session = redisUtil.get(UpmsConstant.UPMS_SHIRO_SESSION_ID + "_" + sessionId);
        _log.debug("doReadSession >>>>> sessionId={}", sessionId);
        return UpmsSession.deserialize(session);
    }

    @Override
    protected void doUpdate(Session session) {
        // 如果会话过期/停止 没必要再更新了
        if(session instanceof ValidatingSession && !((ValidatingSession)session).isValid()) {
            return;
        }
        // 更新session的最后一次访问时间
        UpmsSession upmsSession = (UpmsSession) session;
        UpmsSession cacheUpmsSession = (UpmsSession) doReadSession(session.getId());
        if (null != cacheUpmsSession) {
            upmsSession.setStatus(cacheUpmsSession.getStatus());
            upmsSession.setAttribute("FORCE_LOGOUT", cacheUpmsSession.getAttribute("FORCE_LOGOUT"));
        }
        redisUtil.set(UpmsConstant.UPMS_SHIRO_SESSION_ID + "_" + session.getId(), UpmsSession.serialize(session), (int) session.getTimeout() / 1000);
        _log.debug("doUpdate >>>>> sessionId={}", session.getId());
    }

    @Override
    protected void doDelete(Session session) {
        String sessionId = session.getId().toString();
        if (UpmsConfig.isClientType()) {
            // 删除局部会话和同一code注册的局部会话
            String code = redisUtil.get(UpmsConstant.UPMS_CLIENT_SESSION_ID + "_" + sessionId);
            redisUtil.remove(UpmsConstant.UPMS_CLIENT_SESSION_ID + "_" + sessionId);

            // 只删除这个code下 这个子系统的client session id
            redisUtil.lrem(UpmsConstant.UPMS_CLIENT_SESSION_IDS + "_" + code, 1, sessionId);
        }
        if (UpmsConfig.isServerType()) {
            // 当前全局会话code
            String code = redisUtil.get(UpmsConstant.UPMS_SERVER_SESSION_ID + "_" + sessionId);
            // 清除全局会话
            redisUtil.remove(UpmsConstant.UPMS_SERVER_SESSION_ID + "_" + sessionId);
            // 清除code校验值
            redisUtil.remove(UpmsConstant.UPMS_SERVER_CODE + "_" + code);
            // 清除所有局部会话
            Set<String> clientSessionIds = redisUtil.smembers(UpmsConstant.UPMS_CLIENT_SESSION_IDS + "_" + code);
            for (String clientSessionId : clientSessionIds) {
                redisUtil.remove(UpmsConstant.UPMS_CLIENT_SESSION_ID + "_" + clientSessionId);
                redisUtil.srem(UpmsConstant.UPMS_CLIENT_SESSION_IDS + "_" + code, clientSessionId);
            }
            _log.debug("当前code={}，对应的注册系统个数：{}个", code, redisUtil.ssize(UpmsConstant.UPMS_CLIENT_SESSION_IDS + "_" + code));
            // 维护会话id列表，提供会话分页管理
            redisUtil.lrem(UpmsConstant.UPMS_SERVER_SESSION_IDS, 1, sessionId);
        }
        // 删除session
        redisUtil.remove(UpmsConstant.UPMS_SHIRO_SESSION_ID + "_" + sessionId);
        _log.debug("doUpdate >>>>> sessionId={}", sessionId);
    }

    /**
     * 获取会话列表
     * @param offset
     * @param limit
     * @return
     */
    /*public Map getActiveSessions(int offset, int limit) {
        Map sessions = new HashMap();
        // 获取在线会话总数
        long total = redisUtil.lsize(UpmsConstant.UPMS_SERVER_SESSION_IDS);
        // 获取当前页会话详情
        Jedis jedis = redisUtil.getJedis();
        List<String> ids = jedis.lrange(UpmsConstant.UPMS_SERVER_SESSION_IDS, offset, (offset + limit - 1));
        List<Session> rows = new ArrayList<>();
        for (String id : ids) {
            String session = redisUtil.get(UpmsConstant.UPMS_SHIRO_SESSION_ID + "_" + id);
            // 过滤redis过期session
            if (null == session) {
                redisUtil.lrem(UpmsConstant.UPMS_SERVER_SESSION_IDS, 1, id);
                total = total - 1;
                continue;
            }
             rows.add(UpmsSession.deserialize(session));
        }
        jedis.close();
        sessions.put("total", total);
        sessions.put("rows", rows);
        return sessions;
    }*/

    /**
     * 强制退出
     * @param ids
     * @return
     */
    public int forceout(String ids) {
        String[] sessionIds = ids.split(",");
        for (String sessionId : sessionIds) {
            // 会话增加强制退出属性标识，当此会话访问系统时，判断有该标识，则退出登录
            String session = redisUtil.get(UpmsConstant.UPMS_SHIRO_SESSION_ID + "_" + sessionId);
            UpmsSession upmsSession = (UpmsSession) UpmsSession.deserialize(session);
            upmsSession.setStatus(UpmsSession.OnlineStatus.force_logout);
            upmsSession.setAttribute("FORCE_LOGOUT", "FORCE_LOGOUT");
            redisUtil.set(UpmsConstant.UPMS_SHIRO_SESSION_ID + "_" + sessionId, UpmsSession.serialize(upmsSession), (int) upmsSession.getTimeout() / 1000);
        }
        return sessionIds.length;
    }

    /**
     * 更改在线状态
     *
     * @param sessionId
     * @param onlineStatus
     */
    public void updateStatus(Serializable sessionId, UpmsSession.OnlineStatus onlineStatus) {
        UpmsSession session = (UpmsSession) doReadSession(sessionId);
        if (null == session) {
            return;
        }
        session.setStatus(onlineStatus);
        redisUtil.set(UpmsConstant.UPMS_SHIRO_SESSION_ID + "_" + session.getId(), UpmsSession.serialize(session), (int) session.getTimeout() / 1000);
    }

}
