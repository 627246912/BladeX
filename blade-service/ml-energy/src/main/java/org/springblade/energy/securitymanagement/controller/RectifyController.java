package org.springblade.energy.securitymanagement.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springblade.energy.runningmanagement.station.service.ISiteService;
import org.springblade.energy.runningmanagement.station.service.IStationService;
import org.springblade.energy.securitymanagement.entity.Rectify;
import org.springblade.energy.securitymanagement.service.RectifyService;
import org.springblade.energy.securitymanagement.service.SafetyTaskService;
import org.springblade.energy.securitymanagement.util.PageQuery;
import org.springblade.energy.securitymanagement.util.PageUtils;
import org.springblade.system.user.feign.IUserClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/security/rectify")
@Api(value = "安全管理--整改计划", tags = "安全管理--整改计划接口")
public class RectifyController {

	RectifyService rectifyService;

	ISiteService siteService;

	IStationService stationService;

	IUserClient iUserClient;

	SafetyTaskService safetyInspectionRecordService;

	@ApiOperationSupport(order = 1)
	@ApiOperation("添加")
	@PostMapping("/save")
	public R<Rectify> addRectify(@RequestBody Rectify rectify) {
		return rectifyService.addRectify(rectify);
	}


	@ApiOperationSupport(order = 2)
	@ApiOperation("列表 & 条件查询")
	@GetMapping("/page")
	public R<PageUtils> listRectify(PageQuery pageQuery, Rectify rectify) {
		Page<Rectify> page = rectifyService.page(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), new QueryWrapper<>(rectify).orderByDesc("create_time"));
		return R.data(new PageUtils(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
	}


/*	@ApiOperationSupport(order = 3)
	@ApiOperation("编辑")
	@PutMapping("/update")
	public R<Rectify> updateRectify(@RequestBody Rectify rectify) {

		return rectifyService.update(rectify, new QueryWrapper<Rectify>().eq("id", rectify.getId())) ? R.success(Status.UPDATE_SUCCESS.getVal()) : R.fail(Status.UPDATE_ERROR.getVal());
	}*/


	@ApiOperationSupport(order = 4)
	@ApiOperation("批量删除")
	@PostMapping("/remove")
	public R<Boolean> removeRectify(@RequestBody List<Rectify> rectify) {
		return rectifyService.removeRectify(rectify);
	}
}
