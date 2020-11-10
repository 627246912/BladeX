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
package org.springblade.pms.station.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.bean.DeviceCommunicationStatus;
import org.springblade.constants.GwsubscribeConstant;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.utils.Func;
import org.springblade.dto.NameValue;
import org.springblade.pms.config.SystemConfig;
import org.springblade.pms.enums.AreaRank;
import org.springblade.pms.station.dto.AreaResp;
import org.springblade.pms.station.dto.BaseStationDTO;
import org.springblade.pms.station.entity.BaseStation;
import org.springblade.pms.station.entity.SysArea;
import org.springblade.pms.station.mapper.BaseStationMapper;
import org.springblade.pms.station.service.IBaseStationService;
import org.springblade.pms.station.service.IRtuSetService;
import org.springblade.pms.station.service.ISysAreaService;
import org.springblade.pms.station.vo.BaseStationVO;
import org.springblade.util.RedisKeysUtil;
import org.springblade.util.TreeParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *  服务实现类
 *
 * @author bond
 * @since 2020-08-18
 */
@Service
public class BaseStationServiceImpl extends BaseServiceImpl<BaseStationMapper, BaseStation> implements IBaseStationService {
	@Autowired
	private SystemConfig systemConfig;
	@Autowired
	private BladeRedisCache redisCache;

	@Autowired
	private ISysAreaService iSysAreaService;
	@Autowired
	private IRtuSetService iGwcomSetService;

	@Autowired
	private org.springblade.pms.gw.feign.IDeviceClient iDeviceClient;

	@Override
	public IPage<BaseStationDTO> selectBaseStationPage(IPage<BaseStationDTO> page, BaseStationVO baseStation) {

		List<BaseStationDTO> list= baseMapper.selectBaseStationPage(page, baseStation);
		String key= RedisKeysUtil.getKey(systemConfig.getServerIdCard(), GwsubscribeConstant.DEVICE_CODE_TO_STATUS);

		for(BaseStationDTO dto:list) {
			if(Func.isNotEmpty(dto.getGwId())){
				DeviceCommunicationStatus sta=redisCache.hGet(key, dto.getGwId());
				if(Func.isNotEmpty(sta)) {
					dto.setEquipmentStatus(sta.getStatus());
				}
			}

		}
		return page.setRecords(list);

	}
	public List<BaseStationVO> selectStationsByDeviceIds(Set<String> deviceIds){
		return baseMapper.selectStationsByDeviceIds(deviceIds);
	}
	public List<BaseStation> selectStationsByAreaCode(String areaCode){
		return baseMapper.selectStationsByAreaCode(areaCode);
	}

	/**
	 *查询基站区域树
	 */
	public List<AreaResp> getAreaStationTree(String areaCode){
		List<AreaResp> areaResps=new ArrayList<>();

		List<BaseStation> baseStationList=baseMapper.selectStationsByAreaCode(areaCode);

		if(Func.isEmpty(baseStationList)){
			return  areaResps;
		}

		Set<String> areaCodeSet = new HashSet<>();
		AreaResp areaResp;
		int i=1;
		for (BaseStation baseStation : baseStationList) {
			areaResp = new AreaResp();
			areaResp.setAreaCode(String.valueOf(baseStation.getId()));
			areaResp.setAreaName(baseStation.getStationName());
			areaResp.setAreaRank(AreaRank.STATION.id);
			areaResp.setOrderNum(i);
			areaResp.setParentId(baseStation.getCountyCode());
			if(Func.isEmpty(baseStation.getCountyCode())){
				areaResp.setParentId(baseStation.getCityCode());
			}
			if(Func.isEmpty(baseStation.getCityCode())){
				areaResp.setParentId(baseStation.getProvinceCode());
			}
			areaResp.setDisStr("S");
			areaResps.add(areaResp);


			if(Func.isNotEmpty(baseStation.getProvinceCode())){
				areaCodeSet.add(baseStation.getProvinceCode());
			}

			if(Func.isNotEmpty(baseStation.getCityCode())){
				areaCodeSet.add(baseStation.getCityCode());
			}

			if(Func.isNotEmpty(baseStation.getCountyCode())){
				areaCodeSet.add(baseStation.getCountyCode());
			}
			i++;
		}

		if(Func.isEmpty(areaResps)){
			return  areaResps;
		}

		//获取区域信息
		List<SysArea>  sysAreaList= iSysAreaService.getAreaListByCodes(areaCodeSet);
		for(SysArea area :sysAreaList){
			AreaResp entity = new AreaResp();
			entity.setAreaCode(area.getAreaCode());
			entity.setAreaName(area.getAreaName());
			entity.setAreaRank(area.getAreaRank());
			entity.setOrderNum(area.getOrderNum());
			entity.setParentId(area.getParentId());
			areaResps.add(entity);
		}

		return TreeParser.getTreeListByFilter(areaCode,areaResps,true,new String());

	}


	public List<NameValue> getDistribution(Set<String> areaCodes){
		return baseMapper.getDistribution(areaCodes);
	}

	public List<SysArea> getChildAreaStationList(String areaCode){
		return baseMapper.getChildAreaStationList(areaCode);
	}
}
