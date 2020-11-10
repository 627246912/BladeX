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
package org.springblade.energy.equipmentmanagement.eec.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.energy.equipmentmanagement.eec.entity.EecMeter;
import org.springblade.energy.equipmentmanagement.eec.vo.EecMeterVO;

import java.util.Objects;

/**
 * 重点能耗设备-》仪表配置包装类,返回视图层所需的字段
 *
 * @author bond
 * @since 2020-05-06
 */
public class EecMeterWrapper extends BaseEntityWrapper<EecMeter, EecMeterVO>  {

	public static EecMeterWrapper build() {
		return new EecMeterWrapper();
 	}

	@Override
	public EecMeterVO entityVO(EecMeter eecMeter) {
		EecMeterVO eecMeterVO = Objects.requireNonNull(BeanUtil.copy(eecMeter, EecMeterVO.class));

		return eecMeterVO;
	}

}
