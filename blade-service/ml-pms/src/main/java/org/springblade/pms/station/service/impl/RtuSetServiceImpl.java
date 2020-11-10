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
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.dto.NameValue;
import org.springblade.pms.station.entity.RtuSet;
import org.springblade.pms.station.mapper.RtuSetMapper;
import org.springblade.pms.station.service.IRtuSetService;
import org.springblade.pms.station.vo.RtuSetVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  服务实现类
 *
 * @author bond
 * @since 2020-08-20
 */
@Service
public class RtuSetServiceImpl extends BaseServiceImpl<RtuSetMapper, RtuSet> implements IRtuSetService {

	@Override
	public IPage<RtuSetVO> selectGwcomSetPage(IPage<RtuSetVO> page, RtuSetVO gwcomSet) {
		return page.setRecords(baseMapper.selectGwcomSetPage(page, gwcomSet));
	}

	public List<RtuSet> selectGwcomSetList(Map<String,Object> map){
		return baseMapper.selectGwcomSetList(map);
	}

	/**
	 * 查询监测分路用户统计
	 * @param gwIds
	 * @return
	 */
	public List<NameValue> selectGwcomUserGroupCount(Set<String> gwIds){
		return baseMapper.selectGwcomUserGroupCount(gwIds);
	}

	/**
	 * 查询站点用户统计
	 * @param gwIds
	 * @return
	 */
	public List<NameValue> selectStationUserGroupCount(Set<String> gwIds){
		return baseMapper.selectStationUserGroupCount(gwIds);
	}
}
