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
package org.springblade.energy.device.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.energy.device.entity.DeviceRelation;
import org.springblade.energy.device.vo.DeviceRelationVO;
import java.util.Objects;

/**
 * 站点网关表包装类,返回视图层所需的字段
 *
 * @author bond
 * @since 2020-03-16
 */
public class DeviceRelationWrapper extends BaseEntityWrapper<DeviceRelation, DeviceRelationVO>  {

	public static DeviceRelationWrapper build() {
		return new DeviceRelationWrapper();
 	}

	@Override
	public DeviceRelationVO entityVO(DeviceRelation deviceRelation) {
		DeviceRelationVO deviceRelationVO = Objects.requireNonNull(BeanUtil.copy(deviceRelation, DeviceRelationVO.class));

		return deviceRelationVO;
	}

}
