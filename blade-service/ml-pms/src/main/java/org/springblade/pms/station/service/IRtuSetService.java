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
package org.springblade.pms.station.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;
import org.springblade.dto.NameValue;
import org.springblade.pms.station.entity.RtuSet;
import org.springblade.pms.station.vo.RtuSetVO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  服务类
 *
 * @author bond
 * @since 2020-08-20
 */
public interface IRtuSetService extends BaseService<RtuSet> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param gwcomSet
	 * @return
	 */
	IPage<RtuSetVO> selectGwcomSetPage(IPage<RtuSetVO> page, RtuSetVO gwcomSet);

	List<RtuSet> selectGwcomSetList(Map<String,Object> map);

	/**
	 * 查询监测分路用户统计
	 * @param gwIds
	 * @return
	 */
	List<NameValue> selectGwcomUserGroupCount(Set<String> gwIds);
	List<NameValue> selectStationUserGroupCount(Set<String> gwIds);

}
