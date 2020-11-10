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
package org.springblade.energy.deptcost.entity;

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
 * @since 2020-08-07
 */
@Data
@TableName("t_turn_unit")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TurnUnit对象", description = "TurnUnit对象")
public class TurnUnit extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 单位名称
	*/
		@ApiModelProperty(value = "单位名称")
		private String unitName;
	/**
	* 读表方式1手动，2自动
	*/
		@ApiModelProperty(value = "读表方式1手动，2自动")
		private String readType;
	@ApiModelProperty(value = "自动读取数据项")
	private String itemId;


}
