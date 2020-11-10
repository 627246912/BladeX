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
package org.springblade.resource.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import org.springblade.core.mp.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 产品模型编码实体类
 *
 * @author bond
 * @since 2020-07-14
 */
@Data
@TableName("t_product_ptype")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ProductPtype对象", description = "产品模型编码")
public class ProductPtype extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 1遥测2遥信3遥调4遥控5计算遥测6计算遥信
	*/
		@ApiModelProperty(value = "1遥测2遥信3遥调4遥控5计算遥测6计算遥信")
		private Integer stype;
	/**
	* 属性code
	*/
		@ApiModelProperty(value = "属性code")
		private Integer ptype;
	/**
	* 名称
	*/
		@ApiModelProperty(value = "名称")
		private String name;
	/**
	* 简称
	*/
		@ApiModelProperty(value = "简称")
		private String cname;
	/**
	* 单位
	*/
		@ApiModelProperty(value = "单位")
		private String unit;


}
