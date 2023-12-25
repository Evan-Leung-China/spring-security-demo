# 项目说明
当前项目是建立在security-rbac的基础之上，改造出一个自定义/login登录接口的版本。最初的目的是自定义实现前置的验证码逻辑校验。
但如此一来，原来的在UsernamePasswordAuthenticationFilter中实现的功能就需要我们自己来实现了。

# UsernamePasswordAuthenticationFilter
我们如果想要充分定制，又想保留原有的功能，就必须了解原来到底有什么功能，是怎么实现的！
这个过滤器本身可以说啥事没干，因为最核心的登录验证逻辑也交给AuthenticationManager了。
至于登录成功之后干的那些事，都是其父类负责组织的。
```java
public abstract class AbstractAuthenticationProcessingFilter extends GenericFilterBean
        implements ApplicationEventPublisherAware, MessageSourceAware {
    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // 认证过滤器实际上就是针对登录接口完成登录逻辑的，这也是为什么SpringSecurity没有定义/login，或者/j_spring_security_check，却能直接处理的原因
        // 这里就是判断是否为目标认证接口，如果是，那么这个就由当前过滤进行登录认证
        if (!requiresAuthentication(request, response)) {
            chain.doFilter(request, response);
            return;
        }
        try {
            // 这是钩子方法。他就是UsernamePasswordAuthenticationFilter主要实现的方法，在这个方法里面完成的登录认证逻辑
            // 如果我们想要自定义认证逻辑：例如增加验证码校验。也可以直接继承于这个抽象类。
            // tips：认证过滤器可以直接读取RequestBody，而无需担心其他过滤器/Controller因为重复读取请求体而异常的情况。
            // 从这个角度说的话，这个过滤器就相当于/login。
            Authentication authenticationResult = attemptAuthentication(request, response);
            if (authenticationResult == null) {
                // return immediately as subclass has indicated that it hasn't completed
                return;
            }
            // 登录成功后的处理：
            // 1. session相关的操作
            // 这个就有很多选择了，因为即使是SpringSecurity提供的SessionAuthenticationStrategy实现，都有不少。
            // 不过都与session有关。因此都是在SessionManagerConfigurer里面进行配置并放到HttpSecurity的sharedObject属性里面。
            // FormLoginConfigurer再从里面取出来给认证过滤器赋值，以此达到共享协同一致。
            // 如果没有配置的话，默认就是ChangeSessionIdAuthenticationStrategy。这个只是给session更换个sessionId，并不重建session。
            // 想要重建session的话，需要用SessionFixationProtectionStrategy。可以防御“会话固定攻击”
            // 如果你希望控制账号多地登录的数量，这就有点小麻烦了。因为，首先你需要记录账号已经登录的session，然后校验是否已经达到极限。
            // 因此，需要两个策略：ConcurrentSessionControlAuthenticationStrategy（控制登录数量）、RegisterSessionAuthenticationStrategy（登记已登录账号和session）
            // 但使用的时候只能有一个，于是spring使用组合模式：CompositeSessionAuthenticationStrategy。搞多少个都行。
            // 除了这些，还有个跨域攻击防御的CsrfAuthenticationStrategy，就是创建csrfToken，放到RequestAttribute中。
            // CsrfFilter会进行校验，二者通过同一个CsrfTokenRepository进行协同。
            this.sessionStrategy.onAuthentication(authenticationResult, request, response);
            // Authentication success
            if (this.continueChainBeforeSuccessfulAuthentication) {
                // 如果希望后续的过滤器依然生效，可以配置continueChainBeforeSuccessfulAuthentication。但正常都不需要。
                chain.doFilter(request, response);
            }
            // 2. 其他操作。进入到方法里面再看
            successfulAuthentication(request, response, chain, authenticationResult);
        } catch (InternalAuthenticationServiceException failed) {
            this.logger.error("An internal error occurred while trying to authenticate the user.", failed);
            unsuccessfulAuthentication(request, response, failed);
        } catch (AuthenticationException ex) {
            // Authentication failed
            unsuccessfulAuthentication(request, response, ex);
        }
    }

    /**
     * 默认的认证成功后的操作
     * 1. 将当前登录成功的Authentication对象赋值到SecurityContextHolder，并保存起来（默认是保存到session中）。
     * 2. 通知启动时配置的RememberMeServices当前登录成功的请求和响应。——涉及rememberMe的token管理。
     * 3. 发布InteractiveAuthenticationSuccessEvent事件。
     * 4. 额外的认证成功后的操作委托给AuthenticationSuccessHandler
     */
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        // 1. 创建空的安全上下文。securityContextHolderStrategy主要是考虑上下文可能需要跨线程使用，一般应用，直接放到ThreadLocal里面就好了。框架设计时需要考虑小众需求。
        SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
        // 设置认证结果
        context.setAuthentication(authResult);
        // 放到SecurityContextHolder中
        this.securityContextHolderStrategy.setContext(context);
        // ！！！这里大家要注意一下，这个是保存的SecurityContext的。与上面的不一样的是，上面的只能在当前请求中使用。
        // 而这个是在整个用户与应用交互过程中使用的。登录后的请求，都是从这里获取到SecurityContext，再恢复到SecurityContextHolder中的。
        // 同样，SpringSecurity提供了保存到Session和RequestAttribute中两种方式。一般我们都保存到session中。
        this.securityContextRepository.saveContext(context, request, response);
        // 小结：以上操作与大多数同学理解的：登录成功后，将用户信息保存到Session中这个是一致的。
        // 只不过SpringSecurity多维护了一个SecurityContextHolder，以便大家通过静态方法获取认证信息。
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(LogMessage.format("Set SecurityContextHolder to %s", authResult));
        }
        // 2. 记住我。这个功能本身有RememberMeAuthenticationFilter负责通过RememberMeToken进行认证。
        // 但这个功能作为辅助性的功能，必须核心过滤器正常登录成功后，创建RememberMeToken并且保存起来。
        // 这个方法就是完成这个铺垫工作的。
        this.rememberMeServices.loginSuccess(request, response, authResult);
        if (this.eventPublisher != null) {
            this.eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authResult, this.getClass()));
        }
        // 3. 登录成功后跳转操作。
        // 登录成功后，一般有两种选择：1. 跳转到应用首页 2. 跳转到未登录时访问的请求
        // 如果是后者，则需要一来RequestCache这个组件的支持。用于保存未登录的请求。
        // 而RequestCacheAwareFilter，就是将保存的未登录请求包装起来。在这里进行恢复跳转！
        this.successHandler.onAuthenticationSuccess(request, response, authResult);
    }
}
```
小结：UsernamePasswordAuthenticationFilter的功能
1. 从入参获取用户账号和密码进行登录验证。
2. 创建session。
3. 将认证信息保存到SecurityContextHolder。
4. 将认证信息持久化。一般是持久化到session。
5. 辅助性的功能：
    - 多点登录控制：记录当前登录的session、统计校验当前账号已登录数量并进行控制。
    - 跨域请求伪造防御：创建CsrfToken，并保存起来。以便CsrfFilter与入参进行比验证。如果我们使用的是Configurer进行配置的话，那么就是在CsrfConfigurer中创建，然后添加到SessionManagerConfigurer中。
    - 记住我：创建RememberMeToken，并保存起来。其实现与Csrf类似。
    - 登录成功后自定跳转：从request中读取未认证时的请求进行重定向。

