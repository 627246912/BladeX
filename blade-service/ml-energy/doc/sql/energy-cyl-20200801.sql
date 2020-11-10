#
##
###
####
#####
######
#######
ALTER TABLE t_inspection_task_record ADD is_abnormal TINYINT(4) DEFAULT 0 COMMENT '0:正常 1:异常';
ALTER TABLE t_inspection_task_record ADD expect_hours TINYINT(4) COMMENT '期望工时';
ALTER TABLE t_sec_ins_rec ADD expect_time TINYINT(4) COMMENT '期望工时';
ALTER TABLE t_maintain ADD site_name VARCHAR(32) COMMENT '位置名称';
ALTER TABLE t_maintain ADD station_name VARCHAR(32) COMMENT '站点名称';
ALTER TABLE t_maintain ADD equipment_name VARCHAR(32) COMMENT '设备名称';
ALTER TABLE t_maintain ADD assigned_person_name VARCHAR(32) COMMENT '责任人名称';
ALTER TABLE t_maintain ADD next_time datetime COMMENT '下一次检查时间';
ALTER TABLE t_maintain ADD remind_time datetime COMMENT '下一次提醒时间';

#--------------------------------------2020-09-07
#问题描述
ALTER TABLE t_repair ADD problem_detail VARCHAR(255) COMMENT '问题描述';
#设备病例卡
ALTER TABLE t_repair ADD fault_history VARCHAR(1024) COMMENT '故障历史';
#站点名称
ALTER TABLE t_repair ADD station_name VARCHAR(32) COMMENT '站点名称';
#位置名称
ALTER TABLE t_repair ADD site_name VARCHAR(32) COMMENT '位置名称';
#设备名称
ALTER TABLE t_repair ADD equipment_name VARCHAR(32) COMMENT '设备名称';
#报修人名称
ALTER TABLE t_repair ADD release_repair_person_name VARCHAR(32) COMMENT '报修人名称';
#责任人名称
ALTER TABLE t_repair ADD assigned_person_name VARCHAR(32) COMMENT '责任人名称';
#审核人名称
ALTER TABLE t_repair ADD check_person_name VARCHAR(32) COMMENT '审核人名称';
#协助人名称
ALTER TABLE t_repair ADD assist_person_name VARCHAR(32) COMMENT '协助人名称';
#类型
ALTER TABLE t_repair ADD type TINYINT(4) COMMENT '类型 1:报修 2:告警';
ALTER TABLE t_repair MODIFY COLUMN task_status TINYINT(4) DEFAULT 0 COMMENT '任务状态 0:未修复 1:已修复';
ALTER TABLE t_repair ADD handle_level TINYINT(4) COMMENT '处理优先级 1:一般 2:紧急';





