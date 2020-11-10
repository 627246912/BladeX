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
package org.springblade.energy.runningmanagement.standingbook.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.energy.runningmanagement.standingbook.entity.EquipmentTransformer;
import org.springblade.energy.runningmanagement.standingbook.vo.EquipmentTransformerVO;

import java.util.Objects;

/**
 * 台账--变压器包装类,返回视图层所需的字段
 *
 * @author bond
 * @since 2020-04-03
 */
public class EquipmentTransformerWrapper extends BaseEntityWrapper<EquipmentTransformer, EquipmentTransformerVO>  {

	public static EquipmentTransformerWrapper build() {
		return new EquipmentTransformerWrapper();
 	}

	@Override
	public EquipmentTransformerVO entityVO(EquipmentTransformer equipmentTransformer) {
		EquipmentTransformerVO equipmentTransformerVO = Objects.requireNonNull(BeanUtil.copy(equipmentTransformer, EquipmentTransformerVO.class));

		return equipmentTransformerVO;
	}

}
