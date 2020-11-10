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

import org.springblade.pms.station.entity.TimeControl;
import org.springblade.pms.station.vo.TimeControlVO;
import org.springblade.core.mp.base.BaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 *  服务类
 *
 * @author bond
 * @since 2020-09-23
 */
public interface ITimeControlService extends BaseService<TimeControl> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param timeControl
	 * @return
	 */
	IPage<TimeControlVO> selectTimeControlPage(IPage<TimeControlVO> page, TimeControlVO timeControl);

}
