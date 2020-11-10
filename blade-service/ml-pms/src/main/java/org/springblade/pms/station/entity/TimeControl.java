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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;

/**
 * 实体类
 *
 * @author bond
 * @since 2020-09-23
 */
@Data
@TableName("t_time_control")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TimeControl对象", description = "TimeControl对象")
public class TimeControl extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 表t_rtu_set 的id
	*/
	@JsonSerialize(
		using = ToStringSerializer.class
	)
		@ApiModelProperty(value = "表t_rtu_set 的id")
		private Long rtuSetId;
	/**
	* 组
	*/
		@ApiModelProperty(value = "组")
		private Integer grade;
	/**
	* 定时控制0停,1开
	*/
		@ApiModelProperty(value = "定时控制0停,1开")
		private Integer onOff;
	/**
	* 定时开时间
	*/

		@ApiModelProperty(value = "定时时间")
		private String time;



}
