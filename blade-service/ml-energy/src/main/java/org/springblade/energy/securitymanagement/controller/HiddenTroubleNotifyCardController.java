package org.springblade.energy.securitymanagement.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springblade.energy.securitymanagement.entity.CompanyInfo;
import org.springblade.energy.securitymanagement.entity.HiddenTroubleNotifyCard;
import org.springblade.energy.securitymanagement.enums.Status;
import org.springblade.energy.securitymanagement.service.CompanyInfoService;
import org.springblade.energy.securitymanagement.service.HiddenTroubleNotifyCardService;
import org.springblade.energy.securitymanagement.util.DataFactory;
import org.springblade.energy.securitymanagement.util.PageQuery;
import org.springblade.energy.securitymanagement.util.PageUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/security/hidden")
@Api(value = "安全管理--安全隐患告知卡", tags = "安全管理--安全隐患告知卡接口")
@AllArgsConstructor
public class HiddenTroubleNotifyCardController {

	HiddenTroubleNotifyCardService hiddenTroubleNotifyCardService;
	CompanyInfoService companyInfoService;

	@ApiOperationSupport(order = 1)
	@ApiOperation("添加")
	@PostMapping("/save")
	public R<HiddenTroubleNotifyCard> addHiddenTroubleNotifyCard(@RequestBody @Valid HiddenTroubleNotifyCard hiddenTroubleNotifyCard) {
		if (companyInfoService.count(new QueryWrapper<CompanyInfo>().eq("id", hiddenTroubleNotifyCard.getRelated())) <= 0)
			return R.fail("无相关公司信息,请先添加公司信息");
		if (!hiddenTroubleNotifyCardService.save(new DataFactory<HiddenTroubleNotifyCard>().nameFactory(hiddenTroubleNotifyCard)))
			return R.fail(Status.ADD_ERROR.getVal());
		return R.success(Status.ADD_SUCCESS.getVal());
	}


	@ApiOperationSupport(order = 2)
	@ApiOperation("列表")
	@GetMapping("/page")
	public R<PageUtils> listHiddenTroubleNotifyCard(PageQuery pageQuery, HiddenTroubleNotifyCard hiddenTroubleNotifyCard) {
		Page<HiddenTroubleNotifyCard> page = hiddenTroubleNotifyCardService.page(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), new QueryWrapper<>(hiddenTroubleNotifyCard).orderByDesc("create_time"));
		return R.data(new PageUtils(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
	}


	@ApiOperationSupport(order = 3)
	@ApiOperation("编辑")
	@PutMapping("/update")
	public R<HiddenTroubleNotifyCard> updateHiddenTroubleNotifyCard(@RequestBody HiddenTroubleNotifyCard hiddenTroubleNotifyCard) {
		if (!
			hiddenTroubleNotifyCardService.update(new DataFactory<HiddenTroubleNotifyCard>().nameFactory(hiddenTroubleNotifyCard)
				, new QueryWrapper<HiddenTroubleNotifyCard>()
					.eq("id", hiddenTroubleNotifyCard.getId())
					.eq("is_deleted", 0)))
			return R.fail(Status.UPDATE_ERROR.getVal());
		return R.success(Status.UPDATE_SUCCESS.getVal());
	}


	@ApiOperationSupport(order = 4)
	@ApiOperation("批量删除")
	@PostMapping("/remove")
	public R<HiddenTroubleNotifyCard> batchDeletedHiddenTroubleNotifyCard(@RequestBody List<HiddenTroubleNotifyCard> params) {
		if (params.size() <= 0)
			return R.fail(Status.DELETE_ERROR.getVal());
		params.forEach((h) -> hiddenTroubleNotifyCardService.remove(new QueryWrapper<HiddenTroubleNotifyCard>().eq("id", h.getId())));
		return R.success(Status.DELETE_SUCCESS.getVal());
	}
}
