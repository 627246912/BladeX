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
import org.springblade.energy.qualitymanagement.entity.WaterMaintenance;
import org.springblade.energy.qualitymanagement.service.WaterMaintenanceService;
import org.springframework.web.bind.annotation.*;


@AllArgsConstructor
@RestController
@RequestMapping("/system/maintenance/water")
@Api(value = "质量体系--供水保养标准", tags = "质量体系--供水保养标准接口")
public class WaterMaintenanceController {

	WaterMaintenanceService systemQualityWaterMaintenanceService;

	@ApiOperationSupport(order = 1)
	@ApiOperation("添加")
	@PostMapping("/save")
	public R<WaterMaintenance> addSystemQualityElectricityMaintenance(@RequestBody WaterMaintenance systemQualityWaterMaintenance) {
		return systemQualityWaterMaintenanceService.save(new DataFactory<WaterMaintenance>().nameFactory(systemQualityWaterMaintenance)) ? R.success(Status.ADD_SUCCESS.getVal()) : R.fail(Status.ADD_ERROR.getVal());
	}


	@ApiOperationSupport(order = 2)
	@ApiOperation("列表 & 条件查询")
	@GetMapping("/page")
	public R<PageUtils> listSystemQualityElectricityMaintenance(PageQuery pageQuery, WaterMaintenance systemQualityWaterMaintenance) {
		Page<WaterMaintenance> page = systemQualityWaterMaintenanceService.page(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), new QueryWrapper<>(systemQualityWaterMaintenance).orderByDesc("create_time"));
		return R.data(new PageUtils(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
	}


	@ApiOperationSupport(order = 3)
	@ApiOperation("编辑")
	@PutMapping("/update")
	public R<WaterMaintenance> updateSystemQualityElectricityMaintenance(@RequestBody WaterMaintenance systemQualityWaterMaintenance) {
		return systemQualityWaterMaintenanceService.update(new DataFactory<WaterMaintenance>().nameFactory(systemQualityWaterMaintenance), new QueryWrapper<WaterMaintenance>().eq("id", systemQualityWaterMaintenance.getId())) ? R.success(Status.UPDATE_SUCCESS.getVal()) : R.fail(Status.UPDATE_ERROR.getVal());
	}


	@ApiOperationSupport(order = 4)
	@ApiOperation("删除")
	@PostMapping("/remove")
	public R<WaterMaintenance> deleteSystemQualityElectricityMaintenance(@RequestBody WaterMaintenance systemQualityWaterMaintenance) {
		return systemQualityWaterMaintenanceService.remove(new QueryWrapper<WaterMaintenance>().eq("id", systemQualityWaterMaintenance.getId())) ? R.success(Status.DELETE_SUCCESS.getVal()) : R.fail(Status.DELETE_ERROR.getVal());
	}
}
