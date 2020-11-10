package org.springblade.energy.operationmaintenance.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.energy.operationmaintenance.entity.Notice;
import org.springblade.energy.operationmaintenance.service.NoticeService;
import org.springblade.energy.operationmaintenance.vo.TaskAllocationVo;
import org.springblade.energy.securitymanagement.util.DataFactory;
import org.springblade.energy.securitymanagement.util.PageQuery;
import org.springblade.energy.securitymanagement.util.PageUtils;
import org.springblade.system.user.entity.Shift;
import org.springblade.system.user.entity.UserShift;
import org.springblade.system.user.feign.IUserClient;
import org.springblade.system.user.vo.UserShiftVo;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
@Api(value = "APP--我的排班", tags = "APP--我的排班接口")
public class ShiftController {

	IUserClient userClient;

	NoticeService noticeService;

	@ApiOperationSupport(order = 1)
	@ApiOperation("换班申请添加")
	@PostMapping("/save")
	public R shift(@RequestBody Shift shift) {
		Long primaryKey = DataFactory.processPrimaryKey();
		shift.setId(primaryKey);
		Notice notice = new Notice();
		notice.setTaskId(primaryKey);
		notice.setSiteId(shift.getSiteId());
		notice.setStationId(shift.getStationId());
		Long applicantId = shift.getApplicant();
		notice.setResponsibleId(applicantId);
		notice.setLeaderId(Long.parseLong(DataFactory.administratorId(applicantId).split(",")[0]));
		notice.setNoticeType(3);
		notice.setChangeType(2);
		notice.setTaskType(8);
		notice.setNoticeName("换班");
		notice.setNoticeTime(DataFactory.datetime);
		if (!noticeService.save(notice)) {
			throw new ServiceException("变更换班通知失败");
		}
		return userClient.shiftSave(new DataFactory<Shift>().nameFactory(shift));
	}

