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
package org.springblade.pms.alarmmanagement.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

import java.time.LocalDateTime;

/**
 * 设备告警表实体类
 *
 * @author bond
 * @since 2020-04-08
 */
@Data
@TableName("t_equipment_alarm")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "EquipmentAlarm对象", description = "设备告警表")
public class EquipmentAlarm extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 告警id
	*/
		@ApiModelProperty(value = "告警id")
		@TableField("alarmId")
	private Integer alarmId;

	@ApiModelProperty(value = "网关id")
	@TableField("gwId")
	private String gwId;

	/**
	* 告警数据项id
	*/
		@ApiModelProperty(value = "告警数据项id")
		@TableField("alarmItemId")
	private String alarmItemId;
	/**
	* 设备id
	*/
		@ApiModelProperty(value = "设备id")
		private Long equipmentId;
	/**
	* 设备名称
	*/
		@ApiModelProperty(value = "设备名称")
		private String equipmentName;
	/**
	* 设备类型id
	*/
		@ApiModelProperty(value = "设备类型id")
		private Long equipmentTypeId;
	/**
	* 位置id
	*/
		@ApiModelProperty(value = "位置id")
		@JsonSerialize(
			using = ToStringSerializer.class
		)
		private Long siteId;
	/**
	* 站点id
	*/
		@ApiModelProperty(value = "站点id")
		@JsonSerialize(
			using = ToStringSerializer.class
		)
		private Long stationId;
	/**
	* 地址
	*/
		@ApiModelProperty(value = "地址")
		private String address;
	/**
	* 告警内容
	*/
		@ApiModelProperty(value = "告警内容")
		private String alarmContent;
	/**
	* 告警等级
	*/
		@ApiModelProperty(value = "告警等级")
		private Integer level;
	/**
	* 告警状态
	*/
		@ApiModelProperty(value = "告警状态")
		private Integer alarmStatus;
	/**
	* 告警时间
	*/
		@ApiModelProperty(value = "告警时间")
		@JsonFormat(
			pattern = "yyyy-MM-dd HH:mm:ss"
		)
		private LocalDateTime alarmTime;
	/**
	* 告警结束时间
	*/
		@ApiModelProperty(value = "告警结束时间")
		@JsonFormat(
			pattern = "yyyy-MM-dd HH:mm:ss"
		)
		private LocalDateTime alarmEndTime;
	/**
	* 处理状态
	*/
		@ApiModelProperty(value = "处理状态")
		private Integer handleStatus;

		@ApiModelProperty(value = "告警类型")
		private Integer alarmType;

	@ApiModelProperty(value = "影响用户")
	private String userGroup;

}
