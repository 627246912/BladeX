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
package org.springblade.pms.station.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.tool.api.R;
import org.springblade.pms.station.entity.SysArea;
import org.springblade.pms.station.service.ISysAreaService;
import org.springblade.pms.station.vo.AreaTree;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *  控制器
 *
 * @author bond
 * @since 2020-03-13
 */
@RestController
@AllArgsConstructor
@RequestMapping("/sysarea")
@Api(value = "区域表", tags = "区域接口")
public class SysAreaController extends BladeController {

	private ISysAreaService sysAreaService;


	/**
	 * 查询区域树
	 */
	@GetMapping("/getAreaTree")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "查询区域树", notes = "传入areaCode")
	public R<List<AreaTree>> getAreaTree(String areaCode) {
		List<AreaTree> list = sysAreaService.getAreaTree(areaCode);
		return R.data(list);
	}

	/**
	 * 查询areaCode下级区域
	 */
	@GetMapping("/getArea")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "查询areaCode下级区域", notes = "传入areaCode")
	public R<List<SysArea>> getChildAreaList(String areaCode) {
		List<SysArea> list = sysAreaService.getChildAreaList(areaCode);
		return R.data(list);
	}

//	/**
//	 * 详情
//	 */
//	@GetMapping("/detail")
//	@ApiOperationSupport(order = 1)
//	@ApiOperation(value = "详情", notes = "传入sysArea")
//	public R<SysAreaVO> detail(SysArea sysArea) {
//		SysArea detail = sysAreaService.getOne(Condition.getQueryWrapper(sysArea));
//		return R.data(SysAreaWrapper.build().entityVO(detail));
//	}
//
//	/**
//	 * 分页
//	 */
//	@GetMapping("/list")
//	@ApiOperationSupport(order = 2)
//	@ApiOperation(value = "分页", notes = "传入sysArea")
//	public R<IPage<SysAreaVO>> list(SysArea sysArea, Query query) {
//		IPage<SysArea> pages = sysAreaService.page(Condition.getPage(query), Condition.getQueryWrapper(sysArea));
//		return R.data(SysAreaWrapper.build().pageVO(pages));
//	}
//
//
//	/**
//	 * 自定义分页
//	 */
//	@GetMapping("/page")
//	@ApiOperationSupport(order = 3)
//	@ApiOperation(value = "分页", notes = "传入sysArea")
//	public R<IPage<SysAreaVO>> page(SysAreaVO sysArea, Query query) {
//		IPage<SysAreaVO> pages = sysAreaService.selectSysAreaPage(Condition.getPage(query), sysArea);
//		return R.data(pages);
//	}
//
//	/**
//	 * 新增
//	 */
//	@PostMapping("/save")
//	@ApiOperationSupport(order = 4)
//	@ApiOperation(value = "新增", notes = "传入sysArea")
//	public R save(@Valid @RequestBody SysArea sysArea) {
//		return R.status(sysAreaService.save(sysArea));
//	}
//
//	/**
//	 * 修改
//	 */
//	@PostMapping("/update")
//	@ApiOperationSupport(order = 5)
//	@ApiOperation(value = "修改", notes = "传入sysArea")
//	public R update(@Valid @RequestBody SysArea sysArea) {
//		return R.status(sysAreaService.updateById(sysArea));
//	}
//
//	/**
//	 * 新增或修改
//	 */
//	@PostMapping("/submit")
//	@ApiOperationSupport(order = 6)
//	@ApiOperation(value = "新增或修改", notes = "传入sysArea")
//	public R submit(@Valid @RequestBody SysArea sysArea) {
//		return R.status(sysAreaService.saveOrUpdate(sysArea));
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
//		return R.status(sysAreaService.deleteLogic(Func.toLongList(ids)));
//	}


}
