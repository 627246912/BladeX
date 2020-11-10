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
import org.springblade.energy.deptcost.dto.DeptInvoiceDTO;
import org.springblade.energy.deptcost.entity.Invoice;
import org.springblade.energy.deptcost.service.IInvoiceService;
import org.springblade.energy.deptcost.vo.InvoiceVO;
import org.springblade.enums.EnergyType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 *  控制器
 *
 * @author bond
 * @since 2020-07-24
 */
@RestController
@AllArgsConstructor
@RequestMapping("/invoice")
@Api(value = "能源管理-费用管理", tags = "能源管理-费用管理")
public class InvoiceController extends BladeController {

	private IInvoiceService invoiceService;





	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "总电费核算", notes = "传入invoice")
	public R<IPage<DeptInvoiceDTO>> page(InvoiceVO invoice, Query query) {
		String	time=invoice.getInvoiceTime();
		Integer type= invoice.getInvoiceType();
		if(Func.isEmpty(time)){
			return R.fail("时间不能为空");
		}

		if(Func.isEmpty(type)){
			return R.fail("水电气类型不能为空");
		}
		if(Func.isEmpty(EnergyType.getValue(type))){
			return R.fail("水电气类型不正确");
		}


		IPage<DeptInvoiceDTO> pages = invoiceService.selectDeptInvoicePage(Condition.getPage(query), invoice);
		return R.data(pages);
	}
	/**
	 * 录入发票新增或修改
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "录入发票新增或修改", notes = "传入invoice")
	public R submitInvoice(@Valid @RequestBody List<Invoice> invoices) {
		return R.status(invoiceService.saveOrUpdateBatch(invoices));
	}


	/**
	 * 删除发票
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "删除发票", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(invoiceService.deleteLogic(Func.toLongList(ids)));
	}


}
