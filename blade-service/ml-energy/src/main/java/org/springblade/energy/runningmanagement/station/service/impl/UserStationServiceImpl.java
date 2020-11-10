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
package org.springblade.energy.runningmanagement.station.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.energy.runningmanagement.station.entity.UserStation;
import org.springblade.energy.runningmanagement.station.mapper.UserStationMapper;
import org.springblade.energy.runningmanagement.station.service.IUserStationService;
import org.springblade.energy.runningmanagement.station.vo.UserStationVO;
import org.springframework.stereotype.Service;

/**
 *  服务实现类
 *
 * @author bond
 * @since 2020-04-09
 */
@Service
public class UserStationServiceImpl extends BaseServiceImpl<UserStationMapper, UserStation> implements IUserStationService{

	@Override
	public IPage<UserStationVO> selectUserStationPage(IPage<UserStationVO> page, UserStationVO userStation) {
		return page.setRecords(baseMapper.selectUserStationPage(page, userStation));
	}
	public boolean removeByUid(Long uid){
		return baseMapper.removeByUid(uid);
	}
}
