package org.springblade.energy.securitymanagement.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springblade.energy.securitymanagement.entity.CompanyInfo;
import org.springblade.energy.securitymanagement.enums.Status;
import org.springblade.energy.securitymanagement.service.CompanyInfoService;
import org.springblade.energy.securitymanagement.util.PageQuery;
import org.springblade.energy.securitymanagement.util.PageUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/security/company")
@Api(value = "安全管理--公司信息", tags = "安全管理--公司信息接口")
@AllArgsConstructor
public class CompanyInfoController {

	CompanyInfoService companyInfoService;

	@ApiOperationSupport(order = 1)
	@ApiOperation("添加")
	@PostMapping("/save")
	public R<CompanyInfo> addCompanyInfo(@RequestBody @Valid CompanyInfo companyInfo) {
		if (!companyInfoService.save(companyInfo))
			return R.fail(Status.ADD_ERROR.getVal());
		return R.success(Status.ADD_SUCCESS.getVal());
	}


	@ApiOperationSupport(order = 2)
	@ApiOperation("列表")
	@GetMapping("/page")
	public R<PageUtils> listCompanyInfo(PageQuery pageQuery, CompanyInfo companyInfo) {
		Page<CompanyInfo> page = companyInfoService.page(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), new QueryWrapper<>(companyInfo).orderByDesc("create_time"));
		return R.data(new PageUtils(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
	}


	@ApiOperationSupport(order = 3)
	@ApiOperation("编辑")
	@PutMapping("/update")
	public R<CompanyInfo> updateCompanyInfo(@RequestBody CompanyInfo companyInfo) {
		if (!
			companyInfoService.update(companyInfo, new QueryWrapper<CompanyInfo>()
				.eq("id", companyInfo.getId())
				.eq("is_deleted", 0)))
			return R.fail(Status.UPDATE_ERROR.getVal());
		return R.success(Status.UPDATE_SUCCESS.getVal());
	}


	@ApiOperationSupport(order = 4)
	@ApiOperation("批量删除")
	@PostMapping("/remove")
	public R<CompanyInfo> batchDeleted(@RequestBody List<CompanyInfo> params) {
		if (params.size() <= 0)
			return R.fail(Status.DELETE_ERROR.getVal());
		params.stream().distinct().forEach((c) -> {
			companyInfoService.remove(new QueryWrapper<CompanyInfo>().eq("id", c.getId()));
		});
		return R.success(Status.DELETE_SUCCESS.getVal());
	}
}
