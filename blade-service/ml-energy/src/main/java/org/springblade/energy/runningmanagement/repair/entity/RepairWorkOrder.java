/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package org.springblade.energy.runningmanagement.repair.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户报修工单表实体类
 *
 * @author bond
 * @since 2020-04-14
 */
@Data
@TableName("t_repair_work_order")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "RepairWorkOrder对象", description = "用户报修工单表")
public class RepairWorkOrder extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 报修或告警id
	*/
		@ApiModelProperty(value = "报修或告警id")
		private Long repairId;
	/**
	* 位置id
	*/
		@ApiModelProperty(value = "位置id")
		private Integer siteId;
	/**
	* 站点id
	*/
		@ApiModelProperty(value = "站点id")
		private Integer sid;
	/**
	* 派单员
	*/
		@ApiModelProperty(value = "派单员")
		private Integer sendUid;
	/**
	* 优先级
	*/
		@ApiModelProperty(value = "优先级")
		private Integer priority;
	/**
	* 派单方式 0:手动 1:自动
	*/
		@ApiModelProperty(value = "派单方式 0:手动 1:自动")
		private Boolean sendType;
	/**
	* 工单类型:0:用户报修工单,1:告警工单
	*/
		@ApiModelProperty(value = "工单类型:0:用户报修工单,1:告警工单")
		private Integer workType;
	/**
	* 要求完成时间(小时)
	*/
		@ApiModelProperty(value = "要求完成时间(小时)")
		private BigDecimal askHour;
	/**
	* 责任人
	*/
		@ApiModelProperty(value = "责任人")
		private Integer dutyUid;
	/**
	* 完成时间
	*/
		@ApiModelProperty(value = "完成时间")
		private LocalDateTime completeTime;
	/**
	* 开始时间
	*/
		@ApiModelProperty(value = "开始时间")
		private LocalDateTime startTime;
	/**
	* 派单时间
	*/
		@ApiModelProperty(value = "派单时间")
		private LocalDateTime sendTime;
	/**
	* 描述
	*/
		@ApiModelProperty(value = "描述")
		private String remark;
	/**
	* 设备id
	*/
		@ApiModelProperty(value = "设备id")
		private Integer equipmentId;
	/**
	* 设备类型id
	*/
		@ApiModelProperty(value = "设备类型id")
		private Integer equipmentTypeId;


}
