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

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 能源管理-电能计量管账实体类
 *
 * @author bond
 * @since 2020-06-22
 */
@Data
@TableName("t_power_meter")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "PowerMeter对象", description = "能源管理-电能计量管账")
public class PowerMeter extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 数据项id
	*/
		@ApiModelProperty(value = "数据项id")
		private String itemId;
	/**
	* 变比
	*/
		@ApiModelProperty(value = "变比")
		private Float ctratio;
	/**
	* 尖时间
	*/
		@ApiModelProperty(value = "尖时间")
		private String top;
	/**
	* 峰时间
	*/
		@ApiModelProperty(value = "峰时间")
		private String peak;
	/**
	* 平时间
	*/
		@ApiModelProperty(value = "平时间")
		private String flat;
	/**
	* 谷时间
	*/
		@ApiModelProperty(value = "谷时间")
		private String valley;
	/**
	* 尖价格
	*/
		@ApiModelProperty(value = "尖价格")
		@TableField("topPrice")
	private Double topPrice;
	/**
	* 峰价格
	*/
		@ApiModelProperty(value = "峰价格")
		@TableField("peakPrice")
	private Double peakPrice;
	/**
	* 平价格
	*/
		@ApiModelProperty(value = "平价格")
		@TableField("flatPrice")
	private Double flatPrice;
	/**
	* 谷价格
	*/
		@ApiModelProperty(value = "谷价格")
		@TableField("valleyPrice")
	private Double valleyPrice;
	/**
	* 电类型0中压，1低压 2直流
	*/
		@ApiModelProperty(value = "电类型0中压，1低压 2直流")
		private String diagramType;


}
