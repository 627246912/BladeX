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
package org.springblade.energy.runningmanagement.station.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 位置信息表实体类
 *
 * @author bond
 * @since 2020-03-16
 */
@Data
@TableName("t_site")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Site对象", description = "位置信息表")
public class Site extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 位置名称
	*/
		@ApiModelProperty(value = "位置名称")
		private String siteName;
	/**
	* 位置分类id
	*/
		@ApiModelProperty(value = "位置分类id")
		private Integer typeId;
	/**
	* 位置分类名称
	*/
		@ApiModelProperty(value = "位置分类名称")
		private String typeName;
	/**
	* 站点id
	*/
		@ApiModelProperty(value = "站点id")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long stationId;
	/**
	* 项目id
	*/
		@ApiModelProperty(value = "项目id")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long projectId;
	/**
	* 父位置id
	*/
		@ApiModelProperty(value = "父位置id")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long parentId;
	/**
	* 所属级别
	*/
		@ApiModelProperty(value = "所属级别")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long siteRank;
	/**
	* 备注
	*/
		@ApiModelProperty(value = "备注")
		private String remark;

	@ApiModelProperty(value = "所属部门id 多个用英文逗号分隔")
	private String deptId;

}
