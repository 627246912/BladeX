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
package org.springblade.energy.diagram.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 供水供气系统图展示数据项实体类
 *
 * @author bond
 * @since 2020-06-02
 */
@Data
@TableName("t_diagram_show_item")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "DiagramShowItem对象", description = "供水供气系统图展示数据项")
public class DiagramShowItem extends TenantEntity {

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
	/**
	* 系统图ID
	*/
		@ApiModelProperty(value = "系统图ID")
		@JsonSerialize(
			using = ToStringSerializer.class
		)
		private Long diagramId;
	/**
	* 网关id
	*/
		@ApiModelProperty(value = "网关id")
		private String did;
	@ApiModelProperty(value = "rtuidcb")
	private String rtuidcb;
	/**
	* 数据项
	*/
		@ApiModelProperty(value = "数据项")
		private String itemId;
	/**
	* 系统图产品id
	*/
		@ApiModelProperty(value = "系统图产品id")
		@JsonSerialize(
			using = ToStringSerializer.class
		)
		private Long diagramProductId;
	/**
	* 产品id
	*/
		@ApiModelProperty(value = "产品id")
		@JsonSerialize(
			using = ToStringSerializer.class
		)
		private Long productId;
	/**
	* 属性
	*/
		@ApiModelProperty(value = "属性")
		private String propertyCode;

		private String pindex;

	/**
	* 属性ID
	*/
		@ApiModelProperty(value = "属性ID")
		@JsonSerialize(
			using = ToStringSerializer.class
		)
		private Long propertyId;


}
