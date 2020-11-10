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
package org.springblade.energy.equipmentmanagement.eec.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 重点能耗设备-》仪表配置实体类
 *
 * @author bond
 * @since 2020-05-06
 */
@Data
@TableName("t_eec_meter")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "EecMeter对象", description = "重点能耗设备-》仪表配置")
public class EecMeter extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 站点ID
	 */
	@ApiModelProperty(value = "站点ID")
	@JsonSerialize(
		using = ToStringSerializer.class
	)
	private Long stationId;
	/**
	 * 位置ID
	 */
	@ApiModelProperty(value = "位置ID")
	@JsonSerialize(
		using = ToStringSerializer.class
	)
	private Long siteId;

	@ApiModelProperty(value = "设备编号")
	private String equNo;

	@ApiModelProperty(value = "性质1:户式中央空调 2:商用中央空调")
	private Integer nature;

	@ApiModelProperty(value = "性质名称")
	private String natureName;

	@ApiModelProperty(value = "设备型号")
	private String equType;

	@ApiModelProperty(value = "单位面积")
	private Float unitArea;

	@ApiModelProperty(value = "投入时间")
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	private Date putTime;


}
