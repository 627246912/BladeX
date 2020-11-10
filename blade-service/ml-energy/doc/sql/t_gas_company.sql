
DROP TABLE IF EXISTS `t_gas_company`;

CREATE TABLE `t_gas_company` (
  `id` bigint(20) NOT NULL,
  `company_name` varchar(64) COLLATE utf8mb4_bin NOT NULL,
  `create_user` bigint(64) DEFAULT NULL COMMENT '创建人',
  `create_dept` bigint(64) DEFAULT NULL COMMENT '创建部门',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` bigint(64) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `status` int(2) DEFAULT NULL COMMENT '状态',
  `is_deleted` int(2) DEFAULT '0' COMMENT '是否删除',
  `tenant_id` varchar(12) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '租户Id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

/*Data for the table `t_gas_company` */

insert  into `t_gas_company`(`id`,`company_name`,`create_user`,`create_dept`,`create_time`,`update_user`,`update_time`,`status`,`is_deleted`,`tenant_id`) values

(1,'中油',NULL,NULL,NULL,NULL,NULL,NULL,0,'CRRCZhuzhou'),

(2,'新奥',NULL,NULL,NULL,NULL,NULL,NULL,0,'CRRCZhuzhou');

