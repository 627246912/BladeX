package org.springblade.energy.poweroutagemanagement.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.tool.api.R;
import org.springblade.energy.operationmaintenance.entity.Notice;
import org.springblade.energy.operationmaintenance.service.NoticeService;
import org.springblade.energy.poweroutagemanagement.entity.PowerOutage;
import org.springblade.energy.poweroutagemanagement.service.PowerOutageService;
import org.springblade.energy.securitymanagement.enums.Status;
import org.springblade.energy.securitymanagement.util.DataFactory;
import org.springblade.energy.securitymanagement.util.PageQuery;
import org.springblade.energy.securitymanagement.util.PageUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/power/outage")
@Api(value = "APP--停送电", tags = "APP--停送电接口")
//app
public class PowerOutageController {


	PowerOutageService powerOutageService;

	NoticeService noticeService;


	@ApiOperationSupport(order = 1)
	@ApiOperation("添加")
	@PostMapping("/save")
	public R addPowerOutage(@RequestBody PowerOutage powerOutage) {
		Long primaryKey = DataFactory.processPrimaryKey();
		powerOutage.setId(primaryKey);
		Notice notice = new Notice();
		notice.setTaskId(primaryKey);
		notice.setSiteId(powerOutage.getSiteId());
		notice.setStationId(powerOutage.getStationId());
		Long responsibleId = powerOutage.getApplicationPerson();
		notice.setResponsibleId(responsibleId);
		notice.setLeaderId(Long.parseLong(DataFactory.administratorId(responsibleId).split(",")[0]));
		notice.setNoticeType(5);
		notice.setTaskType(7);
		notice.setNoticeName("停送电");
		notice.setNoticeTime(DataFactory.datetime);
		return R.status(powerOutageService.save(new DataFactory<PowerOutage>().nameFactory(powerOutage)) && noticeService.save(notice));
	}


	@ApiOperationSupport(order = 2)
	@ApiOperation("列表 & 条件查询")
	@GetMapping("/page")
	public R<PageUtils> listPowerOutage(PageQuery pageQuery, PowerOutage powerOutage) {
		Page<PowerOutage> page = powerOutageService.page(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), new QueryWrapper<>(powerOutage).orderByDesc("create_time"));
		return R.data(new PageUtils(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
	}


	@ApiOperationSupport(order = 3)
	@ApiOperation("编辑")
	@PutMapping("/update")
	public R<PowerOutage> updatePowerOutage(@RequestBody PowerOutage powerOutage) {
		return powerOutageService.update(new DataFactory<PowerOutage>().nameFactory(powerOutage), new QueryWrapper<PowerOutage>().eq("id", powerOutage.getId())) ? R.success(Status.UPDATE_SUCCESS.getVal()) : R.fail(Status.UPDATE_ERROR.getVal());
	}


	@ApiOperationSupport(order = 4)
	@ApiOperation("批量删除")
	@PostMapping("/remove")
	public R<PowerOutage> removePowerOutage(@RequestBody List<PowerOutage> params) {
		if (params.size() <= 0)
			return R.fail(Status.DELETE_ERROR.getVal());

		params.stream().distinct().forEach((powerOutage) -> powerOutageService.remove(new QueryWrapper<PowerOutage>().eq("id", powerOutage.getId())));
		return R.success(Status.DELETE_SUCCESS.getVal());
	}
}
