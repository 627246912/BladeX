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
import org.springblade.energy.qualitymanagement.entity.StandardManual;
import org.springblade.energy.qualitymanagement.service.StandardManualService;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
@Api(value = "质量体系--标准手册", tags = "质量体系--标准手册接口")
@RequestMapping("/system/manual")
public class StandardManualController {

	StandardManualService standardManualService;

	@ApiOperationSupport(order = 1)
	@ApiOperation("添加")
	@PostMapping("/save")
	public R<StandardManual> addStandardManual(@RequestBody StandardManual standardManual) {
		return standardManualService.save(new DataFactory<StandardManual>().nameFactory(standardManual)) ? R.success(Status.ADD_SUCCESS.getVal()) : R.fail(Status.ADD_ERROR.getVal());
	}

	@ApiOperationSupport(order = 2)
	@ApiOperation("列表 & 条件查询")
	@GetMapping("/page")
	public R<PageUtils> listStandardManual(PageQuery pageQuery, StandardManual standardManual) {
		Page<StandardManual> page = standardManualService.page(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), new QueryWrapper<>(standardManual).eq("manual_type", standardManual.getManualType()).orderByDesc("create_time"));
		return R.data(new PageUtils(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
	}

	@ApiOperationSupport(order = 3)
	@ApiOperation("编辑")
	@PutMapping("/update")
	public R<StandardManual> updateStandardManual(@RequestBody StandardManual standardManual) {
		return standardManualService.update(new DataFactory<StandardManual>().nameFactory(standardManual), new QueryWrapper<StandardManual>().eq("id", standardManual.getId())) ? R.success(Status.UPDATE_SUCCESS.getVal()) : R.fail(Status.UPDATE_ERROR.getVal());
	}

	@ApiOperationSupport(order = 4)
	@ApiOperation("删除")
	@PostMapping("/remove")
	public R<StandardManual> removeStandardManual(@RequestBody StandardManual standardManual) {
		return standardManualService.remove(new QueryWrapper<StandardManual>().eq("id", standardManual.getId())) ? R.success(Status.DELETE_SUCCESS.getVal()) : R.fail(Status.DELETE_ERROR.getVal());
	}
}
