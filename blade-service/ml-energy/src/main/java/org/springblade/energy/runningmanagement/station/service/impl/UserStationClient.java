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

import lombok.AllArgsConstructor;
import org.springblade.energy.feign.IUserStationClient;
import org.springblade.energy.runningmanagement.station.entity.UserStation;
import org.springblade.energy.runningmanagement.station.service.IUserStationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 *  服务实现类
 *
 * @author bond
 * @since 2020-04-09
 */
@RestController
@AllArgsConstructor
public class UserStationClient implements IUserStationClient {
	private IUserStationService iUserStationService;

	@Override
	@PostMapping(SAVEORUPDATE)
	public boolean saveStationClient(Long uid, String sid) {
		boolean res=iUserStationService.removeByUid(uid);
		List<UserStation> list=new ArrayList<>();
		if(res){
			String	sids []=sid.split(",");
			for(int i=0;i<sids.length;i++){
				UserStation userStation=new UserStation();
				userStation.setStationId(Long.valueOf(sids[i]));
				userStation.setUid(Long.valueOf(uid));
				list.add(userStation);
			}
		}
		res=iUserStationService.saveBatch(list);
		return res;
	}
}
