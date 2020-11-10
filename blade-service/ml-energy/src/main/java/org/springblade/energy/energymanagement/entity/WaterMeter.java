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
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 能源管理-水能计量管账实体类
 *
 * @author bond
 * @since 2020-06-22
 */
@Data
@TableName("t_water_meter")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "WaterMeter对象", description = "能源管理-水能计量管账")
public class WaterMeter extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 数据项id
	*/
	@ApiModelProperty(value = "数据项id")
	private String itemId;

	@ApiModelProperty(value = "价格")
	@TableField("price")
	private Double price;
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@ApiModelProperty(value = "计量时间")
	private Date meterTime;
	@ApiModelProperty(value = "计量方式")
	private Integer meterType;
	}
