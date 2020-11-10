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
package org.springblade.energy.diagram.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 系统图网关数据项数据传输对象实体类
 *
 * @author bond
 * @since 2020-04-15
 */
@Data
public class QueryDiagramItemReq {
	private static final long serialVersionUID = 1L;


	/**
	 * 系统图ID
	 */
	@ApiModelProperty(value = "系统图ID")
	@JsonSerialize(
		using = ToStringSerializer.class
	)
	private Long diagramId;

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
	 * 系统图产品id
	 */
	@ApiModelProperty(value = "系统图产品id")
	@JsonSerialize(
		using = ToStringSerializer.class
	)
	private Long diagramProductId;


	/**
	 * 网关id
	 */
	@ApiModelProperty(value = "网关id")
	private String did;

	@ApiModelProperty(value = "rtuidcb")
	private String rtuidcb;

	/**
	 * 产品id
	 */
	@ApiModelProperty(value = "产品id")
	@JsonSerialize(
		using = ToStringSerializer.class
	)
	private Long productId;

	@ApiModelProperty(value = "排序")
	private String pindex;
}
