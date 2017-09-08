spring-springmvc
===
## 项目介绍
在之前的shiro整合项目之后，更加完善shiro功能，之前的代码不予展示与介绍，想了解的请参考shiro整合项目

## 功能新增
- 用户注册
- 登录错误次数限制(使用redis作缓存)
- shiro注解配置
- DTO引入
- 数据校验(使用hibernate validation)
- SpringMVC统一异常处理配置

## 项目结构
### java：代码
- controller:控制层，以下展示注册和登录功能
```
    @RequestMapping("/login")
    public String login(ShiroUser shiroUser, HttpServletRequest request) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(shiroUser.getUsername(), shiroUser.getPassword());
        try {
            subject.login(token);//会跳到我们自定义的realm中
            request.getSession().setAttribute("user", shiroUser);
            log.info(shiroUser.getUsername() + "登录");
            return "user/success";
        } catch (UnknownAccountException e) {
            return "user/login";
        } catch (IncorrectCredentialsException e) {
            request.setAttribute("error", "用户名或密码错误");
            return "user/login";
        } catch (ExcessiveAttemptsException e) {
            request.setAttribute("error", "输入密码错误太多次,请稍后再试！");
            return "user/login";
        } catch (Exception e) {
            request.setAttribute("error", "未知错误");
            return "user/login";
        }
    }
    
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(Model model,
                           @Valid @ModelAttribute ShiroUserDTO shiroUserDTO, BindingResult bindingResult) {
        //数据校验
        if (bindingResult.hasErrors()) {
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            for (ObjectError objectError : allErrors) {
                //输出错误信息
                System.out.println(objectError.getDefaultMessage());
            }
            model.addAttribute("error", "填入信息有误");
            model.addAttribute("user", shiroUserDTO);
            return "/user/register";
        }
        if (shiroUserService.getByUsername(shiroUserDTO.getUsername()) == null) {
            shiroUserService.insertUser(shiroUserDTO);
            return "redirect:/";
        } else {
            model.addAttribute("user", shiroUserDTO);
            model.addAttribute("error", "userName has been registered!");
            return "/user/register";
        }
    }
```    
- service:业务处理层，以下展示新增用户功能，包含数据转换(DTO到Entity)和密码加密(shiro加密策略)
 ```
 public void insertUser(ShiroUserDTO shiroUserDTO) {
        ShiroUser shiroUser = converToAddress(shiroUserDTO);
        shiroUserMapper.insert(shiroUser);
    }
  
 private ShiroUser converToAddress(ShiroUserDTO shiroUserDTO) {
        ShiroUser shiroUser = new ShiroUser();
        BeanUtils.copyProperties(shiroUserDTO, shiroUser);
        passwordEncrypt(shiroUser);
        shiroUser.setCreatetime(new Date());
        shiroUser.setRoleId(1);
        return shiroUser;
    }

  private void passwordEncrypt(ShiroUser shiroUser) {
        String username = shiroUser.getUsername();
        String password = shiroUser.getPassword();
        String salt2 = new SecureRandomNumberGenerator().nextBytes().toHex();
        int hashIterations = 3;
        String algorithmName = "md5";
        SimpleHash hash = new SimpleHash(algorithmName, password,
                username + salt2, hashIterations);
        String encodedPassword = hash.toHex();
        shiroUser.setSalt(salt2);
        shiroUser.setPassword(encodedPassword);
    }
 ```
