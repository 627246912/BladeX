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
 * 实体类
 *
 * @author bond
 * @since 2020-03-31
 */
@Data
@TableName("t_diagram_product")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "DiagramProduct对象", description = "DiagramProduct对象")
public class DiagramProduct extends TenantEntity {

	private static final long serialVersionUID = 1L;


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
	* 父节点
	*/
		@ApiModelProperty(value = "父节点")
		private String parentId;
	/**
	* 系统图ID
	*/
		@ApiModelProperty(value = "系统图ID")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long diagramId;
	/**
	* 产品ID
	*/
		@ApiModelProperty(value = "产品ID")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long productId;

	@ApiModelProperty(value = "资产编码")
	private String assetCode;


	@ApiModelProperty(value = "产品类型")
	private String productDtype;

	@ApiModelProperty(value = "网关ID")
	private String did;

	@ApiModelProperty(value = "rtuidcb")
	private String rtuidcb;

	@ApiModelProperty(value = "产品别名")
	private String productcname;


	@ApiModelProperty(value = "排序")
	private String pindex;

	@ApiModelProperty(value = "变压器中低压关联")
	private String mesolow;

	@ApiModelProperty(value = "用电部门")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long deptId;

	@ApiModelProperty(value = "水表，燃气表等级")
	private Integer grade;

	@ApiModelProperty(value = "用电类型key")
	private Integer electricTypekey;

}
