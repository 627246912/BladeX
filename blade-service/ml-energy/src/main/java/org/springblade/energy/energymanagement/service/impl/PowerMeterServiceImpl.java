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
package org.springblade.energy.energymanagement.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.common.cache.CacheNames;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.energymanagement.dto.PowerMeterDto;
import org.springblade.energy.energymanagement.entity.PowerMeter;
import org.springblade.energy.energymanagement.mapper.PowerMeterMapper;
import org.springblade.energy.energymanagement.service.IPowerMeterService;
import org.springblade.system.entity.Dept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 能源管理-电能计量管账 服务实现类
 *
 * @author bond
 * @since 2020-06-22
 */
@Service
public class PowerMeterServiceImpl extends BaseServiceImpl<PowerMeterMapper, PowerMeter> implements IPowerMeterService {
@Autowired
private BladeRedisCache redisCache;

	@Override
	public IPage<PowerMeterDto> selectPowerMeterPage(IPage<PowerMeterDto> page, PowerMeterDto dto){
		List<PowerMeterDto> list =baseMapper.selectPowerMeterPage(page, dto);
		for(PowerMeterDto meterDto:list){
			if(Func.isNotEmpty(meterDto.getDeptId())){
				Dept dept = redisCache.hGet(CacheNames.DEPT_KEY, meterDto.getDeptId());
				if(Func.isNotEmpty(dept)){
					meterDto.setDeptName(dept.getDeptName());
				}
			}

		}
		return page.setRecords(list);
	}

	public List<PowerMeterDto> getPowerMeterItem(Map<String,Object> map){
		List<PowerMeterDto> list = baseMapper.getPowerMeterItem(map);
		for(PowerMeterDto meterDto:list){
			if(Func.isNotEmpty(meterDto.getDeptId())) {
				Dept dept = redisCache.hGet(CacheNames.DEPT_KEY, meterDto.getDeptId());
				if (Func.isNotEmpty(dept)) {
					meterDto.setDeptName(dept.getDeptName());
				}
			}
		}

		return list;
	}

}
