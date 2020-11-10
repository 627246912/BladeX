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
package org.springblade.energy.energymanagement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 实体类
 *
 * @author bond
 * @since 2020-07-04
 */
@Data
@TableName("t_energy_consume_type")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "EnergyConsumeType对象", description = "EnergyConsumeType对象")
public class EnergyConsumeType extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 消耗类型
	*/
		@ApiModelProperty(value = "消耗类型")
		@TableId(value = "consume_type", type = IdType.AUTO)
	private Integer consumeType;
	/**
	* 名称
	*/
		@ApiModelProperty(value = "名称")
		private String consumeName;
	/**
	* 能源类型1供电2供水3供气
	*/
		@ApiModelProperty(value = "能源类型1供电2供水3供气")
		private Integer energyType;


}
