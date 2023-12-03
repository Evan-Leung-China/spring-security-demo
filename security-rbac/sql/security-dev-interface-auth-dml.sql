-- 菜单
INSERT INTO `security_dev`.`dev_menu` (`id`, `menu_code`, `menu_name`, `parent`, `menu_type`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('1', 'D_SYSTEM', '系统管理', NULL, '1', '2023-11-13 19:30:00', 'system', '2023-11-13 19:30:00', 'system');
INSERT INTO `security_dev`.`dev_menu` (`id`, `menu_code`, `menu_name`, `parent`, `menu_type`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('2', 'M_MENU', '菜单管理', '1', '2', '2023-11-13 19:30:00', 'system', '2023-11-13 19:30:00', 'system');
INSERT INTO `security_dev`.`dev_menu` (`id`, `menu_code`, `menu_name`, `parent`, `menu_type`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('3', 'M_USER', '用户管理', '1', '2', '2023-11-13 19:30:00', 'system', '2023-11-13 19:30:00', 'system');
INSERT INTO `security_dev`.`dev_menu` (`id`, `menu_code`, `menu_name`, `parent`, `menu_type`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('4', 'M_ROLE', '角色管理', '1', '2', '2023-11-13 19:30:00', 'system', '2023-11-13 19:30:00', 'system');
INSERT INTO `security_dev`.`dev_menu` (`id`, `menu_code`, `menu_name`, `parent`, `menu_type`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('5', 'D_OPERATION', '运营管理', NULL, '1', '2023-11-13 19:30:00', 'system', '2023-11-13 19:30:00', 'system');
INSERT INTO `security_dev`.`dev_menu` (`id`, `menu_code`, `menu_name`, `parent`, `menu_type`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('6', 'M_LOG', '日志管理', '5', '2', '2023-11-13 19:30:00', 'system', '2023-11-13 19:30:00', 'system');


-- 角色
INSERT INTO `security_dev`.`dev_role` (`id`, `role_code`, `role_name`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('1', 'ADMIN', '管理员', '2023-11-13 19:30:00', 'system', '2023-11-13 19:30:00', 'system');
INSERT INTO `security_dev`.`dev_role` (`id`, `role_code`, `role_name`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('2', 'OPERATOR', '运营', '2023-11-13 19:30:00', 'system', '2023-11-13 19:30:00', 'system');

--角色菜单
INSERT INTO `security_dev`.`dev_role_menu` (`role_id`, `menu_id`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('1', '1', '2023-11-25 20:30:00', 'system', '2023-11-25 20:30:00', 'system');
INSERT INTO `security_dev`.`dev_role_menu` (`role_id`, `menu_id`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('1', '2', '2023-11-13 20:30:00', 'system', '2023-11-13 20:30:00', 'system');
INSERT INTO `security_dev`.`dev_role_menu` (`role_id`, `menu_id`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('1', '3', '2023-11-13 20:30:00', 'system', '2023-11-13 20:30:00', 'system');
INSERT INTO `security_dev`.`dev_role_menu` (`role_id`, `menu_id`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('1', '4', '2023-11-13 20:30:00', 'system', '2023-11-13 20:30:00', 'system');
INSERT INTO `security_dev`.`dev_role_menu` (`role_id`, `menu_id`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('1', '5', '2023-11-25 20:30:00', 'system', '2023-11-25 20:30:00', 'system');
INSERT INTO `security_dev`.`dev_role_menu` (`role_id`, `menu_id`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('1', '6', '2023-11-13 20:30:00', 'system', '2023-11-13 20:30:00', 'system');
INSERT INTO `security_dev`.`dev_role_menu` (`role_id`, `menu_id`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('2', '5', '2023-11-25 20:30:00', 'system', '2023-11-25 20:30:00', 'system');
INSERT INTO `security_dev`.`dev_role_menu` (`role_id`, `menu_id`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('2', '6', '2023-11-13 20:30:00', 'system', '2023-11-13 20:30:00', 'system');

-- 用户
-- admin123
INSERT INTO `security_dev`.`dev_user` (`id`, `user_code`, `user_name`, `password`, `is_lock`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('1', 'administrator', '超级管理员', '$2a$10$EDC5yHXf.9w7fl456N.P0uCAwwvWrtYansPS4KBHJnAcNi2G10U9S', '0', '2023-11-13 20:30:00', 'system', '2023-11-13 20:30:00', 'system');
-- evan1234
INSERT INTO `security_dev`.`dev_user` (`id`, `user_code`, `user_name`, `password`, `is_lock`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('2', 'evan', '名可夫斯基', '$2a$10$5QGL6F7YV4vRk58ON0i2k.CLMMFQq9tv1SFekecySk5HYf854TBOy', '0', '2023-11-13 20:30:00', 'system', '2023-11-13 20:30:00', 'system');

-- 用户角色
INSERT INTO `security_dev`.`dev_user_role` (`id`, `user_id`, `role_id`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('1', '1', '1', '2023-11-13 20:30:00', 'system', '2023-11-13 20:30:00', 'system');
INSERT INTO `security_dev`.`dev_user_role` (`id`, `user_id`, `role_id`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('2', '2', '2', '2023-11-13 20:30:00', 'system', '2023-11-13 20:30:00', 'system');

-- 菜单接口
INSERT INTO `security_dev`.`dev_menu_interface` (`menu_id`, `uri`, `description`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES (2, '/menu/query/*', '菜单查询', '2023-11-13 19:30:00', 'system', '2023-11-13 19:30:00', 'system');
INSERT INTO `security_dev`.`dev_menu_interface` (`menu_id`, `uri`, `description`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES (2, '/menu/delete', '删除菜单', '2023-11-13 19:30:00', 'system', '2023-11-13 19:30:00', 'system');
INSERT INTO `security_dev`.`dev_menu_interface` (`menu_id`, `uri`, `description`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES (3, '/user/query/*', '查询用户', '2023-11-13 19:30:00', 'system', '2023-11-13 19:30:00', 'system');
INSERT INTO `security_dev`.`dev_menu_interface` (`menu_id`, `uri`, `description`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES (3, '/user/add', '新增用户', '2023-11-13 19:30:00', 'system', '2023-11-13 19:30:00', 'system');
INSERT INTO `security_dev`.`dev_menu_interface` (`menu_id`, `uri`, `description`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES (3, '/user/update', '更新用户', '2023-11-13 19:30:00', 'system', '2023-11-13 19:30:00', 'system');
INSERT INTO `security_dev`.`dev_menu_interface` (`menu_id`, `uri`, `description`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES (3, '/user/delete/*', '删除用户', '2023-11-13 19:30:00', 'system', '2023-11-13 19:30:00', 'system');
INSERT INTO `security_dev`.`dev_menu_interface` (`menu_id`, `uri`, `description`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES (4, '/role/add', '新增角色', '2023-11-13 19:30:00', 'system', '2023-11-13 19:30:00', 'system');
INSERT INTO `security_dev`.`dev_menu_interface` (`menu_id`, `uri`, `description`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES (4, '/role/query/*', '查询角色', '2023-11-13 19:30:00', 'system', '2023-11-13 19:30:00', 'system');
INSERT INTO `security_dev`.`dev_menu_interface` (`menu_id`, `uri`, `description`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES (4, '/role/authMenu', '角色菜单授权', '2023-11-13 19:30:00', 'system', '2023-11-13 19:30:00', 'system');
INSERT INTO `security_dev`.`dev_menu_interface` (`menu_id`, `uri`, `description`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES (6, '/log/query/*', '日志查询', '2023-11-13 19:30:00', 'system', '2023-11-13 19:30:00', 'system');
