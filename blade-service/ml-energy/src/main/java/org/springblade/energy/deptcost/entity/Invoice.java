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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 实体类
 *
 * @author bond
 * @since 2020-07-24
 */
@Data
@TableName("t_invoice")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Invoice对象", description = "Invoice对象")
public class Invoice extends TenantEntity {

	private static final long serialVersionUID = 1L;


	/**
	* 部门id
	*/
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "部门id")
	private Long deptId;
	/**
	* 发票金额
	*/
		@ApiModelProperty(value = "发票金额")
		private Double invoicePrice;
	/**
	* 发票编号
	*/
		@ApiModelProperty(value = "发票编号")
		private String invoiceNo;
	/**
	* 类型1电，2水，3气
	*/
		@ApiModelProperty(value = "类型1电，2水，3气")
		private Integer invoiceType;


	@ApiModelProperty("费用时间（格式月份：如2020-07）")
	private String invoiceTime;

}
