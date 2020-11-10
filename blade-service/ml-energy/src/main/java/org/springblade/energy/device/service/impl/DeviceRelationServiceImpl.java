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
package org.springblade.energy.device.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.energy.device.entity.DeviceRelation;
import org.springblade.energy.device.mapper.DeviceRelationMapper;
import org.springblade.energy.device.service.IDeviceRelationService;
import org.springblade.energy.device.vo.DeviceRelationVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 站点网关表 服务实现类
 *
 * @author bond
 * @since 2020-03-16
 */
@Service
public class DeviceRelationServiceImpl extends BaseServiceImpl<DeviceRelationMapper, DeviceRelation> implements IDeviceRelationService {

	public List<DeviceRelationVO> getDeviceList(DeviceRelation deviceRelation){
		return baseMapper.getDeviceList(deviceRelation);
	}

	@Override
	public IPage<DeviceRelationVO> selectDeviceRelationPage(IPage<DeviceRelationVO> page, DeviceRelationVO deviceRelation) {
		return page.setRecords(baseMapper.selectDeviceRelationPage(page, deviceRelation));
	}

	public List<DeviceRelation> getDevices(List<String> devices){
		return baseMapper.getDevices(devices);
	}
}
