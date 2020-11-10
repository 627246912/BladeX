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
 * 供水供气系统图展示数据项数据传输对象实体类
 *
 * @author bond
 * @since 2020-06-02
 */
@Data
public class DiagramShowItemReq  {
	private static final long serialVersionUID = 1L;

	/**
	 * 网关id
	 */
	@ApiModelProperty(value = "网关id")
	private String did;

	@ApiModelProperty(value = "rtuidcb")
	private String rtuidcb;

	@ApiModelProperty(value = "排序")
	private String pindex;
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
	/**
	 * 属性ID
	 */
	@ApiModelProperty(value = "属性ID")
	@JsonSerialize(
		using = ToStringSerializer.class
	)
	private Long propertyId;


}
