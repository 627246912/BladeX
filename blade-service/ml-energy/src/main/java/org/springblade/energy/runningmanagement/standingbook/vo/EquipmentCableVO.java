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
package org.springblade.energy.runningmanagement.standingbook.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.energy.runningmanagement.standingbook.entity.EquipmentCable;

/**
 * 台账--柜体视图实体类
 *
 * @author bond
 * @since 2020-04-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "EquipmentCableVO对象", description = "台账--电缆")
public class EquipmentCableVO extends EquipmentCable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "开始站点")
	private String startStationName;
	@ApiModelProperty(value = "结束站点")
	private String endStationName;

}