- dao:数据库交互层
- entity:实体对象层,以下展示数据校验
```
@Data
public class ShiroUser {
    private Integer id;
    
    @NotNull(message = "用户名不能为空")
    @Size(min = 3, max = 16, message = "用户名长度必须介于3-16个字符之间")
    
    private String username;
    
    @NotNull(message = "密码不能为空")
    @Size(min = 3, max = 16, message = "{密码长度必须介于3-16个字符之间")
    private String password;
    
    private Date createtime;
    
    private Date lasttime;
    
    @Email(message = "请输入正确的邮箱")
    private String email;
    
    private String sex;
    
    private String salt;
    
    private Integer roleId;
}
```
- realm:自定义Realm(shiro相关)，以下加入了加密相关代码
```
public class MyRealm extends AuthorizingRealm {

    @Resource
    private ShiroUserService shiroUserService;

    // 为当前登陆成功的用户授予权限和角色，已经登陆成功了
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(
            PrincipalCollection principals) {
        String username = (String) principals.getPrimaryPrincipal(); //获取用户名
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.setRoles(shiroUserService.getRoles(username));
        authorizationInfo.setStringPermissions(shiroUserService.getPermissions(username));
        return authorizationInfo;
    }

    // 验证当前登录的用户，获取认证信息
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
            AuthenticationToken token) throws AuthenticationException {
        String username = (String) token.getPrincipal(); // 获取用户名
        ShiroUser shiroUser = shiroUserService.getByUsername(username);
        if (shiroUser != null) {
            SimpleAuthenticationInfo authcInfo = new SimpleAuthenticationInfo(shiroUser.getUsername(), shiroUser.getPassword(), "myRealm");
            //通过SimpleAuthenticationInfo的credentialsSalt设置盐，HashedCredentialsMatcher会自动识别这个盐。
            authcInfo.setCredentialsSalt(ByteSource.Util.bytes(shiroUser.getUsername() + shiroUser.getSalt()));
            return authcInfo;
        } else {
            return null;
        }
    }
}
```
- constants:常量类包
- dto:DTO对象包,以下展示ShiroUserDTO(只包含交互相关的字段)
```
@Data
public class ShiroUserDTO {

    @NotNull(message = "用户名不能为空")
    @Size(min = 3, max = 16, message = "用户名长度必须介于3-16个字符之间")
    private String username;

    @NotNull(message = "密码不能为空")
    @Size(min = 3, max = 16, message = "{密码长度必须介于3-16个字符之间")
    private String password;

    @Email(message = "请输入正确的邮箱")
    private String email;

    @NotNull(message = "请选择性别")
    private String sex;
}
```
- credentials:处理重试次数类包
```
public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher {

    @Autowired
    private RedisCache redisCache;


    //匹配用户输入的token的凭证（未加密）与系统提供的凭证（已加密）
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        String username = (String) token.getPrincipal();
        //retry count + 1
        //AtomicInteger是一个提供原子操作的Integer类，通过线程安全的方式操作加减。
        AtomicInteger retryCount = redisCache.getCache(Constants.USER + username, AtomicInteger.class);
        if (retryCount == null) {
            retryCount = new AtomicInteger(0);
        }
        //增长
        if (retryCount.incrementAndGet() > 5) {
            //if retry count > 5 throw
            throw new ExcessiveAttemptsException();
        }
        redisCache.putCacheWithExpireTime(Constants.USER + username, retryCount, 600);
        boolean matches = super.doCredentialsMatch(token, info);
        if (matches) {
            //clear retry count
            redisCache.deleteCache(Constants.USER + username);
        }
        return matches;
    }
}
```

### resources：配置文件
- application.xml:spring配置文件入口，加载spring-config.xml
- spring-mvc.xml:springmvc配置相关文件
- spring-config.xml:加载其他集成的配置文件
- spring-mybatis.xml：mybatis相关配置文件
- spring-shiro.xml:shiro配置相关文件
```
    <!-- 凭证匹配器 -->
    <bean id="credentialsMatcher" class="com.py.credentials.RetryLimitHashedCredentialsMatcher">
        <!--指定散列算法为md5，需要和生成密码时的一样-->
        <property name="hashAlgorithmName" value="md5"/>
        <!--散列迭代次数，需要和生成密码时的一样-->
        <property name="hashIterations" value="3"/>
        <!--表示是否存储散列后的密码为16进制，需要和生成密码时的一样，默认是base64-->
        <property name="storedCredentialsHexEncoded" value="true"/>
    </bean>

    <!-- 自定义Realm -->
    <bean id="myRealm" class="com.py.realm.MyRealm">
        <property name="credentialsMatcher" ref="credentialsMatcher"/>
    </bean>
    
     <!--Spring MVC统一异常处理(主要处理shiro注解(如@RequiresPermissions)引发的异常)-->
    <bean class="org.springframework.web.servlet.handler.SimpleMappingExceptionResolver">
        <property name="exceptionMappings">
            <props>
                <!--未登录-->
                <prop key="org.apache.shiro.authz.UnauthenticatedException">
                    redirect:/user/login
                </prop>
                <!--未授权-->
                <prop key="org.apache.shiro.authz.UnauthorizedException">
                    redirect:/user/login
                </prop>
            </props>
        </property>
        <!--默认跳转页面-->
        <property name="defaultErrorView" value="unauthorized"/>
    </bean>
```
- db.properties：数据库相关参数配置
- log4j.properties：日志相关参数配置
- mapping:存放mybatis映射文件，以UserMapper.xml为例
- redis.properties:redis相关参数配置
```
#redis config
redis.pool.maxTotal=100
redis.pool.maxIdle=10
redis.pool.maxWaitMillis=5000
redis.pool.testOnBorrow=true
redis.pool.maxActive= 100
redis.pool.maxWait= 3000


#redis ip和端口号
redis.ip=127.0.0.1
redis.port=6379
redis.pass=
```
- spring-redis.xml:redis相关配置
```
 <!-- Redis 配置 -->
    <bean id="jedisPoolConfig" class="redis.clients.jedis.JedisPoolConfig">
        <property name="maxTotal" value="${redis.pool.maxTotal}"/>
        <property name="maxIdle" value="${redis.pool.maxIdle}"/>
        <property name="maxWaitMillis" value="${redis.pool.maxWaitMillis}"/>
        <property name="testOnBorrow" value="${redis.pool.testOnBorrow}"/>
    </bean>

    <!-- redis单节点数据库连接配置 -->
    <bean id="jedisConnectionFactory" class="org.springframework.data.redis.connection.jedis.JedisConnectionFactory">
        <property name="hostName" value="${redis.ip}"/>
        <property name="port" value="${redis.port}"/>
        <property name="password" value="${redis.pass}"/>
        <property name="poolConfig" ref="jedisPoolConfig"/>
    </bean>

    <!-- redisTemplate配置，redisTemplate是对Jedis的对redis操作的扩展，有更多的操作，封装使操作更便捷 -->
    <bean id="redisTemplate" class="org.springframework.data.redis.core.StringRedisTemplate">
        <property name="connectionFactory" ref="jedisConnectionFactory"/>
    </bean>
```
- spring-mvc-shiro.xml:shiro注解相关配置
```
 <!-- 开启Shiro注解 -->
    <bean class="org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator"
          depends-on="lifecycleBeanPostProcessor"/>
    <bean class="org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor">
        <property name="securityManager" ref="securityManager"/>
    </bean>
```

