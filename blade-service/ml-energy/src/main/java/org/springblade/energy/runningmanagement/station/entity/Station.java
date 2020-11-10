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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 站点信息表实体类
 *
 * @author bond
 * @since 2020-03-16
 */
@Data
@TableName("t_station")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Station对象", description = "站点信息表")
public class Station extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 站点名称
	*/
		@ApiModelProperty(value = "站点名称")
		private String name;
	/**
	* 供电区域
	*/
		@ApiModelProperty(value = "供电区域")
		private String powerArea;
	/**
	* 站点类型 0：其他，1：配电房2：箱变 3:变压器
	*/
		@ApiModelProperty(value = "站点类型 0：其他，1：配电房2：箱变 3:变压器")
		private Integer type;
	/**
	* 性质
	*/
		@ApiModelProperty(value = "性质")
		private String property;
	/**
	* 容量
	*/
		@ApiModelProperty(value = "容量")
		private Double capacity;
	/**
	* 单价
	*/
		@ApiModelProperty(value = "单价")
		private Double price;


		@ApiModelProperty(value = "尖时间")
		private String top;

		@ApiModelProperty(value = "峰时间")
		private String peak;

		@ApiModelProperty(value = "平时间")
		private String flat;

		@ApiModelProperty(value = "谷时间")
		private String valley;
	/**
	* 尖价格
	*/
		@ApiModelProperty(value = "尖价格")
		private Double topPrice;
	/**
	* 峰价格
	*/
		@ApiModelProperty(value = "峰价格")
		private Double peakPrice;
	/**
	* 平价格
	*/
		@ApiModelProperty(value = "平价格")
		private Double flatPrice;
	/**
	* 谷价格
	*/
		@ApiModelProperty(value = "谷价格")
		private Double valleyPrice;
	/**
	* 所属省份
	*/
		@ApiModelProperty(value = "所属省份")
		private String provinceCode;
	/**
	* 所属城市
	*/
		@ApiModelProperty(value = "所属城市")
		private String cityCode;
	/**
	* 区域编码
	*/
		@ApiModelProperty(value = "区域编码")
		private String areaCode;
	/**
	* 省
	*/
		@ApiModelProperty(value = "省")
		private String province;
	/**
	* 市
	*/
		@ApiModelProperty(value = "市")
		private String city;
	/**
	* 区县
	*/
		@ApiModelProperty(value = "区县")
		private String county;
	/**
	* 地址
	*/
		@ApiModelProperty(value = "地址")
		private String address;
	/**
	* 经度
	*/
		@ApiModelProperty(value = "经度")
		private Double lng;
	/**
	* 纬度
	*/
		@ApiModelProperty(value = "纬度")
		private Double lat;
	/**
	* 站点平面图
	*/
		@ApiModelProperty(value = "站点平面图")
		private String planeImg;
	/**
	* 站点走向图
	*/
		@ApiModelProperty(value = "站点走向图")
		private String flowImg;
	/**
	* 站点拓扑图
	*/
		@ApiModelProperty(value = "站点拓扑图")
		private String topologyImg;


	@ApiModelProperty(value = "水价格")
	private Double waterPrice;
	@ApiModelProperty(value = "气价格")
	private Double gasPrice;



}
