-- 相较于之前的表结构，菜单接口表，不使用uri，而是使用authority_code字段。以两级菜单为例，
-- 格式为：{业务模块}:{菜单}:{权限}，可以简单理解为：
-- 业务模块对应一级菜单
-- 菜单对应二级菜单
-- 权限对应按钮
-- 但权限并不完全等价于按钮。所以，菜单可能进入就自动查询接口，这种不存在查询按钮的情况，也可以对应查询权限。
-- 不妨梳理一下
-- 姑且把我们要访问的菜单&按钮&接口，统一叫做资源。
-- 我们访问这些资源，须要某些权限。
-- 于是我们需要两层配置：
-- 1. 角色权限表，用于记录角色拥有哪些权限。
--    由于我们的权限有两种：用户直接可见的菜单（包括按钮），和用户不可见的接口。
--    为了配合前端页面以树结构展示权限进行配置，我们需要把二者合而为一。
--    这里有两个选择：
--    一，单独定义一张权限表，用于记录树结构的权限数据——其中包括菜单&按钮&接口.
--        好处是，概念清晰，逻辑明了。但需要单独维护权限表，增加维护成本。
--    二，把菜单表和权限表合并，菜单表中增加权限字段。
--        如此一来，我们看到的权限可能会多于页面的可见元素，例如上述的的进入页面立即查询的场景。
--        除此之外，这是把接口权限也等同于按钮权限了。
--        不过，问题不大。只要配置菜单权限的时候勾上查询权限即可，反正页面也不会绘制出该按钮。
--        带来的好处是，只需要维护一份数据即可。
--    姑且选择第二种方案。
-- 2. 资源权限表，用于记录访问资源时需要哪些权限。
-- 而菜单表本身就是记录的资源，所以，我们需要在菜单表加上权限字段。
-- 而按钮也是在菜单表中配置的。
-- 至于接口，可以通过Spring Security的注解进行配置。
--
-- 诶，大家发现了没有，这么一来，我们的菜单表也是权限表了。
-- 而对于页面而言，在初始化页面时，只需要把需要展示的元素与拿到的权限进行比对即可。
--
CREATE TABLE `security_demo`.`dev_user` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_code` VARCHAR(45) NOT NULL COMMENT '用户代码',
  `user_name` VARCHAR(45) NOT NULL COMMENT '用户名称',
  `password` VARCHAR(256) NOT NULL COMMENT '登录密码',
  `is_lock` TINYINT ZEROFILL NOT NULL COMMENT '是否锁定',
  `created_date` DATETIME NOT NULL COMMENT '创建时间',
  `created_by` VARCHAR(45) NOT NULL COMMENT '创建人',
  `updated_date` DATETIME NOT NULL COMMENT '更新时间',
  `updated_by` VARCHAR(45) NOT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `user_code_UNIQUE` (`user_code` ASC) VISIBLE)
COMMENT = '用户表';

CREATE TABLE `security_demo`.`dev_user_role` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL COMMENT '用户表主键',
  `role_id` INT NOT NULL COMMENT '角色表主键',
  `created_date` DATETIME NOT NULL,
  `created_by` VARCHAR(45) NOT NULL,
  `updated_date` DATETIME NOT NULL,
  `updated_by` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`))
COMMENT = '用户角色表';

CREATE TABLE `dev_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_code` varchar(45) NOT NULL COMMENT '角色代码',
  `role_name` varchar(45) NOT NULL COMMENT '角色名称',
  `created_date` datetime NOT NULL,
  `created_by` varchar(45) NOT NULL,
  `updated_date` datetime NOT NULL,
  `updated_by` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `role_code_UNIQUE` (`role_code`)
) COMMENT='角色表';

CREATE TABLE `security_demo`.`dev_menu` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `menu_code` VARCHAR(45) NOT NULL COMMENT '菜单名称',
  `menu_name` VARCHAR(45) NOT NULL,
  `parent` INT NULL COMMENT '父菜单ID，为空表示顶级菜单',
  `menu_uri` VARCHAR(128) NULL COMMENT '菜单uri',
  `permission` VARCHAR(128) NULL COMMENT '菜单权限',
  `menu_icon` VARCHAR(45) NULL DEFAULT 'menu',
  `menu_type` CHAR NOT NULL COMMENT '菜单类型 1 目录 2 菜单 3 按钮',
  `created_date` DATETIME NOT NULL,
  `created_by` VARCHAR(45) NOT NULL,
  `updated_date` DATETIME NOT NULL,
  `updated_by` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`))
COMMENT = '菜单表';

CREATE TABLE `security_demo`.`dev_role_menu` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `role_id` int NOT NULL COMMENT '角色表主键',
  `menu_id` VARCHAR(45) NOT NULL COMMENT '菜单表主键',
  `created_date` DATETIME NOT NULL,
  `created_by` VARCHAR(45) NOT NULL,
  `updated_date` DATETIME NOT NULL,
  `updated_by` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`))
COMMENT = '角色菜单表';

