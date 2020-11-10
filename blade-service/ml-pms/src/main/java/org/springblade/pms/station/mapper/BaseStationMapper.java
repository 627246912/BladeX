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
package org.springblade.pms.station.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.springblade.dto.NameValue;
import org.springblade.pms.station.dto.BaseStationDTO;
import org.springblade.pms.station.entity.BaseStation;
import org.springblade.pms.station.entity.SysArea;
import org.springblade.pms.station.vo.BaseStationVO;

import java.util.List;
import java.util.Set;

/**
 *  Mapper 接口
 *
 * @author bond
 * @since 2020-08-18
 */
public interface BaseStationMapper extends BaseMapper<BaseStation> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param baseStation
	 * @return
	 */
	List<BaseStationDTO> selectBaseStationPage(IPage page, BaseStationVO baseStation);

	public List<BaseStationVO> selectStationsByDeviceIds(@Param("deviceIds")  Set<String> deviceIds);

	public List<BaseStation> selectStationsByAreaCode(@Param("areaCode") String areaCode);
	public List<NameValue> getDistribution(@Param("areaCodes")  Set<String> areaCodes);

	List<SysArea> getChildAreaStationList(String areaCode);

}
