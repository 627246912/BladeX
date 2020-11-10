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
import org.springblade.energy.poweroutagemanagement.entity.OffOperating;
import org.springblade.energy.poweroutagemanagement.service.OffOperatingService;
import org.springblade.energy.securitymanagement.enums.Status;
import org.springblade.energy.securitymanagement.util.DataFactory;
import org.springblade.energy.securitymanagement.util.PageQuery;
import org.springblade.energy.securitymanagement.util.PageUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/powerOutage/off")
@Api(value = "APP--倒闸操作票", tags = "APP--倒闸操作票接口")
//app
public class OffOperatingController {

	OffOperatingService offOperatingService;

	NoticeService noticeService;


	@ApiOperationSupport(order = 1)
	@ApiOperation("添加")
	@PostMapping("/save")
	public R addOffOperating(@RequestBody OffOperating offOperating) {
		Long primaryKey = DataFactory.processPrimaryKey();
		offOperating.setId(primaryKey);
		Notice notice = new Notice();
		notice.setTaskId(primaryKey);
		notice.setSiteId(offOperating.getSiteId());
		notice.setStationId(offOperating.getStationId());
		Long responsibleId = offOperating.getApplicationPerson();
		notice.setResponsibleId(responsibleId);
		notice.setLeaderId(Long.parseLong(DataFactory.administratorId(responsibleId).split(",")[0]));
		notice.setNoticeType(5);
		notice.setTaskType(5);
		notice.setNoticeName("倒闸票");
		notice.setNoticeTime(DataFactory.datetime);
		return R.status(offOperatingService.save(new DataFactory<OffOperating>().nameFactory(offOperating)) && noticeService.save(notice));
	}


	@ApiOperationSupport(order = 2)
	@ApiOperation("列表 & 条件查询")
	@GetMapping("/page")
	public R<PageUtils> listOffOperating(PageQuery pageQuery, OffOperating offOperating) {
		Page<OffOperating> page = offOperatingService.page(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), new QueryWrapper<>(offOperating).orderByDesc("create_time"));
		return R.data(new PageUtils(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
	}


	@ApiOperationSupport(order = 3)
	@ApiOperation("编辑")
	@PutMapping("/update")
	public R<OffOperating> updateOffOperating(@RequestBody OffOperating offOperating) {
		return offOperatingService.update(new DataFactory<OffOperating>().nameFactory(offOperating), new QueryWrapper<OffOperating>().eq("id", offOperating.getId())) ? R.success(Status.UPDATE_SUCCESS.getVal()) : R.fail(Status.UPDATE_ERROR.getVal());
	}


	@ApiOperationSupport(order = 4)
	@ApiOperation("删除")
	@PostMapping("/remove")
	public R<OffOperating> removeOffOperating(@RequestBody List<OffOperating> params) {
		if (params.size() <= 0)
			return R.fail(Status.DELETE_ERROR.getVal());

		params.stream().distinct().forEach((offOperating) ->
			offOperatingService.remove(new QueryWrapper<OffOperating>().eq("id", offOperating.getId())));
		return R.success(Status.DELETE_SUCCESS.getVal());
	}
}
