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
package org.springblade.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;

/**
 * 产品基础表实体类
 *
 * @author bond
 * @since 2020-03-24
 */
@Data
@TableName("t_product")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Product对象", description = "产品基础表")
public class Product extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 产品名称
	*/
		@ApiModelProperty(value = "产品名称")
		private String productName;
	/**
	* 产品编码
	*/
		@ApiModelProperty(value = "产品设备类别")
		private String productDtype;
	/**
	* 产品类别
	*/
		@ApiModelProperty(value = "产品分组")
		@JsonSerialize(
			using = ToStringSerializer.class
		)
		private Long productType;
	/**
	* 资产编码
	*/
		@ApiModelProperty(value = "资产编码")
		private String assetCode;

	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "产品图标")
	private String productIcon;

	@ApiModelProperty(value = "产品页面布局")
	private String layout;

}