#---------------------------------------2020-09-08日
ALTER TABLE t_repair MODIFY COLUMN assigned_type TINYINT(4) DEFAULT 0 COMMENT '派单类型 0:自动 1:手动 默认自动';
ALTER TABLE t_repair ADD alert_level TINYINT(4) COMMENT '告警等级';
ALTER TABLE t_repair ADD alert_id BIGINT(20) COMMENT '告警id';
ALTER TABLE t_repair ADD is_create TINYINT(4) DEFAULT 0 COMMENT '是否创建任务 0:未创建 1:已创建';
ALTER TABLE t_equipment_alarm ADD is_create TINYINT(4) DEFAULT 0 COMMENT '是否创建任务 0:未创建 1:已创建';
#---- 把t_inspection_task_record 表数据清空 巡检统计
ALTER TABLE t_inspection_task_record MODIFY COLUMN inspection_item_count INT(8) DEFAULT 0 COMMENT '巡检项总数';
ALTER TABLE t_inspection_task_record MODIFY COLUMN abnormal_number INT(8) DEFAULT 0 COMMENT '异常数';
ALTER TABLE t_inspection_task_record MODIFY COLUMN repair_number INT(8) DEFAULT 0 COMMENT '报修数';
ALTER TABLE t_inspection_task_record MODIFY COLUMN check_item INT(8) DEFAULT 0 COMMENT '已检项';
ALTER TABLE t_inspection_task_record MODIFY COLUMN uncheck_item INT(8) DEFAULT 0 COMMENT '未检项';
ALTER TABLE t_repair MODIFY COLUMN is_expired TINYINT(4) DEFAULT 1 COMMENT '是否指定时间完成 0:未过期 1已过期 默认已过期';
ALTER TABLE t_repair ADD is_handle TINYINT(4) DEFAULT 0 COMMENT '是否处理 0:未处理 1:已处理';
ALTER TABLE t_repair MODIFY COLUMN site_id VARCHAR(32) COMMENT '位置id';
ALTER TABLE t_repair MODIFY COLUMN task_status VARCHAR(32) DEFAULT 0 COMMENT '任务状态 0：未修复 1：已修复 默认未修复';
ALTER TABLE t_repair ADD is_turn_down TINYINT(4) DEFAULT 0 COMMENT '是否驳回 0：审核中 1:已驳回 2：已派单 默认审核中';
ALTER TABLE t_repair ADD release_repair_person_phone VARCHAR(32) COMMENT '报修人电话';
ALTER TABLE t_repair ADD start_time datetime COMMENT '开始时间';
ALTER TABLE t_repair MODIFY COLUMN repair_type TINYINT(4) DEFAULT 0 COMMENT '维修类型 0:未开始 1:维修中 2:暂停 3:已完成 4:重检';
ALTER TABLE t_repair MODIFY COLUMN task_type TINYINT(4) DEFAULT -1 COMMENT '任务类型 0:巡检 1:保养 2:安全检测 3:人工报修';
ALTER TABLE t_repair MODIFY COLUMN task_id BIGINT DEFAULT -1 COMMENT '任务id';
ALTER TABLE t_repair MODIFY COLUMN inspection_type TINYINT(4) DEFAULT -1 COMMENT '巡检类型 1:巡检 2:巡查';
ALTER TABLE t_repair MODIFY COLUMN handle_level TINYINT(4) DEFAULT -1 COMMENT '处理优先级 1:一般 2:紧急';
ALTER TABLE t_repair MODIFY COLUMN fault_history VARCHAR(2048) COMMENT '历史故障';

