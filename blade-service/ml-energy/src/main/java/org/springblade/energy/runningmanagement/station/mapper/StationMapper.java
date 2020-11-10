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
package org.springblade.energy.runningmanagement.station.mapper;

import org.apache.ibatis.annotations.Param;
import org.springblade.energy.runningmanagement.station.entity.Station;
import org.springblade.energy.runningmanagement.station.vo.StationVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;
import java.util.Set;

/**
 * 站点信息表 Mapper 接口
 *
 * @author bond
 * @since 2020-03-16
 */
public interface StationMapper extends BaseMapper<Station> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param station
	 * @return
	 */
	List<StationVO> selectStationPage(IPage page, StationVO station);

    StationVO getStationByDid(@Param(value = "did") String did);

	List<StationVO> selectStationsByDeviceIds(@Param(value = "deviceIds") Set<String> deviceIds);
}
