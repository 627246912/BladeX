ALTER TABLE t_equipment_transformer MODIFY CODE VARCHAR(64) NULL COMMENT '系统图产品id';

CREATE TABLE `t_turn_invoice` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `unit_id` bigint(20) NOT NULL COMMENT '单位id',
  `invoice_price` double(11,2) NOT NULL COMMENT '发票金额',
  `invoice_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '发票编号',
  `invoice_time` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '费用月份',
  `create_user` bigint(64) DEFAULT NULL COMMENT '创建人',
  `create_dept` bigint(64) DEFAULT NULL COMMENT '创建部门',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` bigint(64) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `status` int(2) DEFAULT NULL COMMENT '状态',
  `is_deleted` int(2) DEFAULT '0' COMMENT '是否删除',
  `tenant_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '租户Id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='电量转供发票';

CREATE TABLE `t_turn_unit` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `unit_name` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '单位名称',
  `read_type` varchar(12) COLLATE utf8mb4_bin DEFAULT '1' COMMENT '读表方式1手动，2自动',
  `create_user` bigint(64) DEFAULT NULL COMMENT '创建人',
  `create_dept` bigint(64) DEFAULT NULL COMMENT '创建部门',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` bigint(64) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `status` int(2) DEFAULT NULL COMMENT '状态',
  `is_deleted` int(2) DEFAULT '0' COMMENT '是否删除',
  `tenant_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '租户Id',
  `item_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '自动读取数据项',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE `t_turn_unit_data` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `unit_id` bigint(20) NOT NULL COMMENT '单位id',
  `val` double(11,2) NOT NULL COMMENT '电度',
  `month_time` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '费用月份',
  `create_user` bigint(64) DEFAULT NULL COMMENT '创建人',
  `create_dept` bigint(64) DEFAULT NULL COMMENT '创建部门',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` bigint(64) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `status` int(2) DEFAULT NULL COMMENT '状态',
  `is_deleted` int(2) DEFAULT '0' COMMENT '是否删除',
  `tenant_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '租户Id',
  `cost` double(11,2) DEFAULT NULL COMMENT '费用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='单位电量转供月份数据';