### webapp：web相关
- web.xml
```
 <!-- shiro过滤器定义 -->
    <filter>
        <filter-name>shiroFilter</filter-name>
        <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
        <init-param>
            <!-- 该值缺省为false,表示生命周期由SpringApplicationContext管理,设置为true则表示由ServletContainer管理 -->
            <param-name>targetFilterLifecycle</param-name>
            <param-value>true</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>shiroFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
 ```
##　其他文件
### logs：日志存放
### deploy：部署文件(sql)
- update.sql
```
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `t_permission`
-- ----------------------------
DROP TABLE IF EXISTS `t_permission`;
CREATE TABLE `t_permission` (
  `id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  `permissionname` varchar(100) COLLATE utf8mb4_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of t_permission
-- ----------------------------
INSERT INTO `t_permission` VALUES ('1', '1', 'user:create');
INSERT INTO `t_permission` VALUES ('2', '2', 'user:update');
INSERT INTO `t_permission` VALUES ('3', '1', 'user:update');

-- ----------------------------
-- Table structure for `t_role`
-- ----------------------------
DROP TABLE IF EXISTS `t_role`;
CREATE TABLE `t_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `rolename` varchar(20) COLLATE utf8mb4_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of t_role
-- ----------------------------
INSERT INTO `t_role` VALUES ('1', 'teacher');
INSERT INTO `t_role` VALUES ('2', 'student');

-- ----------------------------
-- Table structure for `t_user`
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userName` varchar(20) NOT NULL,
  `password` varchar(50) NOT NULL,
  `createTime` date DEFAULT NULL,
  `lastTime` datetime DEFAULT NULL,
  `email` varchar(256) DEFAULT NULL,
  `sex` enum('male','female') DEFAULT 'male',
  `salt` varchar(50) DEFAULT NULL,
  `role_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES ('1', 'admin', '86c4604b628d4e91f5f2a2fed3f88430', '2017-08-28', null, '404158848@qq.com', 'male', '26753209835f4c837066d1cc7d9b46aa', '1');
INSERT INTO `t_user` VALUES ('2', 'test', 'a038892c7b638aad0357adb52cabfb29', '2017-08-28', null, '404158848@qq.com', 'male', '6ced07d939407fb0449d92d9f17cfcd1', '2');
INSERT INTO `t_user` VALUES ('3', 'test1', '4be958cccb89213221888f9ffca6969b', '2017-08-28', null, '404158848@qq.com', 'male', 'c95a278e52daf5166b1ffd6436cde7b7', '1');

```
### pom.xml：maven相关
```
        <!-- redis begin-->
        <dependency>
            <groupId>redis.clients</groupId>
            <artifactId>jedis</artifactId>
            <version>2.8.0</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.data</groupId>
            <artifactId>spring-data-redis</artifactId>
            <version>1.6.4.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>com.dyuproject.protostuff</groupId>
            <artifactId>protostuff-core</artifactId>
            <version>1.0.8</version>
        </dependency>

        <dependency>
            <groupId>com.dyuproject.protostuff</groupId>
            <artifactId>protostuff-runtime</artifactId>
            <version>1.0.8</version>
        </dependency>
        <!-- redis end-->

        <!--hibernate validation begin-->
        <!-- spring 数据校验-->
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-validator</artifactId>
            <version>5.0.2.Final</version>
        </dependency>
        <!--hibernate validation end-->
```
