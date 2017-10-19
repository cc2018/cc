# 用户权限系统



### 创建数据库

```create database

create database cc;
use cc;
grant all privileges on cc.* to work@localhost identified by 'cc%1234';
source ~/pro/upms/src/main/resources/upms.sql

```

各表说明：

```
#组织表
upms_organization: 编号 | 所属上级 | 组织名称 | 组织描述

#角色表
upms_role: 编号 | 角色名称 | 角色标题 | 角色描述

#系统表
upms_system： 编号 | 系统名称 | 系统背景 | 主题 | icon | url

#权限表：细粒度的权限定义  权限值：子系统:功能:curd 路径：url
upms_permission: 编号 | 所属系统 | 所属上级 | 名称 | 类型(1:目录,2:菜单,3:按钮) | 权限值（子系统:功能:curd）| 路径

#用户表：
upms_user:  编号 | 帐号 | 密码 | 盐 等

再组合用户与权限、角色与权限、用户与权限可得到
upms_user_permission、upms_user_role、upms_role_permission 等表

```

### jpa

主要使用jpa操作数据库，如果想用复杂sql做直接查询，可接入EntityManagerFactory和EntityManager。

### shiro

shiro过滤器过滤属性含义

```
securityManager：这个属性是必须的。
loginUrl ：没有登录的用户请求需要登录的页面时自动跳转到登录页面，不是必须的属性，不输入地址的话会自动寻找项目web项目的根目录下的”/login.jsp”页面。
successUrl ：登录成功默认跳转页面，不配置则跳转至”/”。如果登陆前点击的一个需要登录的页面，则在登录自动跳转到那个需要登录的页面。不跳转到此。
unauthorizedUrl ：没有权限默认跳转的页面
```
 

其权限过滤器及配置释义

```
anon:例子/admins/**=anon 没有参数，表示可以匿名使用。
authc:例如/admins/user/**=authc表示需要认证(登录)才能使用，没有参数
roles(角色)：例子/admins/user/**=roles[admin],参数可以写多个，多个时必须加上引号，并且参数之间用逗号分割，当有多个参数时，例如admins/user/**=roles["admin,guest"],每个参数通过才算通过，相当于hasAllRoles()方法。
perms（权限）：例子/admins/user/**=perms[user:add:*],参数可以写多个，多个时必须加上引号，并且参数之间用逗号分割，例如/admins/user/**=perms["user:add:*,user:modify:*"]，当有多个参数时必须每个参数都通过才通过，想当于isPermitedAll()方法。
rest：例子/admins/user/**=rest[user],根据请求的方法，相当于/admins/user/**=perms[user:method] ,其中method为post，get，delete等。
port：例子/admins/user/**=port[8081],当请求的url的端口不是8081是跳转到schemal://serverName:8081?queryString,其中schmal是协议http或https等，serverName是你访问的host,8081是url配置里port的端口，queryString
是你访问的url里的？后面的参数。
authcBasic：例如/admins/user/**=authcBasic没有参数表示httpBasic认证
ssl:例子/admins/user/**=ssl没有参数，表示安全的url请求，协议��
```

### 单点登录

SOO 登陆流程

```
用户访问系统1的受保护资源，系统1发现用户未登录，跳转至sso认证中心，并将自己的地址作为参数
sso认证中心发现用户未登录，将用户引导至登录页面
用户输入用户名密码提交登录申请
sso认证中心校验用户信息，创建用户与sso认证中心之间的会话，称为全局会话，同时创建授权令牌
sso认证中心带着令牌跳转会最初的请求地址（系统1）
系统1拿到令牌，去sso认证中心校验令牌是否有效
sso认证中心校验令牌，返回有效，注册系统1
系统1使用该令牌创建与用户的会话，称为局部会话，返回受保护资源
用户访问系统2的受保护资源
系统2发现用户未登录，跳转至sso认证中心，并将自己的地址作为参数
sso认证中心发现用户已登录，跳转回系统2的地址，并附上令牌
系统2拿到令牌，去sso认证中心校验令牌是否有效
sso认证中心校验令牌，返回有效，注册系统2
系统2使用该令牌创建与用户的局部会话，返回受保护资源
```

### 参考

本例子很多代码会参考以下开源项目

1. [zheng](https://github.com/shuzheng/zheng)
2. [favorites-web](https://github.com/cloudfavorites/favorites-web)