	@ApiOperationSupport(order = 2)
	@ApiOperation("换班申请编辑")
	@PutMapping("/update")
	public R shiftUpdate(@RequestBody Shift shift) {
		if (shift.getShiftStatus() == 1) {
			String[] applicantDate = new SimpleDateFormat("yyyy-MM-dd").format(shift.getShiftDate()).split("-");
			String[] shiftDate = new SimpleDateFormat("yyyy-MM-dd").format(shift.getShiftedDate()).split("-");
			Long applicant = shift.getApplicant();
			Long shiftedPerson = shift.getShiftedPerson();
			AtomicLong applicantUserShiftId = new AtomicLong(0L);
			AtomicLong shiftedPersonUserShiftId = new AtomicLong(0L);
			Integer applicantYear = Integer.parseInt(applicantDate[0]);
			Integer shiftedYear = Integer.parseInt(shiftDate[0]);
			Integer applicantMonth = Integer.parseInt(applicantDate[1]);
			Integer shiftedMonth = Integer.parseInt(shiftDate[1]);
			final int applicantDay = Integer.parseInt(applicantDate[2]) - 1;
			final int shiftedDay = Integer.parseInt(shiftDate[2]) - 1;
			// << TODO 特殊情况 2020年12月31日 - 2021年1月1日
			AtomicReference<List<UserShiftVo>> applicantShiftVosBefore = new AtomicReference<>();
			AtomicReference<List<UserShiftVo>> shiftPersonShiftVosBefore = new AtomicReference<>();
			List<UserShift> userShiftList = userClient.userShiftList();
			userShiftList.stream().filter(userShift -> userShift.getUserId().equals(applicant) && userShift.getShiftYear().equals(applicantYear) && userShift.getShiftMonth().equals(applicantMonth)).forEach(userShift -> {
				applicantShiftVosBefore.set(JSONObject.parseArray(userShift.getShiftCycle(), UserShiftVo.class));
				applicantUserShiftId.set(userShift.getId());
			});
			userShiftList.stream().filter(userShift -> userShift.getUserId().equals(shiftedPerson) && userShift.getShiftYear().equals(shiftedYear) && userShift.getShiftMonth().equals(shiftedMonth)).forEach(userShift -> {
				shiftPersonShiftVosBefore.set(JSONObject.parseArray(userShift.getShiftCycle(), UserShiftVo.class));
				shiftedPersonUserShiftId.set(userShift.getId());
			});

			{
				applicantShiftVosBefore.get().stream().filter(userShiftVo -> userShiftVo.getDay().equals(applicantDay)).forEach(userShiftVo -> {
					userShiftList.stream().filter(userShift -> userShift.getUserId().equals(shiftedPerson) && userShift.getShiftYear().equals(applicantYear) && userShift.getShiftMonth().equals(applicantMonth)).forEach(userShift -> {
						AtomicReference<List<UserShiftVo>> shiftPersonShiftVosAfter = new AtomicReference<>();
						shiftPersonShiftVosAfter.set(JSONObject.parseArray(userShift.getShiftCycle(), UserShiftVo.class));
						shiftPersonShiftVosAfter.get().stream().filter(shiftUserShift -> shiftUserShift.getDay().equals(applicantDay)).forEach(shiftUserShift -> {
							shiftPersonShiftVosAfter.get().set(applicantDay, new UserShiftVo(shiftedPerson, userShiftVo.getName(), userShiftVo.getDay(), userShiftVo.getWeek(), userShiftVo.getShift(), userShiftVo.getResRange()));
							UserShift shiftPersonUserShiftBefore = new UserShift();
							shiftPersonUserShiftBefore.setId(userShift.getId());
							shiftPersonUserShiftBefore.setShiftCycle(JSONObject.toJSONString(shiftPersonShiftVosAfter.get()));
							userClient.exchangeShift(shiftPersonUserShiftBefore);
						});
					});
					applicantShiftVosBefore.get().set(applicantDay, new UserShiftVo(applicant, userShiftVo.getName(), userShiftVo.getDay(), userShiftVo.getWeek(), 0, 0));
					UserShift applicantUserShiftAfter = new UserShift();
					applicantUserShiftAfter.setId(applicantUserShiftId.get());
					applicantUserShiftAfter.setShiftCycle(JSONObject.toJSONString(applicantShiftVosBefore.get()));
					userClient.exchangeShift(applicantUserShiftAfter);
				});
			}

			{
				shiftPersonShiftVosBefore.get().stream().filter(userShiftVo -> userShiftVo.getDay().equals(shiftedDay)).forEach(userShiftVo -> {
					userShiftList.stream().filter(userShift -> userShift.getUserId().equals(applicant) && userShift.getShiftYear().equals(shiftedYear) && userShift.getShiftMonth().equals(shiftedMonth)).forEach(userShift -> {
						AtomicReference<List<UserShiftVo>> applicantShiftVosAfter = new AtomicReference<>();
						applicantShiftVosAfter.set(JSONObject.parseArray(userShift.getShiftCycle(), UserShiftVo.class));
						applicantShiftVosAfter.get().stream().filter(applicantUserShift -> applicantUserShift.getDay().equals(shiftedDay)).forEach(applicantUserShift -> {
							applicantShiftVosAfter.get().set(shiftedDay, new UserShiftVo(applicant, userShiftVo.getName(), userShiftVo.getDay(), userShiftVo.getWeek(), userShiftVo.getShift(), userShiftVo.getResRange()));
							UserShift applicantUserShiftBefore = new UserShift();
							applicantUserShiftBefore.setId(userShift.getId());
							applicantUserShiftBefore.setShiftCycle(JSONObject.toJSONString(applicantShiftVosAfter.get()));
							userClient.exchangeShift(applicantUserShiftBefore);
						});
					});
					shiftPersonShiftVosBefore.get().set(shiftedDay, new UserShiftVo(shiftedPerson, userShiftVo.getName(), userShiftVo.getDay(), userShiftVo.getWeek(), 0, 0));
					UserShift shiftPersonUserShiftAfter = new UserShift();
					shiftPersonUserShiftAfter.setId(shiftedPersonUserShiftId.get());
					shiftPersonUserShiftAfter.setShiftCycle(JSONObject.toJSONString(shiftPersonShiftVosBefore.get()));
					userClient.exchangeShift(shiftPersonUserShiftAfter);
				});
			}
		}

		return userClient.shiftUpdate(new DataFactory<Shift>().nameFactory(shift));
	}

	@ApiOperationSupport(order = 3)
	@ApiOperation("上班人员列表")
	@GetMapping("/go/to/work")
	public R<PageUtils> transferOrderPage(PageQuery pageQuery, String dateTime) {
		String[] date = dateTime.substring(0, 10).split("-");
		String[] time = dateTime.substring(11, 19).split(":");
		AtomicReference<Long> user = new AtomicReference<>(0L);
		String[] admin = DataFactory.administratorId(AuthUtil.getUserId()).split(",");
		user.set(Long.parseLong(admin[0]));
		List<TaskAllocationVo> taskAllocationVoList = new DataFactory<>().taskAllocation(user.get(), date[0], date[1], date[2], time[0], time[1], time[2], Long.parseLong(admin[1])).stream().filter(taskAllocationVo -> taskAllocationVo.getStatus() != 0 && taskAllocationVo.getStatus() != 4).collect(Collectors.toList());
		return R.data(new PageUtils(taskAllocationVoList, (long) taskAllocationVoList.size(), pageQuery.getSize(), pageQuery.getCurrent(), true));
	}
}
