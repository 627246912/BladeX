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
package org.springblade.energy.runningmanagement.standingbook.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

import java.time.LocalDateTime;

/**
 * 台账--变压器实体类
 *
 * @author bond
 * @since 2020-04-03
 */
@Data
@TableName("t_equipment_transformer")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "EquipmentTransformer对象", description = "台账--变压器")
public class EquipmentTransformer extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 设备编号
	*/
		@ApiModelProperty(value = "设备编号")
		private String code;
	/**
	* 设备名称
	*/
		@ApiModelProperty(value = "设备名称")
		private String name;
	/**
	* 设备型号
	*/
		@ApiModelProperty(value = "设备型号")
		private String modelNo;
	/**
	* 所属站点
	*/
		@ApiModelProperty(value = "所属站点")
		@JsonSerialize(
			using = ToStringSerializer.class
		)
		private Long stationId;
	/**
	* 设备位置
	*/
		@ApiModelProperty(value = "设备位置")
		@JsonSerialize(
			using = ToStringSerializer.class
		)
		private Long siteId;
	/**
	* 投入时间
	*/
		@ApiModelProperty(value = "投入时间")
		@JsonFormat(
			pattern = "yyyy-MM-dd HH:mm:ss"
		)
		private LocalDateTime devoteTime;
	/**
	* 厂家
	*/
		@ApiModelProperty(value = "厂家")
		private String factory;
	/**
	* 用电单位
	*/
		@ApiModelProperty(value = "用电单位")
		private String useUnit;
	/**
	* 设备图片
	*/
		@ApiModelProperty(value = "设备图片")
		private String picture;
	/**
	* 设备二维码图片
	*/
		@ApiModelProperty(value = "设备二维码图片")
		private String qrCode;
	/**
	* 额定电压
	*/
		@ApiModelProperty(value = "额定电压")
		private Double voltage;
	/**
	* 额定电流
	*/
		@ApiModelProperty(value = "额定电流")
		private Double electricity;
	/**
	* 额定容量
	*/
		@ApiModelProperty(value = "额定容量")
		private Double capacity;
	/**
	* 铜损
	*/
		@ApiModelProperty(value = "铜损")
		private Double copperLoss;
	/**
	* 铁损
	*/
		@ApiModelProperty(value = "铁损")
		private Double ironLoss;

	@ApiModelProperty(value = "设备类别")
	private String deviceType;

	@ApiModelProperty(value = "系统图id")
	private Long diagramId;


}
