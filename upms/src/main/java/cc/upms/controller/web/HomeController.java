package cc.upms.controller.web;

import cc.upms.constant.UpmsConstant;
import cc.upms.constant.UpmsResult;
import cc.upms.constant.UpmsResultConstant;
import cc.upms.shiro.ShiroConfig;
import cc.upms.shiro.session.UpmsSession;
import cc.upms.shiro.session.UpmsSessionDao;
import cc.upms.util.RedisUtil;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.UUID;

@Controller
public class HomeController {

    private static Logger _log = LoggerFactory.getLogger(ShiroConfig.class);

    @Autowired
    UpmsSessionDao upmsSessionDao;

    @Autowired
    RedisUtil redisUtil;

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }

    @RequestMapping("/sso/login")
    public String login(@RequestParam(value="backurl", required=false, defaultValue="/")String backurl, Model model) {
        // 登录失败从request中获取shiro处理的异常信息。
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        String serverSessionId = session.getId().toString();
        // 判断是否已登录，如果已登录，则回跳
        String code = redisUtil.get(UpmsConstant.UPMS_SERVER_SESSION_ID + "_" + serverSessionId);
        // code校验值
        if (StringUtils.isNotBlank(code)) {
            // 回跳
            String username = (String) subject.getPrincipal();
            if (backurl.contains("?")) {
                backurl += "&upms_code=" + code + "&upms_username=" + username;
            } else {
                backurl += "?upms_code=" + code + "&upms_username=" + username;
            }
            _log.debug("认证中心帐号通过，带code回跳：{}", backurl);
            return "redirect:" + backurl;
        }
        // model.addAttribute("name", "");
        return "login";
    }

    // 使用ajax发送post请求，获取返回结果
    @RequestMapping(value = "/sso/login", method = RequestMethod.POST)
    @ResponseBody
    public Object login(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe");
        if (StringUtils.isBlank(username)) {
            return new UpmsResult(UpmsResultConstant.EMPTY_USERNAME, "帐号不能为空！");
        }
        if (StringUtils.isBlank(password)) {
            return new UpmsResult(UpmsResultConstant.EMPTY_PASSWORD, "密码不能为空！");
        }
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        String sessionId = session.getId().toString();
        // 判断是否已登录，如果已登录，则回跳，防止重复登录
        String hasCode = redisUtil.get(UpmsConstant.UPMS_SERVER_SESSION_ID + "_" + sessionId);
        // code校验值
        if (StringUtils.isBlank(hasCode)) {
            // 使用shiro认证
            UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, password);
            try {
                if (BooleanUtils.toBoolean(rememberMe)) {
                    usernamePasswordToken.setRememberMe(true);
                } else {
                    usernamePasswordToken.setRememberMe(false);
                }
                subject.login(usernamePasswordToken);
            } catch (UnknownAccountException e) {
                return new UpmsResult(UpmsResultConstant.INVALID_USERNAME, "帐号不存在！");
            } catch (IncorrectCredentialsException e) {
                return new UpmsResult(UpmsResultConstant.INVALID_PASSWORD, "密码错误！");
            } catch (LockedAccountException e) {
                return new UpmsResult(UpmsResultConstant.INVALID_ACCOUNT, "帐号已锁定！");
            }
            // 更新session状态
            upmsSessionDao.updateStatus(sessionId, UpmsSession.OnlineStatus.on_line);
            // 全局会话sessionId列表，供会话管理
            redisUtil.lpush(UpmsConstant.UPMS_SERVER_SESSION_IDS, sessionId.toString());
            // 默认验证帐号密码正确，创建code
            String code = UUID.randomUUID().toString();
            // 全局会话的code
            redisUtil.set(UpmsConstant.UPMS_SERVER_SESSION_ID + "_" + sessionId, code, (int) subject.getSession().getTimeout() / 1000);
            // code校验值
            redisUtil.set(UpmsConstant.UPMS_SERVER_CODE + "_" + code, code, (int) subject.getSession().getTimeout() / 1000);
        }
        // 回跳登录前地址
        String backurl = request.getParameter("backurl");
        if (StringUtils.isBlank(backurl)) {
            return new UpmsResult(UpmsResultConstant.SUCCESS, "/");
        } else {
            return new UpmsResult(UpmsResultConstant.SUCCESS, backurl);
        }
    }

    @RequestMapping("/403")
    public String unauthorizedRole(){
        System.out.println("------没有权限-------");
        return "403";
    }
}