DROP TABLE IF EXISTS `t_change`;
CREATE TABLE `t_change` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `change_type` tinyint(4) DEFAULT '-1' COMMENT '变更类型 1：转单',
  `task_id` bigint(20) DEFAULT NULL COMMENT '任务id',
  `task_type` tinyint(4) DEFAULT '-1' COMMENT '任务类型 0:巡检 1:保养 2:维修 3:安全检查',
  `task_time` datetime DEFAULT NULL COMMENT '任务开始时间',
  `task_work_hours` tinyint(4) DEFAULT NULL COMMENT '任务工时',
  `exchange_person_id` bigint(20) DEFAULT NULL COMMENT '交换人id',
  `exchange_person_name` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '交换人名称',
  `exchange_change_time` datetime DEFAULT NULL COMMENT '交换人变更提交时间',
  `exchange_season` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '交换人原因说明',
  `reply_person_id` bigint(20) DEFAULT NULL COMMENT '回复人id',
  `reply_person_name` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '回复人名称',
  `reply_change_time` datetime DEFAULT NULL COMMENT '回复人变更提交时间',
  `reply_refuse_season` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '回复人拒绝原因说明',
  `change_status` tinyint(4) DEFAULT '0' COMMENT '变更状态 0:待同意 1:同意 2:拒绝 默认待同意',
  `avatar` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '头像',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '部门id',
  `dept_name` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '部门名称',
  `create_user` bigint(64) DEFAULT NULL COMMENT '创建人',
  `create_dept` bigint(64) DEFAULT NULL COMMENT '创建部门',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` bigint(64) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `status` int(2) DEFAULT NULL COMMENT '状态',
  `is_deleted` int(2) DEFAULT '0' COMMENT '是否删除',
  `tenant_id` varchar(12) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '租户Id',
  `site_id` bigint(20) DEFAULT '-1' COMMENT '位置id',
  `station_id` bigint(20) DEFAULT '-1' COMMENT '站点id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='变更表';

DROP TABLE IF EXISTS `t_notice`;
CREATE TABLE `t_notice` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `station_id` bigint(20) DEFAULT '-1' COMMENT '站点id',
  `site_id` bigint(20) DEFAULT '-1' COMMENT '位置id',
  `task_id` varchar(20) COLLATE utf8mb4_bin DEFAULT '-1' COMMENT '任务id',
  `responsible_id` bigint(20) DEFAULT '-1' COMMENT '责任人id',
  `leader_id` bigint(20) DEFAULT '-1' COMMENT '领导id',
  `responsible_name` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '责任人名称',
  `site_name` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '位置名称',
  `notice_type` tinyint(4) DEFAULT '-1' COMMENT '通知类型 1:告警通知 2:任务通知 3:变更通知 4:报修通知 5:审核通知',
  `notice_name` varchar(32) COLLATE utf8mb4_bin DEFAULT '0' COMMENT '通知名称',
  `notice_time` datetime DEFAULT NULL COMMENT '通知时间',
  `notice_content` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '通知内容',
  `notice_status` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '通知时任务状态',
  `message_number` bigint(20) DEFAULT '0' COMMENT '消息数',
  `task_type` tinyint(4) DEFAULT '-1' COMMENT '任务类型：0:巡检 1:保养 2:维修 3:安全巡视',
  `inspection_type` tinyint(4) DEFAULT '-1' COMMENT '巡检类型：1:巡检 2:巡查',
  `inspection_source_type` tinyint(4) DEFAULT '-1' COMMENT '巡检资源类型 1:电 2:水 3:气 4:重点能耗 5:安全巡视',
  `repair_type` tinyint(4) DEFAULT '-1' COMMENT '维修类型：1:报修 2:告警',
  `change_type` tinyint(4) DEFAULT '-1' COMMENT '变更类型：1:转单 2:换班',
  `safety_inspection_type` tinyint(4) DEFAULT '-1' COMMENT '巡视类型：1:安全检查 2:整改计划',
  `process_status` tinyint(4) DEFAULT '0' COMMENT '处理状态：0:未处理 1:已处理',
  `is_look_over` tinyint(4) DEFAULT '0' COMMENT '是否查看：0:未查看 1:已查看',
  `create_user` bigint(64) DEFAULT NULL COMMENT '创建人',
  `create_dept` bigint(64) DEFAULT NULL COMMENT '创建部门',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` bigint(64) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `status` int(2) DEFAULT NULL COMMENT '状态',
  `is_deleted` int(2) DEFAULT '0' COMMENT '是否删除',
  `tenant_id` varchar(12) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '租户Id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '消息通知表';
ALTER TABLE t_notice ADD is_new TINYINT(4) DEFAULT 0 COMMENT '是否是新数据 0:否 1:是默认否 ';

