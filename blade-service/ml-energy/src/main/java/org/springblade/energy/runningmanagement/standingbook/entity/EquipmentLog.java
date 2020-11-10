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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 设备运行记录表实体类
 *
 * @author bond
 * @since 2020-04-09
 */
@Data
@TableName("t_equipment_log")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "EquipmentLog对象", description = "设备运行记录表")
public class EquipmentLog extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 设备id
	*/
		@ApiModelProperty(value = "设备id")
		private Integer equipmentId;
	/**
	* 记录类型
	*/
		@ApiModelProperty(value = "记录类型")
		private Integer logType;
	/**
	* 备注
	*/
		@ApiModelProperty(value = "备注")
		private String remark;


}
