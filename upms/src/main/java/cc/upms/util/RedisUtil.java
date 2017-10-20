package cc.upms.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Autowired
    private StringRedisTemplate template;

    private static Logger _log = LoggerFactory.getLogger(RedisUtil.class);

    /**
     * 设置 String
     * @param key
     * @param value
     */
    public void set(String key, String value) {
        try {
            ValueOperations<String, String> ops = template.opsForValue();
            ops.set(key, value);
        } catch (Exception e) {
            _log.error("redis: set key error : " + e);
        }
    }

    /**
     * 设置 String 过期时间
     * @param key
     * @param value
     * @param seconds 以秒为单位
     */
    public void set(String key, String value, int seconds) {
        try {
            ValueOperations<String, String> ops = template.opsForValue();
            ops.set(key, value, seconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            _log.error("redis: set key error : " + e);
        }
    }

    /**
     * 获取String值
     * @param key
     * @return value
     */
    public String get(String key) {
        try {
            ValueOperations<String, String> ops = template.opsForValue();
            return ops.get(key);
        } catch (Exception e) {
            _log.error("redis: get key error : " + e);
        }

        return null;
    }

    /**
     * 删除值
     * @param key
     */
    public void remove(String key) {
        try {
            template.delete(key);
        } catch (Exception e) {
            _log.error("Remove key error : " + e);
        }
    }

    /**
     * lpush
     * @param key
     * @param strings
     */
    public void lpush(String key, String... strings) {
        try {
            BoundListOperations<String, String> ops = template.boundListOps(key);
            ops.leftPushAll(strings);
        } catch (Exception e) {
            _log.error("lpush error : " + e);
        }
    }

    /**
     * lrem
     * @param key
     * @param count
     * @param value
     */
    public void lrem(String key, long count, String value) {
        try {
            BoundListOperations<String, String> ops = template.boundListOps(key);
            ops.remove(count, value);
        } catch (Exception e) {
            _log.error("lrem error : " + e);
        }
    }

    /**
     * lrem
     * @param key
     * @return
     */
    public long lsize(String key) {
        try {
            BoundListOperations<String, String> ops = template.boundListOps(key);
            return ops.size();
        } catch (Exception e) {
            _log.error("lrem error : " + e);
        }
        return 0L;
    }

    /**
     * sadd
     * @param key
     * @param value
     * @param seconds
     */
    public void sadd(String key, String value, int seconds) {
        try {
            BoundSetOperations<String, String> ops = template.boundSetOps(key);
            ops.add(value);
        } catch (Exception e) {
            _log.error("sadd error : " + e);
        }
    }

    /**
     * 删除set集合中的对象
     * @param key
     * @param value
     */
    public void srem(String key, String... value) {
        try {
            BoundSetOperations<String, String> ops = template.boundSetOps(key);
            ops.remove(value);
        } catch (Exception e) {
            _log.error("srem error : " + e);
        }
    }

    /**
     * 删除set集合中的对象
     * @param key
     * @return
     */
    public Set<String> smembers(String key) {
        try {
            BoundSetOperations<String, String> ops = template.boundSetOps(key);
            return ops.members();
        } catch (Exception e) {
            _log.error("srem error : " + e);
        }

        return null;
    }

    /**
     * 删除set集合中的对象
     * @param key
     * @return
     */
    public Long ssize(String key) {
        try {
            BoundSetOperations<String, String> ops = template.boundSetOps(key);
            return ops.size();
        } catch (Exception e) {
            _log.error("srem error : " + e);
        }

        return 0L;
    }
}
