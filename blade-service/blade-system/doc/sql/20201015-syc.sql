ALTER TABLE  blade_tenant ADD UNIQUE(tenant_id);
ALTER TABLE blade_tenant ADD login_logo VARCHAR(400) NULL COMMENT '登入页和大屏端logo' ;
ALTER TABLE blade_tenant ADD cms_logo VARCHAR(400) NULL COMMENT '后台logo' ;
ALTER TABLE blade_tenant ADD sys_name VARCHAR(32) NULL COMMENT '系统名称' ;


ALTER TABLE `blade_menu` ADD sys_id VARCHAR(32) NULL COMMENT '系统id(对应租户Id)' ;
ALTER TABLE `blade_menu` ADD is_hide INT(2) DEFAULT 0 COMMENT '是否隐藏1隐藏，0不隐藏' ;
ALTER TABLE `blade_menu` ADD source_path VARCHAR(400) NULL COMMENT 'web端文件资源路劲' ;
update blade_menu set source_path =path;

