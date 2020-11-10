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
import org.springblade.energy.qualitymanagement.entity.ElectricityInspection;
import org.springblade.energy.qualitymanagement.service.ElectricityInspectionService;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@Api(value = "质量体系--供电巡检", tags = "质量体系--供电巡检接口")
@RequestMapping("/system/electricity")
public class ElectricityInspectionController {

	ElectricityInspectionService systemQualityElectricityInspectionService;

	@ApiOperationSupport(order = 1)
	@ApiOperation("添加")
	@PostMapping("/save")
	public R<ElectricityInspection> addSystemQualityElectricityInspection(@RequestBody ElectricityInspection electricityInspection) {
		return systemQualityElectricityInspectionService.save(new DataFactory<ElectricityInspection>().nameFactory(electricityInspection)) ? R.success(Status.ADD_SUCCESS.getVal()) : R.fail(Status.ADD_ERROR.getVal());
	}


	@ApiOperationSupport(order = 2)
	@ApiOperation("列表 & 条件查询")
	@GetMapping("/page")
	public R<PageUtils> listSystemQualityElectricityInspection(PageQuery pageQuery, ElectricityInspection systemQualityElectricityInspection) {
		Page<ElectricityInspection> page = systemQualityElectricityInspectionService.page(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), new QueryWrapper<>(systemQualityElectricityInspection).orderByDesc("create_time"));
		return R.data(new PageUtils(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
	}


	@ApiOperationSupport(order = 3)
	@ApiOperation("修改")
	@PutMapping("/update")
	public R<ElectricityInspection> updateSystemQualityElectricityInspection(@RequestBody ElectricityInspection electricityInspection) {
		return systemQualityElectricityInspectionService.update(new DataFactory<ElectricityInspection>().nameFactory(electricityInspection), new QueryWrapper<ElectricityInspection>().eq("id", electricityInspection.getId())) ? R.success(Status.UPDATE_SUCCESS.getVal()) : R.fail(Status.UPDATE_ERROR.getVal());
	}


	@ApiOperationSupport(order = 4)
	@ApiOperation("删除")
	@PostMapping("/remove")
	public R<ElectricityInspection> deleteSystemQualityElectricityInspection(@RequestBody ElectricityInspection systemQualityElectricityInspection) {
		return systemQualityElectricityInspectionService.remove(new QueryWrapper<ElectricityInspection>().eq("id", systemQualityElectricityInspection.getId())) ? R.success(Status.UPDATE_SUCCESS.getVal()) : R.fail(Status.DELETE_ERROR.getVal());
	}
}
