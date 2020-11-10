package org.springblade.energy.operationmaintenance.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.energy.operationmaintenance.entity.Change;
import org.springblade.energy.operationmaintenance.entity.Notice;
import org.springblade.energy.operationmaintenance.entity.Repair;
import org.springblade.energy.operationmaintenance.service.ChangeService;
import org.springblade.energy.operationmaintenance.service.NoticeService;
import org.springblade.energy.operationmaintenance.service.RepairService;
import org.springblade.energy.securitymanagement.util.DataFactory;
import org.springblade.energy.securitymanagement.util.PageQuery;
import org.springblade.energy.securitymanagement.util.PageUtils;
import org.springblade.system.user.feign.IUserClient;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/repair")
@AllArgsConstructor
@Api(value = "APP--维修", tags = "APP--维修接口")
public class RepairApiController {

	RepairService repairService;

	ChangeService changeService;

	NoticeService noticeService;

	IUserClient iUserClient;

	@GetMapping("/history/fault")
	@ApiOperationSupport(order = 1)
	@ApiOperation("维修设备病例史")
	public R<PageUtils> historyFault(PageQuery pageQuery, String equipmentNo) {
		List<Repair> repairs = repairService.list();
		List<String> result = new ArrayList<>();
		repairs.stream().sorted(Comparator.comparing(Repair::getDispatchTime).reversed()).filter(repair -> repair.getEquipmentNo().equals(equipmentNo)).forEach(repair -> {
			String faultHistory = repair.getFaultHistory();
			result.add(faultHistory);
		});
		return R.data(new PageUtils(result, (long) result.size(), pageQuery.getSize(), pageQuery.getCurrent(), true));
	}

	@GetMapping("/page")
	@ApiOperationSupport(order = 2)
	@ApiOperation("任务列表")
	public R<PageUtils> repairPage(PageQuery pageQuery, Repair repair) {
		return repairService.customizePage(pageQuery, repair);
	}

	@PostMapping("/save")
	@ApiOperationSupport(order = 3)
	@ApiOperation("人工报修")
	public R addRepair(@RequestBody Repair repair) {
		Long primaryKey = DataFactory.processPrimaryKey();
		repair.setId(primaryKey);
		Notice notice = new Notice();
		notice.setTaskId(primaryKey);
		notice.setSiteId(Long.parseLong(repair.getSiteId()));
		notice.setStationId(repair.getStationId());
		Long responsibleId = Long.parseLong(repair.getReleaseRepairPersonId());
		notice.setResponsibleId(responsibleId);
		notice.setLeaderId(Long.parseLong(DataFactory.administratorId(responsibleId).split(",")[0]));
		notice.setNoticeType(4);
		notice.setTaskType(10);
		notice.setNoticeName("报修");
		notice.setNoticeTime(DataFactory.datetime);
		return R.status(repairService.save(new DataFactory<>().historyFault(new DataFactory<Repair>().nameFactory(repair))) && noticeService.save(notice));
	}

	@PostMapping("/transferOrder")
	@ApiOperationSupport(order = 4)
	@ApiOperation("转单")
	public R transferOrder(@RequestBody Change change) {
		Long primaryKey = DataFactory.processPrimaryKey();
		change.setId(primaryKey);
		Notice notice = new Notice();
		notice.setTaskId(primaryKey);
		notice.setSiteId(change.getSiteId());
		notice.setStationId(change.getStationId());
		Long applicantId = change.getExchangePersonId();
		notice.setResponsibleId(applicantId);
		notice.setLeaderId(Long.parseLong(DataFactory.administratorId(applicantId).split(",")[0]));
		notice.setNoticeType(3);
		notice.setChangeType(1);
		notice.setTaskType(9);
		notice.setNoticeName("转单");
		notice.setTransferOrderTaskType(2);
		notice.setNoticeTime(DataFactory.datetime);
		if (!noticeService.save(notice)) {
			throw new ServiceException("变更转单通知失败");
		}
		return R.status(changeService.save(new DataFactory<Change>().nameFactory(change)));
	}

	@GetMapping("/transferOrder/page")
	@ApiOperationSupport(order = 5)
	@ApiOperation("转单人员分配列表")
	public R<PageUtils> transferOrderPage(PageQuery pageQuery, String dateTime) {
		return repairService.transferOrderPage(pageQuery, dateTime, AuthUtil.getUserId());
	}

	@GetMapping("/transferOrder/list")
	@ApiOperationSupport(order = 6)
	@ApiOperation("转单列表")
	public R<PageUtils> transferOrderPage(PageQuery pageQuery, Change change) {
		Page<Change> page = changeService.page(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), new QueryWrapper<>(change).orderByDesc("create_time"));
		return R.data(new PageUtils(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
	}

	@PutMapping("/transferOrder/update")
	@ApiOperationSupport(order = 7)
	@ApiOperation("转单编辑")
	public R transferOrderUpdate(@RequestBody Change change) {
		if (change.getChangeStatus() == 1) {
			Repair repair = new Repair();
			repair.setAssignedPersonId(change.getReplyPersonId().toString());
			repair.setCreateDept(Long.parseLong(iUserClient.userInfoById(change.getReplyPersonId()).getData().getDeptId()));
			if (!repairService.update(repair, new QueryWrapper<Repair>().eq("id", change.getTaskId()))) {
				throw new ServiceException("转单失败");
			}
		}
		return R.status(changeService.update(change, new QueryWrapper<Change>().eq("id", change.getId())));
	}

	@GetMapping("/my/repair")
	@ApiOperationSupport(order = 8)
	@ApiOperation("我的报修")
	public R<PageUtils> myRepair(PageQuery pageQuery, Repair repair, String time) {
		Page<Repair> page = repairService.page(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), new QueryWrapper<>(repair).between("repair_time", time + " 00:00:00", time + " 23:59:59").orderByDesc("create_time"));
		return R.data(new PageUtils(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
	}
}