# 定制
搞清楚各项功能的实现之后，我们瞄准目标：登录成功后跳转到未认证的请求。

## 自定义/login接口
第一种方式：见LoginController
利用SpringSecurity的组件来达到我们的目标。一来避免重复造轮子，二来可以避免淌坑。
1. 利用SpringMVC的功能，将application/x-www-form-urlencoded的入参封装好。（如果不是form表单提交而是RequestBody传参，则需要使用@RequestBody。）
   如果是通过自定义认证过滤器，则需要自己从HttpServletRequest读取。
2. 通过AuthenticationManager完成登录。在这之前，我们就可以自定义的增加前置校验：验证码校验。
3. 创建认证信息保存到SecurityContextRepository。这样下次请求就能维持会话了。
4. 重定向到认证前的请求。

可能有的同学注意到了，我们并没有重新创建session。显然我们确实可以简单粗暴地直接通过`request.getSession(true);`创建，但可能会影响原有的Spring的组件的运行。

## 自定义认证过滤器
第二种方式：见AuthenticationWithKaptFilter.
逻辑也大差不差。只是，全部使用了SpringSecurity的组件。然后配置复杂了不少。
由于这里我并没有像UsernamePasswordAuthenticationFilter那样，搞个FormLoginConfigurer。
这导致复杂度加剧。因为需要把与其他的Filter协作完成的功能所涉及到的对象提前创建好，然后放到HttpSecurity#sharedObjects里面。这样其他由于Configurer创建的Filter才能正确创建。
但如果要这样配置，你必须很清楚这些协作的功能和实现原理。如果大家看懂了这些配置及其涉及的功能，相信你们能够参考FormLoginConfigurer把这些配置写到自定义的Configurer。