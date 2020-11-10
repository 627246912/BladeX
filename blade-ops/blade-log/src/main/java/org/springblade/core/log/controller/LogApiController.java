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
package org.springblade.core.log.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springblade.core.log.dto.LogApiDto;
import org.springblade.core.log.model.LogApi;
import org.springblade.core.log.service.ILogApiService;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.*;

/**
 * 控制器
 *
 * @author Chill
 */
@RestController
@AllArgsConstructor
@RequestMapping("/logApi")
@Api(value = "操作日志", tags = "操作日志接口")
public class LogApiController {

	private ILogApiService logService;

	/**
	 * 查询单条
	 */
	@GetMapping("/detail")
	public R<LogApi> detail(LogApi log) {
		return R.data(logService.getOne(Condition.getQueryWrapper(log)));
	}

	/**
	 * 查询多条(分页)
	 */
	@PostMapping("/page")
	public R<IPage<LogApi>> page(LogApiDto log, Query query) {
		IPage<LogApi> pages = logService.selectLogApiPage(Condition.getPage(query),log);
		return R.data(pages);
	}

	@GetMapping("/list")
	public R<IPage<LogApi>> list(LogApi log, Query query) {
		IPage<LogApi> pages = logService.page(Condition.getPage(query.setDescs("create_time")), Condition.getQueryWrapper(log));
		return R.data(pages);
	}

}
