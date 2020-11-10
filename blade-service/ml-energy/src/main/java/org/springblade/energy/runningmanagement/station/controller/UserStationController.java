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
package org.springblade.energy.runningmanagement.station.controller;

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.energy.runningmanagement.station.service.IUserStationService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  控制器
 *
 * @author bond
 * @since 2020-04-09
 */
@RestController
@AllArgsConstructor
@RequestMapping("/userstation")
@Api(value = "用户站点", tags = "用户站点接口")
public class UserStationController extends BladeController {

	private IUserStationService userStationService;
//
//	/**
//	 * 详情
//	 */
//	@GetMapping("/detail")
//	@ApiOperationSupport(order = 1)
//	@ApiOperation(value = "详情", notes = "传入userStation")
//	public R<UserStationVO> detail(UserStation userStation) {
//		UserStation detail = userStationService.getOne(Condition.getQueryWrapper(userStation));
//		return R.data(UserStationWrapper.build().entityVO(detail));
//	}
//
//	/**
//	 * 分页
//	 */
//	@GetMapping("/list")
//	@ApiOperationSupport(order = 2)
//	@ApiOperation(value = "分页", notes = "传入userStation")
//	public R<IPage<UserStationVO>> list(UserStation userStation, Query query) {
//		IPage<UserStation> pages = userStationService.page(Condition.getPage(query), Condition.getQueryWrapper(userStation));
//		return R.data(UserStationWrapper.build().pageVO(pages));
//	}
//
//
//	/**
//	 * 自定义分页
//	 */
//	@GetMapping("/page")
//	@ApiOperationSupport(order = 3)
//	@ApiOperation(value = "分页", notes = "传入userStation")
//	public R<IPage<UserStationVO>> page(UserStationVO userStation, Query query) {
//		IPage<UserStationVO> pages = userStationService.selectUserStationPage(Condition.getPage(query), userStation);
//		return R.data(pages);
//	}
//
//	/**
//	 * 新增
//	 */
//	@PostMapping("/save")
//	@ApiOperationSupport(order = 4)
//	@ApiOperation(value = "新增", notes = "传入userStation")
//	public R save(@Valid @RequestBody UserStation userStation) {
//		return R.status(userStationService.save(userStation));
//	}
//
//	/**
//	 * 修改
//	 */
//	@PostMapping("/update")
//	@ApiOperationSupport(order = 5)
//	@ApiOperation(value = "修改", notes = "传入userStation")
//	public R update(@Valid @RequestBody UserStation userStation) {
//		return R.status(userStationService.updateById(userStation));
//	}
//
//	/**
//	 * 新增或修改
//	 */
//	@PostMapping("/submit")
//	@ApiOperationSupport(order = 6)
//	@ApiOperation(value = "新增或修改", notes = "传入userStation")
//	public R submit(@Valid @RequestBody UserStation userStation) {
//		return R.status(userStationService.saveOrUpdate(userStation));
//	}
//
//
//	/**
//	 * 删除
//	 */
//	@PostMapping("/remove")
//	@ApiOperationSupport(order = 7)
//	@ApiOperation(value = "逻辑删除", notes = "传入ids")
//	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
//		return R.status(userStationService.deleteLogic(Func.toLongList(ids)));
//	}


}