DROP TABLE IF EXISTS `t_active`;
CREATE TABLE `t_active` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `active_person_id` bigint(20) DEFAULT '-1' COMMENT '活动人id',
  `notice_type` tinyint(4) DEFAULT '-1' COMMENT '通知类型 1:告警通知 2:任务通知 3:变更通知 4:报修通知 5:审核通知',
  `is_active` tinyint(4) DEFAULT '0' COMMENT '是否活跃 0:休息 1:活跃中 ',
  `active_start_time` datetime DEFAULT NULL COMMENT '活跃开始时间',
  `active_ends_time` datetime DEFAULT NULL COMMENT '活跃离开时间',
  `create_user` bigint(64) DEFAULT NULL COMMENT '创建人',
  `create_dept` bigint(64) DEFAULT NULL COMMENT '创建部门',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` bigint(64) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `status` int(2) DEFAULT NULL COMMENT '状态',
  `is_deleted` int(2) DEFAULT '0' COMMENT '是否删除',
  `tenant_id` varchar(12) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '租户Id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '用户活跃表';
ALTER TABLE t_active ADD station_id BIGINT(20) DEFAULT -1 COMMENT '站点id';
ALTER TABLE t_maintain MODIFY COLUMN maintain_before BIGINT(20) DEFAULT 0 COMMENT '保养任务提前派发时间';
ALTER TABLE t_maintain MODIFY COLUMN maintain_cycle BIGINT(20) DEFAULT 0 COMMENT '保养周期';
ALTER TABLE t_maintain MODIFY COLUMN maintain_after BIGINT(20) DEFAULT 0 COMMENT '保养任务过期的时间';
ALTER TABLE t_inspection MODIFY COLUMN inspection_before BIGINT(20) DEFAULT 0 COMMENT '巡检任务提前派发时间';
ALTER TABLE t_inspection MODIFY COLUMN inspection_cycle BIGINT(20) DEFAULT 0 COMMENT '巡检周期';
ALTER TABLE t_inspection MODIFY COLUMN inspection_after BIGINT(20) DEFAULT 0 COMMENT '巡检任务过期的时间';
ALTER TABLE t_inspection_task_record MODIFY COLUMN inspection_cycle BIGINT(20) DEFAULT 0 COMMENT '检查周期';
ALTER TABLE t_inspection_task_record MODIFY COLUMN expect_hours BIGINT(20) DEFAULT 0 COMMENT '期望工时';
ALTER TABLE t_inspection_task_record MODIFY COLUMN maintain_cycle BIGINT(20) DEFAULT 0 COMMENT '保养周期';
ALTER TABLE t_patrol MODIFY COLUMN task_push_ahead_duration BIGINT(20) DEFAULT 0 COMMENT '任务提前推送时长';
ALTER TABLE t_patrol MODIFY COLUMN check_cycle BIGINT(20) DEFAULT 0 COMMENT '检查周期';
ALTER TABLE t_patrol MODIFY COLUMN check_duration BIGINT(20) DEFAULT 0 COMMENT '期望检查时长';
ALTER TABLE t_inspection_task_record ADD is_count TINYINT(4) DEFAULT 1 COMMENT '是否参与统计 0:否 1:是';
ALTER TABLE t_sec_ins_rec ADD is_count TINYINT(4) DEFAULT 1 COMMENT '是否参与统计 0:否 1:是';
ALTER TABLE t_inspection_task_record MODIFY COLUMN task_status VARCHAR(32) DEFAULT '0' COMMENT '任务状态 0:未检 1:进行中 2:暂停 3:已检 4:已取消';
ALTER TABLE t_sec_ins_rec MODIFY COLUMN task_status VARCHAR(32) DEFAULT '0' COMMENT '任务状态 0:未检 1:进行中 2:暂停 3:已检 4:已取消';
ALTER TABLE t_repair MODIFY COLUMN assigned_person_id VARCHAR(32) DEFAULT '0' COMMENT '指派责任人id';
ALTER TABLE t_patrol ADD is_admin TINYINT(4) DEFAULT 1 COMMENT'是否admin 0:是 1:否';
ALTER TABLE t_inspection ADD is_admin TINYINT(4) DEFAULT 1 COMMENT'是否admin 0:是 1:否';
ALTER TABLE t_maintain ADD is_admin TINYINT(4) DEFAULT 1 COMMENT'是否admin 0:是 1:否';
ALTER TABLE t_news_push ADD work_time datetime COMMENT '上班时间';
ALTER TABLE t_news_push ADD work_status TINYINT(4) DEFAULT 0 COMMENT '上班状态 0:休息 1:空闲 2:忙碌 3:未上班 4:已下班 默认休息';
ALTER TABLE t_news_push ADD work_shift TINYINT(4) DEFAULT 0 COMMENT '上班班次 0:休息 1:早班 2:中班 3:晚班 4:全天 默认休息';
RENAME TABLE t_inspection_task_record TO t_inspection_task;
RENAME TABLE t_sec_ins_rec To t_safety_task;
ALTER TABLE t_news_push ADD off_work_time datetime COMMENT '下班时间';

DROP TABLE IF EXISTS `t_plan_count`;
CREATE TABLE `t_plan_count` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `plan_id` bigint(20) DEFAULT NULL COMMENT '计划id',
  `task_type` tinyint(4) DEFAULT '-1' COMMENT '任务类型 0:巡检 1:保养 2:维修 3:安全巡查',
  `task_time` datetime DEFAULT NULL COMMENT '任务执行时间',
  `task_responsible` bigint(20) DEFAULT NULL COMMENT '任务执行人',
  `create_user` bigint(64) DEFAULT NULL COMMENT '创建人',
  `create_dept` bigint(64) DEFAULT NULL COMMENT '创建部门',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` bigint(64) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `status` int(2) DEFAULT NULL COMMENT '状态',
  `is_deleted` int(2) DEFAULT '0' COMMENT '是否删除',
  `tenant_id` varchar(12) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '租户Id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT'计划统计表';
