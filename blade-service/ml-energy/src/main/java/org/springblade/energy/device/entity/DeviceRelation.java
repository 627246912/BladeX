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
package org.springblade.energy.device.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 站点网关表实体类
 *
 * @author bond
 * @since 2020-03-16
 */
@Data
@TableName("t_device_relation")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "DeviceRelation对象", description = "站点网关表")
public class DeviceRelation extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 站点id
	*/
		@ApiModelProperty(value = "站点id")
		@JsonSerialize(
			using = ToStringSerializer.class
		)
		private Long stationId;
	/**
	* 设备id
	*/
		@ApiModelProperty(value = "设备id")
		private String did;
	/**
	* 公司id
	*/
		@ApiModelProperty(value = "公司id")
		private Long pid;


}
