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
package org.springblade.pms.station.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

import java.util.Date;

/**
 * 实体类
 *
 * @author bond
 * @since 2020-08-20
 */
@Data
@TableName("t_rtu_set")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "端口对象", description = "端口对象")
public class RtuSet extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 网关id
	*/
		@ApiModelProperty(value = "网关id")
		private String gwId;

	/**
	 * com口
	 */
	@ApiModelProperty(value = "端口")
	private Integer port;
	/**
	* com口
	*/
		@ApiModelProperty(value = "rtuid")
		private String rtuid;
	/**
	* rtuid
	*/
		@ApiModelProperty(value = "端口组合")
		private String rtuidcb;
	/**
	* 分配用户
	*/
		@ApiModelProperty(value = "分配用户")
		private String userGroup;
	/**
	* 额定电流
	*/
		@ApiModelProperty(value = "额定电流")
		private Float ratedI;
	/**
	* 电流预警百分比
	*/
		@ApiModelProperty(value = "电流预警百分比")
		private Float warnI;
	/**
	* 电流告警百分比
	*/
		@ApiModelProperty(value = "电流告警百分比")
		private Float alarmI;
	/**
	* com名称
	*/
		@ApiModelProperty(value = "端口名称")
		private String rtuname;

	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
		@ApiModelProperty(value = "启用时间")
		private Date activateTime;

		@ApiModelProperty(value = "日最大用电量")
		private Float dayMaxPower;

	@ApiModelProperty(value = "定时控制0禁止,1启用")
	private Integer timeContorStatus;

	@ApiModelProperty(value = "开关状态0分，1合")
	private Integer  switchStatus;


	@ApiModelProperty(value = "下电时长Min")
	private Integer  dischargeTimes;

	@ApiModelProperty(value = "下电电压V")
	private Integer  dischargeVoltage;


}
