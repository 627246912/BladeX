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
import org.springblade.energy.qualitymanagement.entity.EnergyInspection;
import org.springblade.energy.qualitymanagement.service.EnergyInspectionService;
import org.springframework.web.bind.annotation.*;


@AllArgsConstructor
@RestController
@Api(value = "质量体系--重点能耗设备巡检", tags = "质量体系--重点能耗设备巡检接口")
@RequestMapping("/system/energy")
public class EnergyInspectionController {


	EnergyInspectionService systemQualityEnergyInspectionService;

	@ApiOperationSupport(order = 1)
	@ApiOperation("添加")
	@PostMapping("/save")
	public R<EnergyInspection> addSystemQualityEnergyInspection(@RequestBody EnergyInspection systemQualityEnergyInspection) {
		return systemQualityEnergyInspectionService.save(new DataFactory<EnergyInspection>().nameFactory(systemQualityEnergyInspection)) ? R.success(Status.ADD_SUCCESS.getVal()) : R.fail(Status.ADD_ERROR.getVal());
	}


	@ApiOperationSupport(order = 2)
	@ApiOperation("列表 & 条件查询")
	@GetMapping("/page")
	public R<PageUtils> listSystemQualityEnergyInspection(PageQuery pageQuery, EnergyInspection systemQualityEnergyInspection) {
		Page<EnergyInspection> page = systemQualityEnergyInspectionService.page(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), new QueryWrapper<>(systemQualityEnergyInspection).orderByDesc("create_time"));
		return R.data(new PageUtils(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
	}


	@ApiOperationSupport(order = 3)
	@ApiOperation("修改")
	@PutMapping("/update")
	public R<EnergyInspection> updateSystemQualityEnergyInspection(@RequestBody EnergyInspection systemQualityEnergyInspection) {
		return systemQualityEnergyInspectionService.update(new DataFactory<EnergyInspection>().nameFactory(systemQualityEnergyInspection), new QueryWrapper<EnergyInspection>().eq("id", systemQualityEnergyInspection.getId())) ? R.success(Status.UPDATE_SUCCESS.getVal()) : R.fail(Status.UPDATE_ERROR.getVal());
	}


	@ApiOperationSupport(order = 4)
	@ApiOperation("删除")
	@PostMapping("/remove")
	public R<EnergyInspection> deleteSystemQualityEnergyInspection(@RequestBody EnergyInspection systemQualityEnergyInspection) {
		return systemQualityEnergyInspectionService.remove(new QueryWrapper<EnergyInspection>().eq("id", systemQualityEnergyInspection.getId())) ? R.success(Status.UPDATE_SUCCESS.getVal()) : R.fail(Status.DELETE_ERROR.getVal());
	}
}
