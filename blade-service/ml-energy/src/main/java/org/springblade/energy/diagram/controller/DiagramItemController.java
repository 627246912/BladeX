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
package org.springblade.energy.diagram.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.diagram.entity.DiagramItem;
import org.springblade.energy.diagram.service.IDiagramItemService;
import org.springblade.energy.diagram.vo.DiagramItemVO;
import org.springblade.energy.diagram.wrapper.DiagramItemWrapper;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 系统图网关数据项 控制器
 *
 * @author bond
 * @since 2020-04-15
 */
@RestController
@AllArgsConstructor
@RequestMapping("/diagramitem")
//@Api(value = "系统图网关数据项", tags = "系统图网关数据项接口")
public class DiagramItemController extends BladeController {

	private IDiagramItemService diagramItemService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入diagramItem")
	public R<DiagramItemVO> detail(DiagramItem diagramItem) {
		DiagramItem detail = diagramItemService.getOne(Condition.getQueryWrapper(diagramItem));
		return R.data(DiagramItemWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 系统图网关数据项
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入diagramItem")
	public R<IPage<DiagramItemVO>> list(DiagramItem diagramItem, Query query) {
		IPage<DiagramItem> pages = diagramItemService.page(Condition.getPage(query), Condition.getQueryWrapper(diagramItem));
		return R.data(DiagramItemWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 系统图网关数据项
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入diagramItem")
	public R<IPage<DiagramItemVO>> page(DiagramItemVO diagramItem, Query query) {
		IPage<DiagramItemVO> pages = diagramItemService.selectDiagramItemPage(Condition.getPage(query), diagramItem);
		return R.data(pages);
	}

	/**
	 * 新增 系统图网关数据项
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入diagramItem")
	public R save(@Valid @RequestBody DiagramItem diagramItem) {
		return R.status(diagramItemService.save(diagramItem));
	}

	/**
	 * 修改 系统图网关数据项
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入diagramItem")
	public R update(@Valid @RequestBody DiagramItem diagramItem) {
		return R.status(diagramItemService.updateById(diagramItem));
	}

	/**
	 * 新增或修改 系统图网关数据项
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入diagramItem")
	public R submit(@Valid @RequestBody DiagramItem diagramItem) {
		return R.status(diagramItemService.saveOrUpdate(diagramItem));
	}


	/**
	 * 删除 系统图网关数据项
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(diagramItemService.deleteLogic(Func.toLongList(ids)));
	}


}
