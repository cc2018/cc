package cc.upms.shiro;

import cc.upms.config.UpmsConfig;
import cc.upms.shiro.listener.UpmsSessionListener;
import cc.upms.shiro.realm.UpmsRealm;
import cc.upms.shiro.session.UpmsSessionDao;
import cc.upms.shiro.session.UpmsSessionFactory;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.SessionFactory;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import java.util.*;

@Configuration
public class ShiroConfig {
    private static Logger _log = LoggerFactory.getLogger(ShiroConfig.class);

    @Bean
    public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager) {
        _log.debug("ShiroConfiguration.shirFilter()");
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //拦截器.
        Map<String,String> filterChainDefinitionMap = new LinkedHashMap<String,String>();
        // 配置不会被拦截的链接 顺序判断
        filterChainDefinitionMap.put("/static/**", "anon");
        // 配置退出 过滤器,其中的具体的退出代码Shiro已经替我们实现了
        filterChainDefinitionMap.put("/logout", "logout");
        // api 接口可直接访问，具体url由权限控制
        // filterChainDefinitionMap.put("/api/**", "user");
        // <!-- 过滤链定义，从上向下顺序执行，一般将/**放在最为下边 -->:这是一个坑呢，一不小心代码就不好使了;
        // <!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问-->
        // filterChainDefinitionMap.put("/**", "authc");

        // 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
        shiroFilterFactoryBean.setLoginUrl("/sso/login");
        // 登录成功后要跳转的链接
        shiroFilterFactoryBean.setSuccessUrl("/index");

        //未授权界面;
        shiroFilterFactoryBean.setUnauthorizedUrl("/403");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    /**
     * 凭证匹配器
     * （由于我们的密码校验交给Shiro的SimpleAuthenticationInfo进行处理了）
     *
     * @return
     */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher(){
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("md5");//散列算法:这里使用MD5算法;
        hashedCredentialsMatcher.setHashIterations(2);//散列的次数，比如散列两次，相当于 md5(md5(""));
        return hashedCredentialsMatcher;
    }

    @Bean
    public UpmsRealm getShiroRealm(){
        UpmsRealm upmsRealm = new UpmsRealm();
        //upmsRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return upmsRealm;
    }

    @Bean
    public SessionFactory getSessionFactory(){
        UpmsSessionFactory sessionFactory = new UpmsSessionFactory();
        return sessionFactory;
    }

    @Bean
    public Cookie getSessionIdCookie() {
        SimpleCookie simpleCookie = new SimpleCookie();
        simpleCookie.setHttpOnly(true);
        // 默认-1表示关闭浏览器时过期Cookie
        simpleCookie.setMaxAge(-1);
        simpleCookie.setName(UpmsConfig.getSessionId());
        return simpleCookie;
    }

    @Bean
    public UpmsSessionDao getSessionDao() {
        UpmsSessionDao sessionDao = new UpmsSessionDao();
        return sessionDao;
    }

    @Bean
    public DefaultWebSessionManager configWebSessionManager(SessionDAO sessionDao){
        DefaultWebSessionManager manager = new DefaultWebSessionManager();
        // manager.setCacheManager(cacheManager);// 加入缓存管理器
        manager.setSessionDAO(sessionDao);// 设置SessionDao
        // manager.setGlobalSessionTimeout(sessionDao.getExpireTime());// 设置全局session超时时间
        manager.setDeleteInvalidSessions(true);// 删除过期的session

        manager.setSessionIdCookieEnabled(true);
        manager.setSessionIdCookie(getSessionIdCookie());
        manager.setGlobalSessionTimeout(UpmsConfig.getSessionTime());
        manager.setSessionValidationSchedulerEnabled(false);// 是否定时检查session

        List<SessionListener> listeners = new ArrayList<SessionListener>();
        listeners.add(new UpmsSessionListener());
        manager.setSessionListeners(listeners);

        manager.setSessionFactory(getSessionFactory());
        return manager;
    }


    @Bean
    public SecurityManager securityManager(DefaultWebSessionManager webSessionManager){

        // realm
        DefaultWebSecurityManager securityManager =  new DefaultWebSecurityManager();
        securityManager.setRealm(getShiroRealm());

        // session管理器
        securityManager.setSessionManager(webSessionManager);

        return securityManager;
    }

    /**
     *  开启shiro aop注解支持.
     *  使用代理方式;所以需要开启代码支持;
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager){
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    @Bean(name="simpleMappingExceptionResolver")
    public SimpleMappingExceptionResolver
    createSimpleMappingExceptionResolver() {
        SimpleMappingExceptionResolver r = new SimpleMappingExceptionResolver();
        Properties mappings = new Properties();
        mappings.setProperty("DatabaseException", "databaseError");//数据库异常处理
        mappings.setProperty("UnauthorizedException","403");
        r.setExceptionMappings(mappings);  // None by default
        r.setDefaultErrorView("error");    // No default
        r.setExceptionAttribute("ex");     // Default is "exception"
        //r.setWarnLogCategory("example.MvcLogger");     // No default
        return r;
    }
}