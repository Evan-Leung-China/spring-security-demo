# 工程说明
本工程旨在搭建一个在spring-security之上、基于角色的认证授权demo。
RBAC：Role Base Access Control

# 项目说明
项目按照需要分成了4个版本。
v1-v3版本的数据库将权限细化到按钮级别。因此可以直接在菜单表中将按钮对应的url也配置到表中。
v4版本则将权限只配置到菜单级别，只要拥有某个菜单，就能拥有整个菜单所需要的接口权限。
# SpringSecurity的集成
围绕着AuthorizationManager<T>（SpringSecurity5.5推出的）、UserDetails这两个东西进行集成。
UserDetails是我们通过UserDetailsService#loadUserByUsername从数据库读取。
而AuthorizationManager从Authentication#getAuthorities获取当前用户的权限来进行授权验证的，该方法的权限就是UserDetails里面的。
因此，只要我们把UserDetails的权限跟AuthorizationManager的权限协调好，就能达到我们的目的。

## v1版本
按照SpringSecurity的规范，先构建一个Map<AntPathRequestMatcher, List<GrantedAuthority>>作为授权依据。
GrantedAuthority则使用其提供的SimpleGrantedAuthority，将dev_role表的role_code作为角色，传入SimpleGrantedAuthority.
其麻烦的点是，我们需要通过dev_user.id -> dev_user_role -> dev_role -> dev_role_menu -> dev_menu. 
然后将dev_menu.uri跟dev_role.role_code字段进行映射，作为我们判断是否授权的权限依据。在接口访问时，
获取当前用户权限:Authentication#getAuthorities，再来判断是否包含即可。

## v2版本
相较于v1版本，v2版本通过定制UserDetails绕开了GrantedAuthority，使用dev_role.id。
还是这个链路：dev_user.id -> dev_user_role -> dev_role -> dev_role_menu -> dev_menu，
只是，不返回GrantedAuthority，在验证权限时，通过
`DevUserDetailV2 principal = (DevUserDetailV2) authenticate.getPrincipal();`
强转为我们的DevUserDetailV2，获取roleIdList，再判断uri的权限。

## v3版本
鉴于SpringSecurity并推荐如下使用方式，推出v3版本：SpringSecurity推荐大家在需要定制复杂的权限时，实现GrantedAuthority，
并为GrantedAuthority.getAuthority接口返回null，通过特定的AuthorizationManager，来对自定义的GrantedAuthority提供支持。
所以，我们简单实现了一个ComplexGrantedAuthority，其本质上还是之前v2版本的dev_role.id。

## v4版本
v4版本则是在考虑应对老旧系统的需求。权限只配置到菜单级别，将菜单需要调用的接口放到另一个表dev_menu_interface表中。
实际上就算是配置到按钮级别，有的系统也有可能通过这样的菜单接口表来进行配置。也可以参考这个实现。
首先，我们定义了自定义了一个MenuGrantedAuthority。这个权限的重点是menuId,然后他会保存着与这个menuId相对应的uri。
考虑到当页面联调好之后，页面能够调用的接口其实已经完全定下来了，因此可以在应用启动后直接初始化所有的MenuGrantedAuthority。
在用户登录时，我们只需要dev_user.id -> dev_user_role -> dev_role -> dev_role_menu，
拿到menuId到MenuGrantedAuthorityManager转成MenuGrantedAuthority即可。这样一来，AuthorizationManager的逻辑就变成：
1. 将遍历MenuGrantedAuthority，找到匹配的uri后返回该MenuGrantedAuthority，可能有多个：例如基础接口-查询字典/机构信息。
2. 通过Authentication#getAuthorities获取到当前用户的权限。
3. 遍历当前用户是否拥有当前请求所需要的MenuGrantedAuthority。

### 意外之喜
这个版本实现之后，我竟然发现不管我们怎么在后台管理中修改用户/角色的权限，我们的AuthorizationManager都不需要更新权限信息。
因为AuthorizationManager只关心菜单的接口，并不在意用户/角色拥有多少个菜单！

# SQL脚本
## v4版本脚本
./sql/security-dev-interface-auth-ddl.sql
./sql/security-dev-interface-auth-dml.sql
## v1-v3版本脚本
./sql/security-dev-ddl.sql 建表脚本
./sql/security-dev-dml.sql 建表脚本

