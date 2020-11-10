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
package org.springblade.pms.station.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 实体类
 *
 * @author bond
 * @since 2020-08-18
 */
@Data
@TableName("t_base_station")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "BaseStation对象", description = "BaseStation对象")
public class BaseStation extends TenantEntity {

	private static final long serialVersionUID = 1L;



	@ApiModelProperty(value = "网关id")
	private String gwId;
	/**
	* 站点名称
	*/

	@ApiModelProperty(value = "站点编号")
	private String stationNo;
		@ApiModelProperty(value = "站点名称")
		private String stationName;
	/**
	* 设备名称
	*/
		@ApiModelProperty(value = "设备名称")
		private String deviceName;
	/**
	* 省份code
	*/
		@ApiModelProperty(value = "省份code")
		private String provinceCode;
	/**
	* 省份
	*/
		@ApiModelProperty(value = "省份")
		private String provinceName;
	/**
	* 城市code
	*/
		@ApiModelProperty(value = "城市code")
		private String cityCode;
	/**
	* 城市
	*/
		@ApiModelProperty(value = "城市")
		private String cityName;
	/**
	* 区县code
	*/
		@ApiModelProperty(value = "区县code")
		private String countyCode;
	/**
	* 区县
	*/
		@ApiModelProperty(value = "区县")
		private String countyName;
	/**
	* 详细地址
	*/
		@ApiModelProperty(value = "详细地址")
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

	@ApiModelProperty(value = "产权属性")
	private String property;
	@ApiModelProperty(value = "责任人")
	private String dutyer;
	@ApiModelProperty(value = "电话")
	private String phone;

}
