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
package org.springblade.energy.deptcost.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.deptcost.dto.TurnInvoiceDTO;
import org.springblade.energy.deptcost.entity.TurnInvoice;
import org.springblade.energy.deptcost.service.ITurnInvoiceService;
import org.springblade.energy.deptcost.vo.TurnInvoiceVO;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 电量转供发票 控制器
 *
 * @author bond
 * @since 2020-08-07
 */
@RestController
@AllArgsConstructor
@RequestMapping("/turninvoice")
@Api(value = "电量转供发票", tags = "能源管理-费用管理")
public class TurnInvoiceController extends BladeController {

	private ITurnInvoiceService turnInvoiceService;





	@GetMapping("/page")
	@ApiOperationSupport(order = 11)
	@ApiOperation(value = "转供电费核算", notes = "传入invoice")
	public R<IPage<TurnInvoiceDTO>> page(TurnInvoiceVO invoice, Query query) {
		String	time=invoice.getInvoiceTime();
		if(Func.isEmpty(time)){
			return R.fail("时间不能为空");
		}
		IPage<TurnInvoiceDTO> pages = turnInvoiceService.selectTurnInvoicePage(Condition.getPage(query), invoice);
		return R.data(pages);
	}


	/**
	 * 新增 电量转供发票
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 34)
	@ApiOperation(value = "电量转供发票-新增", notes = "传入turnInvoice")
	public R submit(@Valid @RequestBody List<TurnInvoice> turnInvoices) {
		return R.status(turnInvoiceService.saveOrUpdateBatch(turnInvoices));
	}

	/**
	 * 删除 电量转供发票
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 37)
	@ApiOperation(value = "电量转供发票-逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(turnInvoiceService.deleteLogic(Func.toLongList(ids)));
	}


}
