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
package org.springblade.demo.service.impl;

import org.springblade.demo.entity.Demo;
import org.springblade.demo.vo.DemoVO;
import org.springblade.demo.mapper.DemoMapper;
import org.springblade.demo.service.IDemoService;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 *  服务实现类
 *
 * @author bond
 * @since 2020-03-13
 */
@Service
public class DemoServiceImpl extends BaseServiceImpl<DemoMapper, Demo> implements IDemoService {

	@Override
	public IPage<DemoVO> selectDemoPage(IPage<DemoVO> page, DemoVO demo) {
		return page.setRecords(baseMapper.selectDemoPage(page, demo));
	}

}
