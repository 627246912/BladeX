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
 * 单位电量转供月份数据实体类
 *
 * @author bond
 * @since 2020-08-07
 */
@Data
@TableName("t_turn_unit_data")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TurnUnitData对象", description = "单位电量转供月份数据")
public class TurnUnitData extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 单位id
	*/
		@ApiModelProperty(value = "单位id")
		private Long unitId;
	/**
	* 电度
	*/
		@ApiModelProperty(value = "电度")
		private Double val;

	@ApiModelProperty(value = "电度费用")
	private Double cost;
	/**
	* 费用月份
	*/
		@ApiModelProperty(value = "费用月份")
		private String monthTime;


}
