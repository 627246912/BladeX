package org.springblade.energy.qualitymanagement.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springblade.energy.securitymanagement.enums.Status;
import org.springblade.energy.securitymanagement.util.DataFactory;
import org.springblade.energy.securitymanagement.util.PageQuery;
import org.springblade.energy.securitymanagement.util.PageUtils;
import org.springblade.energy.qualitymanagement.entity.GasInspection;
import org.springblade.energy.qualitymanagement.service.GasInspectionService;
import org.springframework.web.bind.annotation.*;


@Api(value = "质量体系--供气巡检", tags = "质量体系--供气巡检接口")
@RestController
@AllArgsConstructor
@RequestMapping("/system/gas")
public class GasInspectionController {


	GasInspectionService systemQualityGasInspectionService;

	@ApiOperationSupport(order = 1)
	@ApiOperation("添加")
	@PostMapping("/save")
	public R<GasInspection> addSystemQualityGasInspection(@RequestBody GasInspection systemQualityGasInspection) {
		return systemQualityGasInspectionService.save(new DataFactory<GasInspection>().nameFactory(systemQualityGasInspection)) ? R.success(Status.ADD_SUCCESS.getVal()) : R.fail(Status.ADD_ERROR.getVal());
	}


	@ApiOperationSupport(order = 2)
	@ApiOperation("列表 & 条件查询")
	@GetMapping("/page")
	public R<PageUtils> listSystemQualityGasInspection(PageQuery pageQuery, GasInspection systemQualityGasInspection) {
		Page<GasInspection> page = systemQualityGasInspectionService.page(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), new QueryWrapper<>(systemQualityGasInspection).eq("is_deleted", 0).orderByDesc("create_time"));
		return R.data(new PageUtils(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
	}


	@ApiOperationSupport(order = 3)
	@ApiOperation("修改")
	@PutMapping("/update")
	public R<GasInspection> updateSystemQualityGasInspection(@RequestBody GasInspection systemQualityGasInspection) {
		return systemQualityGasInspectionService.update(new DataFactory<GasInspection>().nameFactory(systemQualityGasInspection), new QueryWrapper<GasInspection>().eq("id", systemQualityGasInspection.getId())) ? R.success(Status.UPDATE_SUCCESS.getVal()) : R.fail(Status.UPDATE_ERROR.getVal());
	}


	@ApiOperationSupport(order = 4)
	@ApiOperation("删除")
	@PostMapping("/remove")
	public R<GasInspection> deleteSystemQualityGasInspection(@RequestBody GasInspection systemQualityGasInspection) {
		return systemQualityGasInspectionService.remove(new QueryWrapper<GasInspection>().eq("id", systemQualityGasInspection.getId())) ? R.success(Status.UPDATE_SUCCESS.getVal()) : R.fail(Status.DELETE_ERROR.getVal());
	}
}
