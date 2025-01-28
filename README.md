# spring-security-demo

# 介绍
学习spring-security，乃至定制

| 模块 |工程类型| 实践方式 | 作用|
|:----|:---|:---|:---|
|encrypted-authentication-01|定制认证|基于Controller，且放弃AuthenticationManager|加密认证方式：requestBody是加密的，还包含验证码校验|
|encrypted-authentication-02|定制认证|基于Controller，自定义AuthenticationManager|加密认证方式：requestBody是加密的，还包含验证码校验|
|encrypted-authentication-03|定制认证+RememberMe|在02的基础上，实现RememberMe功能|加密认证方式：requestBody是加密的，还包含验证码校验|
|security-rbac|授权/鉴权方式-基于角色-基于uri-基于数据库|自定义AuthorizationManager|见下方**描述1**|

- 描述1

  | 版本 | UserDetails权限字段和类型                       | 数据库权限配置                     | 实现原理      |
  |:---|:-----------------------------------------|:----------------------------|:----------|
  | v1 | 原生的authorities，封装为SimpleGrantedAuthority | userId->roleCode->uri       | 查询用户时，直接将角色封装成SimpleGrantedAuthority。鉴权时，通过uri查询到对应的角色，校验当前用户是否有该角色 |
  | v2 | 绕开authorities，基于自定义的UserDetails的其他字段     | userId->roleId->uri         |           |
  | v3 | 自定义GrantedAuthority，将每个请求需要有哪些角色         | userId->roleCode->uri       |           |
  | v4 | 自定义GrantedAuthority，将权限封装成菜单权限，菜单包含接口    | userId->roleId->menuId->uri |           |

  不管是哪个版本都是基于uri的权限配置方式，并且与[Spring Security之基于HttpRequest配置权限](https://blog.csdn.net/Evan_L/article/details/136605794)中也有区别。
  因为这里都是基于数据库的配置方式，而Spring原生的则是以硬编码的方式通过HttpSecurity#authorizeHttpRequests方法直接配置的。
  当然，如果我们的需求还是通过uri->role这种方式的话，也可以通过数据库查询后再进行配置。
  ```java
  class DemoConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> {
            // query database in order to build the map: uri to require roles
            Map<String, List<String>> uriRoleList = urlAuthorizationService.buildUrlAuthorizationMap();
            if (CollectionUtils.isEmpty(uriRoleList)) {
                return;
            }
            uriRoleList.forEach((uri, roles) -> {
                authorize.requestMatchers(uri).hasAnyRole(roles.toArray(new String[0]));
            });
        });
    }
  }
  ```
  值得注意的是，虽然可以如此基于数据库进行权限配置，但是却没有提供动态配置权限的可能。


- 描述2
