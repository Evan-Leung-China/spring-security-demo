-- 对于老旧系统，授权只做到菜单。默认拥有菜单的人，可以拥有所有这个菜单的接口权限。
-- 主要区别是，多了一张菜单接口表
CREATE TABLE `security_dev`.`dev_user` (
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

CREATE TABLE `security_dev`.`dev_user_role` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色表';

CREATE TABLE `security_dev`.`dev_menu` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `menu_code` VARCHAR(45) NOT NULL COMMENT '菜单名称',
  `menu_name` VARCHAR(45) NOT NULL,
  `parent` INT NULL COMMENT '父菜单ID，为空表示顶级菜单',
  `menu_uri` VARCHAR(128) NULL COMMENT '菜单uri',
  `menu_icon` VARCHAR(45) NULL DEFAULT 'menu',
  `menu_type` CHAR NOT NULL COMMENT '菜单类型 1 目录 2 菜单 3 按钮',
  `created_date` DATETIME NOT NULL,
  `created_by` VARCHAR(45) NOT NULL,
  `updated_date` DATETIME NOT NULL,
  `updated_by` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`))
COMMENT = '菜单表';

CREATE TABLE `security_dev`.`dev_menu_interface` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `menu_id` INT NOT NULL COMMENT '菜单表主键',
  `uri` VARCHAR(128) NULL COMMENT 'uri',
  `description` VARCHAR(45) NULL DEFAULT '接口描述',
  `created_date` DATETIME NOT NULL,
  `created_by` VARCHAR(45) NOT NULL,
  `updated_date` DATETIME NOT NULL,
  `updated_by` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`))
COMMENT = '菜单接口表';

CREATE TABLE `security_dev`.`dev_role_menu` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `role_id` int NOT NULL COMMENT '角色表主键',
  `menu_id` VARCHAR(45) NOT NULL COMMENT '菜单表主键',
  `created_date` DATETIME NOT NULL,
  `created_by` VARCHAR(45) NOT NULL,
  `updated_date` DATETIME NOT NULL,
  `updated_by` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`))
COMMENT = '角色菜单表';

