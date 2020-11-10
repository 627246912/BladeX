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
import org.springblade.energy.energymanagement.dto.GasMeterDto;
import org.springblade.energy.energymanagement.entity.GasMeter;
import org.springblade.energy.energymanagement.mapper.GasMeterMapper;
import org.springblade.energy.energymanagement.service.IGasMeterService;
import org.springblade.system.entity.Dept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 能源管理-气能计量管账 服务实现类
 *
 * @author bond
 * @since 2020-06-22
 */
@Service
public class GasMeterServiceImpl extends BaseServiceImpl<GasMeterMapper, GasMeter> implements IGasMeterService {
	@Autowired
	private BladeRedisCache redisCache;
	@Override
	public IPage<GasMeterDto> selectGasMeterPage(IPage<GasMeterDto> page, GasMeterDto dto){
		List<GasMeterDto> list =baseMapper.selectGasMeterPage(page, dto);
		for(GasMeterDto meterDto:list){
			if(Func.isNotEmpty(meterDto.getDeptId())){
				Dept dept = redisCache.hGet(CacheNames.DEPT_KEY, meterDto.getDeptId());
				if(Func.isNotEmpty(dept)){
					meterDto.setDeptName(dept.getDeptName());
				}
			}

		}
		return page.setRecords(list);
	}

	public List<GasMeterDto> getGasMeterItem(Map<String,Object> map){
		List<GasMeterDto> list = baseMapper.getGasMeterItem(map);
		for(GasMeterDto meterDto:list){
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
