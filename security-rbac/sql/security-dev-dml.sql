-- 菜单
INSERT INTO `security_dev`.`dev_menu` (`id`, `menu_code`, `menu_name`, `parent`, `menu_type`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('1', 'D_SYSTEM', '系统管理', NULL, '1', '2023-11-13 19:30:00', 'system', '2023-11-13 19:30:00', 'system');
INSERT INTO `security_dev`.`dev_menu` (`id`, `menu_code`, `menu_name`, `parent`, `menu_type`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('2', 'M_MENU', '菜单管理', '1', '2', '2023-11-13 19:30:00', 'system', '2023-11-13 19:30:00', 'system');
INSERT INTO `security_dev`.`dev_menu` (`id`, `menu_code`, `menu_name`, `parent`, `menu_type`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('3', 'M_USER', '用户管理', '1', '2', '2023-11-13 19:30:00', 'system', '2023-11-13 19:30:00', 'system');
INSERT INTO `security_dev`.`dev_menu` (`id`, `menu_code`, `menu_name`, `parent`, `menu_type`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('4', 'M_ROLE', '角色管理', '1', '2', '2023-11-13 19:30:00', 'system', '2023-11-13 19:30:00', 'system');
INSERT INTO `security_dev`.`dev_menu` (`id`, `menu_code`, `menu_name`, `parent`, `menu_type`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('5', 'D_OPERATION', '运营管理', NULL, '1', '2023-11-13 19:30:00', 'system', '2023-11-13 19:30:00', 'system');
INSERT INTO `security_dev`.`dev_menu` (`id`, `menu_code`, `menu_name`, `parent`, `menu_type`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('6', 'M_LOG', '日志管理', '5', '2', '2023-11-13 19:30:00', 'system', '2023-11-13 19:30:00', 'system');
INSERT INTO `security_dev`.`dev_menu` (`menu_code`, `menu_name`, `parent`, `menu_uri`, `menu_icon`, `menu_type`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('B_MENU_QUERY', '菜单查询', '2', '/menu/query/*', 'button', '3', '2023-11-13 19:30:00', 'system', '2023-11-13 19:30:00', 'system');
INSERT INTO `security_dev`.`dev_menu` (`menu_code`, `menu_name`, `parent`, `menu_uri`, `menu_icon`, `menu_type`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('B_MUNU_DELETE', '删除菜单', '2', '/menu/delete', 'button', '3', '2023-11-13 19:30:00', 'system', '2023-11-13 19:30:00', 'system');
INSERT INTO `security_dev`.`dev_menu` (`menu_code`, `menu_name`, `parent`, `menu_uri`, `menu_icon`, `menu_type`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('B_USER_QUERY', '查询用户', '3', '/user/query/*', 'button', '3', '2023-11-13 19:30:00', 'system', '2023-11-13 19:30:00', 'system');
INSERT INTO `security_dev`.`dev_menu` (`menu_code`, `menu_name`, `parent`, `menu_uri`, `menu_icon`, `menu_type`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('B_USER_ADD', '新增用户', '3', '/user/add', 'button', '3', '2023-11-13 19:30:00', 'system', '2023-11-13 19:30:00', 'system');
INSERT INTO `security_dev`.`dev_menu` (`menu_code`, `menu_name`, `parent`, `menu_uri`, `menu_icon`, `menu_type`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('B_USER_UPDATE', '更新用户', '3', '/user/update', 'button', '3', '2023-11-13 19:30:00', 'system', '2023-11-13 19:30:00', 'system');
INSERT INTO `security_dev`.`dev_menu` (`menu_code`, `menu_name`, `parent`, `menu_uri`, `menu_icon`, `menu_type`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('B_USER_DELTE', '删除用户', '3', '/user/delete/*', 'button', '3', '2023-11-13 19:30:00', 'system', '2023-11-13 19:30:00', 'system');
INSERT INTO `security_dev`.`dev_menu` (`menu_code`, `menu_name`, `parent`, `menu_uri`, `menu_icon`, `menu_type`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('B_ROLE_ADD', '新增角色', '4', '/role/add', 'button', '3', '2023-11-13 19:30:00', 'system', '2023-11-13 19:30:00', 'system');
INSERT INTO `security_dev`.`dev_menu` (`menu_code`, `menu_name`, `parent`, `menu_uri`, `menu_icon`, `menu_type`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('B_ROLE_QUERY', '查询角色', '4', '/role/query/*', 'button', '3', '2023-11-13 19:30:00', 'system', '2023-11-13 19:30:00', 'system');
INSERT INTO `security_dev`.`dev_menu` (`menu_code`, `menu_name`, `parent`, `menu_uri`, `menu_icon`, `menu_type`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('B_ROLE_AUTH', '角色菜单授权', '4', '/role/authMenu', 'button', '3', '2023-11-13 19:30:00', 'system', '2023-11-13 19:30:00', 'system');
INSERT INTO `security_dev`.`dev_menu` (`menu_code`, `menu_name`, `parent`, `menu_uri`, `menu_icon`, `menu_type`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('B_LOG_QUERY', '日志查询', '6', '/log/query/*', 'button', '3', '2023-11-13 19:30:00', 'system', '2023-11-13 19:30:00', 'system');


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

-- 授权到按钮级别
INSERT INTO `security_dev`.`dev_role_menu` (`role_id`, `menu_id`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('1', '7', '2023-11-25 20:30:00', 'system', '2023-11-25 20:30:00', 'system');
INSERT INTO `security_dev`.`dev_role_menu` (`role_id`, `menu_id`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('1', '8', '2023-11-25 20:30:00', 'system', '2023-11-25 20:30:00', 'system');
INSERT INTO `security_dev`.`dev_role_menu` (`role_id`, `menu_id`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('1', '9', '2023-11-25 20:30:00', 'system', '2023-11-25 20:30:00', 'system');
INSERT INTO `security_dev`.`dev_role_menu` (`role_id`, `menu_id`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('1', '10', '2023-11-25 20:30:00', 'system', '2023-11-25 20:30:00', 'system');
INSERT INTO `security_dev`.`dev_role_menu` (`role_id`, `menu_id`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('1', '11', '2023-11-25 20:30:00', 'system', '2023-11-25 20:30:00', 'system');
INSERT INTO `security_dev`.`dev_role_menu` (`role_id`, `menu_id`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('1', '12', '2023-11-25 20:30:00', 'system', '2023-11-25 20:30:00', 'system');
INSERT INTO `security_dev`.`dev_role_menu` (`role_id`, `menu_id`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('1', '13', '2023-11-25 20:30:00', 'system', '2023-11-25 20:30:00', 'system');
INSERT INTO `security_dev`.`dev_role_menu` (`role_id`, `menu_id`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('1', '14', '2023-11-25 20:30:00', 'system', '2023-11-25 20:30:00', 'system');
INSERT INTO `security_dev`.`dev_role_menu` (`role_id`, `menu_id`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('1', '15', '2023-11-25 20:30:00', 'system', '2023-11-25 20:30:00', 'system');
INSERT INTO `security_dev`.`dev_role_menu` (`role_id`, `menu_id`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('1', '16', '2023-11-25 20:30:00', 'system', '2023-11-25 20:30:00', 'system');
INSERT INTO `security_dev`.`dev_role_menu` (`role_id`, `menu_id`, `created_date`, `created_by`, `updated_date`, `updated_by`) VALUES ('2', '16', '2023-11-25 20:30:00', 'system', '2023-11-25 20:30:00', 'system');

