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
import org.springblade.energy.qualitymanagement.entity.WaterInspection;
import org.springblade.energy.qualitymanagement.service.WaterInspectionService;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
@Api(value = "质量体系--供水巡检", tags = "质量体系--供水巡检接口")
@RequestMapping("/system/water")
public class WaterInspectionController {

	WaterInspectionService systemQualityWaterInspectionService;

	@ApiOperationSupport(order = 1)
	@ApiOperation("添加")
	@PostMapping("/save")
	public R<WaterInspection> addSystemQualityWaterInspection(@RequestBody WaterInspection systemQualityWaterInspection) {
		return systemQualityWaterInspectionService.save(new DataFactory<WaterInspection>().nameFactory(systemQualityWaterInspection)) ? R.success(Status.ADD_SUCCESS.getVal()) : R.fail(Status.ADD_ERROR.getVal());
	}


	@ApiOperationSupport(order = 2)
	@ApiOperation("列表 & 条件查询")
	@GetMapping("/page")
	public R<PageUtils> listSystemQualityWaterInspection(PageQuery pageQuery, WaterInspection systemQualityWaterInspection) {
		Page<WaterInspection> page = systemQualityWaterInspectionService.page(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), new QueryWrapper<>(systemQualityWaterInspection).orderByDesc("create_time"));
		return R.data(new PageUtils(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
	}


	@ApiOperationSupport(order = 3)
	@ApiOperation("修改")
	@PutMapping("/update")
	public R<WaterInspection> updateSystemQualityWaterInspection(@RequestBody WaterInspection systemQualityWaterInspection) {
		return systemQualityWaterInspectionService.update(new DataFactory<WaterInspection>().nameFactory(systemQualityWaterInspection), new QueryWrapper<WaterInspection>().eq("id", systemQualityWaterInspection.getId())) ? R.success(Status.UPDATE_SUCCESS.getVal()) : R.fail(Status.UPDATE_ERROR.getVal());
	}


	@ApiOperationSupport(order = 4)
	@ApiOperation("删除")
	@PostMapping("/remove")
	public R<WaterInspection> deleteSystemQualityWaterInspection(@RequestBody WaterInspection systemQualityWaterInspection) {
		return systemQualityWaterInspectionService.remove(new QueryWrapper<WaterInspection>().eq("id", systemQualityWaterInspection.getId())) ? R.success(Status.UPDATE_SUCCESS.getVal()) : R.fail(Status.DELETE_ERROR.getVal());
	}
}
