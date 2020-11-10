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
package org.springblade.energy.runningmanagement.standingbook.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 台账--柜体实体类
 *
 * @author bond
 * @since 2020-04-03
 */
@Data
@TableName("t_equipment_cable")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "EquipmentCable对象", description = "台账--电缆")
public class EquipmentCable extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 设备编号
	*/
		@ApiModelProperty(value = "设备编号")
		private String code;
	/**
	* 设备名称
	*/
		@ApiModelProperty(value = "设备名称")
		private String name;
	/**
	* 设备型号
	*/
		@ApiModelProperty(value = "设备型号")
		private String modelNo;
	/**
	* 开始站点
	*/
		@ApiModelProperty(value = "开始站点")
		@JsonSerialize(
			using = ToStringSerializer.class
		)
		private Long startStationId;
	/**
	* 结束站点
	*/
		@ApiModelProperty(value = "结束站点")
		@JsonSerialize(
			using = ToStringSerializer.class
		)
		private Long endStationId;
	/**
	* 长度
	*/
		@ApiModelProperty(value = "长度")
		private Integer cableLength;
	@ApiModelProperty(value = "设备类别")
	private String deviceType;

}
