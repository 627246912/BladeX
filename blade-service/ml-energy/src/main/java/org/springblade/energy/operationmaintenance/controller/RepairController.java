package org.springblade.energy.operationmaintenance.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.alarmmanagement.entity.EquipmentAlarm;
import org.springblade.energy.alarmmanagement.service.IEquipmentAlarmService;
import org.springblade.energy.operationmaintenance.entity.Notice;
import org.springblade.energy.operationmaintenance.entity.PlanCount;
import org.springblade.energy.operationmaintenance.entity.Repair;
import org.springblade.energy.operationmaintenance.service.NoticeService;
import org.springblade.energy.operationmaintenance.service.PlanCountService;
import org.springblade.energy.operationmaintenance.service.RepairService;
import org.springblade.energy.operationmaintenance.vo.*;
import org.springblade.energy.securitymanagement.util.DataFactory;
import org.springblade.energy.securitymanagement.util.PageQuery;
import org.springblade.energy.securitymanagement.util.PageUtils;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


/**
 * 控制器
 *
 * @author CYL
 * @since 2020-07-23
 */
@RestController
@AllArgsConstructor
@RequestMapping("/repair")
@Api(value = "运维管理--维修", tags = "运维管理--维修接口")
//PC端 & APP端
public class RepairController extends BladeController {

	RepairService repairService;

	IEquipmentAlarmService iEquipmentAlarmService;

	PlanCountService planCountService;

	NoticeService noticeService;


	@PostMapping("/save")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "派单")
	public R addRepair(@RequestBody Repair repair) {
		PlanCount planCount = new PlanCount();
		Notice notice = new Notice();
		this.processAutomaticStaffAssignment(repair, planCount, notice);
		return R.status(repairService.save(new DataFactory<>().historyFault(new DataFactory<Repair>().nameFactory(repair))) && planCountService.save(planCount) && noticeService.save(notice));
	}

	@GetMapping("/page")
	@ApiOperationSupport(order = 2)
	@ApiOperation("列表")
	public R<PageUtils> listRepair(PageQuery pageQuery, Repair repair) {
		Page<Repair> page = repairService.page(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), new QueryWrapper<>(repair).orderByDesc("create_time"));
		return R.data(new PageUtils(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
	}

	@PutMapping("/update")
	@ApiOperationSupport(order = 3)
	@ApiOperation("编辑")
	public R updateRepair(@RequestBody Repair repair) {
		if (repair.getRepairType() == 3 && repair.getCheckStatus() == 0) {
			Notice notice = new Notice();
			notice.setTaskId(repair.getId());
			notice.setSiteId(Long.parseLong(repair.getSiteId()));
			notice.setStationId(repair.getStationId());
			Long responsibleId = Long.parseLong(repair.getAssignedPersonId());
			notice.setResponsibleId(responsibleId);
			notice.setLeaderId(Long.parseLong(DataFactory.administratorId(responsibleId).split(",")[0]));
			notice.setNoticeType(5);
			notice.setTaskType(2);
			Integer type = repair.getType();
			notice.setRepairType(type);
			switch (type) {
				case 1:
					notice.setNoticeName("报修");
					break;
				case 2:
					notice.setNoticeName("告警");
					break;
			}
			notice.setNoticeTime(DataFactory.datetime);
			if (!noticeService.save(notice)) {
				throw new ServiceException("安全审核通知失败");
			}
		}
		return R.status(repairService.update(new DataFactory<Repair>().nameFactory(repair), new QueryWrapper<Repair>().eq("id", repair.getId())));
	}

	@PostMapping("/remove")
	@ApiOperationSupport(order = 4)
	@ApiOperation("删除")
	public R removeRepair(@RequestBody Repair repair) {
		return R.status(repairService.remove(new QueryWrapper<Repair>().eq("id", repair.getId())) && planCountService.remove(new QueryWrapper<PlanCount>().eq("plan_id", repair.getId())));
	}

	@GetMapping("/count")
	@ApiOperationSupport(order = 5)
	@ApiOperation("维修结果统计")
	public R<RepairCountVo> repairCount(PageQuery pageQuery, String stationId, String siteId, String time, Integer type) {
		return repairService.repairCount(stationId, siteId, time, type);
	}

	@GetMapping("/count/detail")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "维修详情统计")
	public R<PageUtils> repairSiteCount(PageQuery pageQuery, String stationId, String siteId, String time, Integer type) {
		R<RepairCountVo> repairCount = repairService.repairCount(stationId, siteId, time, type);
		List<RepairSiteCountVo> site = repairCount.getData().getSite();
		return R.data(new PageUtils(site, (long) site.size(), pageQuery.getSize(), pageQuery.getCurrent(), true));
	}

	public void processAutomaticStaffAssignment(Repair repair, PlanCount planCount, Notice notice) {
		String datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(repair.getDispatchTime());
		AtomicReference<String> processAutomaticStaffAssignment = new AtomicReference<>("");
		if (repair.getAssignedType() == 0) {
			processAutomaticStaffAssignment.set(new DataFactory<>().processAutomaticStaffAssignment(datetime, AuthUtil.getUserId(), repair.getStationId(), true));
		} else {
			processAutomaticStaffAssignment.set(new DataFactory<>().processAutomaticStaffAssignment(datetime, Long.parseLong(repair.getAssignedPersonId()), repair.getStationId(), false));
		}
		if (processAutomaticStaffAssignment.get().equals("")) {
			throw new ServiceException("无上班人员，无法分配");
		}
		String[] responsible = processAutomaticStaffAssignment.get().split(",");
		repair.setAssignedPersonId(responsible[0]);
		Long primaryKey = DataFactory.processPrimaryKey();
		repair.setId(primaryKey);
		Long siteId = Long.parseLong(repair.getSiteId());
		Long stationId = repair.getStationId();
		Long assignedPersonId = Long.parseLong(repair.getAssignedPersonId());
		planCount.setSiteId(siteId);
		planCount.setStationId(stationId);
		planCount.setPlanId(primaryKey);
		planCount.setTaskType(2);
		Date dispatchTime = repair.getDispatchTime();
		planCount.setTaskTime(dispatchTime);
		planCount.setTaskResponsible(assignedPersonId);
		notice.setTaskId(primaryKey);
		notice.setSiteId(siteId);
		notice.setStationId(stationId);
		notice.setResponsibleId(assignedPersonId);
		notice.setLeaderId(Long.parseLong(DataFactory.administratorId(assignedPersonId).split(",")[0]));
		notice.setNoticeType(2);
		notice.setNoticeTime(dispatchTime);
		notice.setTaskType(2);
		Integer type = repair.getType();
		notice.setRepairType(type);
		switch (type) {
			case 1:
				notice.setNoticeName("报修任务");
				break;
			case 2:
				notice.setNoticeName("告警任务");
				break;
		}
		if (Func.isEmpty(repair.getAssignedPersonId())) {
			throw new ServiceException("分配人员不能为空");
		}
		if (repair.getType() == 2) {
			EquipmentAlarm equipmentAlarm = new EquipmentAlarm();
			equipmentAlarm.setIsCreate(1);
			iEquipmentAlarmService.update(equipmentAlarm, new QueryWrapper<EquipmentAlarm>().eq("id", repair.getAlertId()));
		}
	}
}
