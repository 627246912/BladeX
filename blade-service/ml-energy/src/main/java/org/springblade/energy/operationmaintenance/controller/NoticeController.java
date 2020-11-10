package org.springblade.energy.operationmaintenance.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.alarmmanagement.entity.EquipmentAlarm;
import org.springblade.energy.alarmmanagement.service.IEquipmentAlarmService;
import org.springblade.energy.operationmaintenance.entity.Notice;
import org.springblade.energy.operationmaintenance.service.NoticeService;
import org.springblade.energy.securitymanagement.util.PageQuery;
import org.springblade.energy.securitymanagement.util.PageUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;


@RestController
@RequestMapping("/api/notice")
@AllArgsConstructor
@Api(value = "APP--消息通知", tags = "APP--消息接通知口")
public class NoticeController {

	NoticeService noticeService;

	IEquipmentAlarmService iEquipmentAlarmService;

	@ApiOperationSupport(order = 1)
	@ApiOperation("列表")
	@GetMapping("/list")
	public R<PageUtils> pageNotice(PageQuery pageQuery, Notice notice) {
		Page<Notice> page = noticeService.page(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), new QueryWrapper<>(notice).orderByDesc("notice_time"));
		return R.data(new PageUtils(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
	}

	@ApiOperationSupport(order = 2)
	@ApiOperation("编辑")
	@PutMapping("/update")
	public R updateNotice(@RequestBody Notice notice) {
		return R.status(noticeService.update(notice, new QueryWrapper<Notice>().eq("id", notice.getId())));
	}

	@ApiOperationSupport(order = 3)
	@ApiOperation("未浏览条数")
	@GetMapping("/notViewed")
	public R<Map<String, Object>> notViewedNumberNotice(Long responsibleId) {
		AtomicBoolean res = new AtomicBoolean(false);
		if (Func.isNotEmpty(responsibleId)) {
			res.set(true);
		}
		List<Notice> notices = noticeService.list(new QueryWrapper<Notice>().eq(res.get(), "responsible_id", responsibleId));
		Map<String, Object> notViewedNumber = new TreeMap<>();
		notViewedNumber.put("2", notices.stream().filter(notice -> notice.getNoticeType() == 2 && notice.getIsLookOver() == 0).count());
		notViewedNumber.put("3", notices.stream().filter(notice -> notice.getNoticeType() == 3 && notice.getIsLookOver() == 0).count());
		notViewedNumber.put("4", notices.stream().filter(notice -> notice.getNoticeType() == 4 && notice.getIsLookOver() == 0).count());
		notViewedNumber.put("5", notices.stream().filter(notice -> notice.getNoticeType() == 5 && notice.getIsLookOver() == 0).count());
		return R.data(notViewedNumber);
	}

	@ApiOperationSupport(order = 4)
	@ApiOperation("告警通知详情")
	@GetMapping("/detail/alarm")
	public R<EquipmentAlarm> detailAlarm(EquipmentAlarm equipmentAlarm) {
		return R.data(iEquipmentAlarmService.getOne(new QueryWrapper<EquipmentAlarm>().eq("id", equipmentAlarm.getId())));
	}

	@ApiOperationSupport(order = 5)
	@ApiOperation("告警通知编辑")
	@PutMapping("/update/alarm")
	public R updateAlarm(@RequestBody EquipmentAlarm equipmentAlarm) {
		return R.status(iEquipmentAlarmService.update(equipmentAlarm, new QueryWrapper<EquipmentAlarm>().eq("id", equipmentAlarm.getId())));
	}

	@ApiOperationSupport(order = 6)
	@ApiOperation("告警通知未浏览条数")
	@GetMapping("/notViewed/alarm")
	public R<String> notViewedNumberAlarm() {
		return R.data(Integer.toString(iEquipmentAlarmService.list(new QueryWrapper<EquipmentAlarm>().eq("is_scan", 0).eq("handle_status", 0)).size()));
	}

	@ApiOperationSupport(order = 7)
	@ApiOperation("告警通知列表")
	@GetMapping("/page/alarm")
	public R<PageUtils> pageAlarm(PageQuery pageQuery, EquipmentAlarm equipmentAlarm) {
		Page<EquipmentAlarm> page = iEquipmentAlarmService.page(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), new QueryWrapper<>(equipmentAlarm).select("id", "alarm_content", "level", "alarm_time").eq("is_deleted", 0).eq("handle_status", 0).orderByDesc("alarm_time"));
		return R.data(new PageUtils(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
	}
}