ALTER TABLE t_plan_count ADD site_id BIGINT(20) DEFAULT -1 COMMENT '位置id';
ALTER TABLE t_plan_count ADD station_id BIGINT(20) DEFAULT -1 COMMENT '站点id';
ALTER TABLE t_plan_count ADD is_admin TINYINT(4) DEFAULT 1 COMMENT '是否admin 0:是 1:否 默认否';
ALTER TABLE t_equipment_alarm ADD is_scan TINYINT(4) DEFAULT 0 COMMENT '是否浏览 0:未浏览 1:已浏览';
ALTER TABLE t_repair ADD dispatch_time datetime COMMENT '派单时间';
ALTER TABLE t_notice MODIFY COLUMN task_type tinyint(4) DEFAULT '-1' COMMENT '任务类型：0:巡检 1:保养 2:维修 3:安全检查 4:整改计划 5:倒闸票 6:工作票 7:停送电 8:换班 9:转单 10:人工报修';
ALTER TABLE t_notice ADD transfer_order_task_type TINYINT(4) DEFAULT -1 COMMENT '转单任务类型 0：巡检 1：保养 2：维修 3：安全检查';
ALTER TABLE t_equipment_alarm ADD site_name VARCHAR(32) COMMENT '位置名称';
ALTER TABLE t_equipment_alarm ADD station_name VARCHAR(32) COMMENT '站点名称';


##########################################################################################################
这个是另一个表里的！！！！
ALTER TABLE t_user_shift MODIFY COLUMN shift_cycle VARCHAR(4096) COMMENT '排版周期';
DROP TABLE IF EXISTS `t_shift`;
CREATE TABLE `t_shift` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `site_id` bigint(20) DEFAULT '-1' COMMENT '位置id',
  `station_id` bigint(20) DEFAULT '-1' COMMENT '站点id',
  `applicant` bigint(20) DEFAULT '-1' COMMENT '申请人',
  `applicant_name` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '申请人名称',
  `applicant_avatar` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '申请人头像',
  `applicant_dept_id` bigint(20) DEFAULT '-1' COMMENT '申请人部门id',
  `applicant_dept_name` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '申请人部门名称',
  `shifted_person` bigint(20) DEFAULT '-1' COMMENT '被换班人',
  `shifted_person_name` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '被换班人名称',
  `shifted_person_avatar` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '被换班人头像',
  `shift_date` datetime DEFAULT NULL COMMENT '换班日期',
  `shifted_date` datetime DEFAULT NULL COMMENT '被换班日期',
  `reason` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '换班原因',
  `shift_status` tinyint(4) DEFAULT '0' COMMENT '换班状态 0:待同意 1:已同意 2:已拒绝 ',
  `create_user` bigint(64) DEFAULT NULL COMMENT '创建人',
  `create_dept` bigint(64) DEFAULT NULL COMMENT '创建部门',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` bigint(64) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `status` int(2) DEFAULT NULL COMMENT '状态',
  `is_deleted` int(2) DEFAULT '0' COMMENT '是否删除',
  `tenant_id` varchar(12) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '租户Id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1315545764696424450 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '换班表';
##########################################################################################################



