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
package org.springblade.pms.station.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 数据传输对象实体类
 *
 * @author bond
 * @since 2020-08-20
 */
@Data
public class RtuSetReq {
	private static final long serialVersionUID = 1L;

	@JsonSerialize(
		using = ToStringSerializer.class
	)
	@ApiModelProperty("主键id")
	@TableId(
		value = "id",
		type = IdType.ASSIGN_ID
	)
	private Long id;
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

	@ApiModelProperty(value = "日最大用电量")
	private Float dayMaxPower;

	@ApiModelProperty(value = "下电时长Min")
	private Integer  dischargeTimes;

	@ApiModelProperty(value = "下电电压V")
	private Integer  dischargeVoltage;
}
