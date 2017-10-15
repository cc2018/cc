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



