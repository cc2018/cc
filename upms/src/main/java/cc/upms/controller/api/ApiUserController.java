package cc.upms.controller.api;

import cc.upms.domain.Permission;
import cc.upms.domain.UserInfo;
import cc.upms.service.api.UserInfoService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ApiUserController {
    @Autowired
    private UserInfoService userService;

    @RequestMapping("/api/user")
    public JSONObject greeting(@RequestParam(value="name") String name) {
        UserInfo userInfo = userService.findByUserName(name);

        // test
        List<Permission> permissions =  userService.findUserPermissionsByUserId(userInfo.getUserId());

        // 使用DTO，防止db修改，客户端也要修改code
        JSONObject user = new JSONObject();
        user.put("id", userInfo.getUserId());
        user.put("user_naame", userInfo.getUsername());
        user.put("realname", userInfo.getRealname());
        user.put("avatar", userInfo.getAvatar());
        user.put("phone", userInfo.getPhone());
        user.put("email", userInfo.getEmail());
        return user;
    }

    @RequestMapping(value="/api/user/{userId}", method= RequestMethod.GET)
    public UserInfo getUser(@PathVariable Long userId) {
        return null;
    }

    @RequestMapping(value="/api/user/{userId}", method=RequestMethod.DELETE)
    public UserInfo deleteUser(@PathVariable Long userId) {
        return null;
    }

}