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
package org.springblade.energy.runningmanagement.station.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;
import org.springblade.energy.runningmanagement.station.entity.Station;
import org.springblade.energy.runningmanagement.station.vo.StationVO;

import java.util.List;
import java.util.Set;

/**
 * 站点信息表 服务类
 *
 * @author bond
 * @since 2020-03-16
 */
public interface IStationService extends BaseService<Station> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param station
	 * @return
	 */
	IPage<StationVO> selectStationPage(IPage<StationVO> page, StationVO station);

	StationVO getStationByDid(String did);

	List<StationVO> selectStationsByDeviceIds(Set<String> deviceIds);

}
