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

import java.util.List;

/**
 * 系统图基本信息数据传输对象实体类
 *
 * @author bond
 * @since 2020-03-26
 */
@Data
public class DiagramReq {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "主键ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;

	/**
	 * 站点ID
	 */
	@ApiModelProperty(value = "站点ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long stationId;
	/**
	 * 位置ID
	 */
	@ApiModelProperty(value = "位置ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long siteId;
	/**
	 * 图形分类
	 */
	@ApiModelProperty(value = "图形分类")
	private String diagramType;
	/**
	 * 图名称
	 */
	@ApiModelProperty(value = "图名称")
	private String diagramName;

	@ApiModelProperty(value = "图形TM分类")
	private String tmType;

	/**
	 * 网关
	 */
	@ApiModelProperty(value = "网关")
	private String did;

	@ApiModelProperty(value = "图形数据")
	private String diagramData;


	@ApiModelProperty(value = "产品列表")
	private List<DiagramProductReq> products;

	@ApiModelProperty(value = "产品属性(数据项)列表")
	private List<DiagramItemReq> DiagramItemReq;

	@ApiModelProperty(value = "供水供气数据项展示列表")
	private List<DiagramShowItemReq> diagramShowItemReq;

	@ApiModelProperty(value = "背景图片")
	private String bkImage;

}
