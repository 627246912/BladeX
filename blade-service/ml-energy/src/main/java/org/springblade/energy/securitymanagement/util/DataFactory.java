package org.springblade.energy.securitymanagement.util;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.operationmaintenance.entity.*;
import org.springblade.energy.operationmaintenance.service.*;
import org.springblade.energy.operationmaintenance.vo.ApiTaskCountVo;
import org.springblade.energy.operationmaintenance.vo.TaskAllocationVo;
import org.springblade.energy.poweroutagemanagement.entity.Job;
import org.springblade.energy.poweroutagemanagement.entity.OffOperating;
import org.springblade.energy.poweroutagemanagement.entity.PowerOutage;
import org.springblade.energy.qualitymanagement.entity.*;
import org.springblade.energy.qualitymanagement.service.ElectricityMaintenanceService;
import org.springblade.energy.qualitymanagement.vo.DevNameVo;
import org.springblade.energy.runningmanagement.station.entity.Site;
import org.springblade.energy.runningmanagement.station.entity.Station;
import org.springblade.energy.runningmanagement.station.service.ISiteService;
import org.springblade.energy.runningmanagement.station.service.IStationService;
import org.springblade.energy.securitymanagement.entity.HiddenTroubleNotifyCard;
import org.springblade.energy.securitymanagement.entity.Patrol;
import org.springblade.energy.securitymanagement.entity.Rectify;
import org.springblade.energy.securitymanagement.entity.SafetyTask;
import org.springblade.energy.securitymanagement.service.PatrolService;
import org.springblade.energy.securitymanagement.service.SafetyTaskService;
import org.springblade.system.entity.Dept;
import org.springblade.system.entity.Role;
import org.springblade.system.feign.ISysClient;
import org.springblade.system.user.entity.Shift;
import org.springblade.system.user.entity.User;
import org.springblade.system.user.feign.IUserClient;
import org.springblade.system.user.vo.UserJoinUserShiftVo;
import org.springblade.system.user.vo.UserShiftVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Data
@Component
public class DataFactory<T> {

	static final Integer REST = 0;
	static final Integer IDLE = 1;
	static final Integer BUSY = 2;
	static final Integer GO_WORK = 3;
	static final Integer OFF_WORK = 4;
	static final String MORNING_SHIFT = "早班";
	static final String MIDDLE_SHIFT = "中班";
	static final String NIGHT_SHIFT = "晚班";
	static final String ALL_DAY = "全天";
	static final String RESTING = "休息";

	@Autowired
	ISiteService siteService;

	@Autowired
	IStationService stationService;

	@Autowired
	ElectricityMaintenanceService devService;

	@Autowired
	IUserClient iUserClient;

	@Autowired
	ISysClient iSysClient;

	@Autowired
	InspectionService inspectionService;

	@Autowired
	InspectionTaskService inspectionRecordService;

	@Autowired
	SafetyTaskService safetyInspectionRecordService;

	@Autowired
	MaintainService maintainService;

	@Autowired
	RepairService repairService;

	@Autowired
	PatrolService patrolService;

	@Autowired
	NewsPushService newsPushService;

	@Autowired
	PlanCountService planCountService;

	public static DataFactory serviceFactory;

	public static Date datetime = new Date();

	Long stationId;

	Long siteId;

	Long devId;

	Long responsibleId;

	Long checkPersonId;

	Long applicationPersonId;

	Long applicationPersonDeptId;

	Long releasePersonId;

	Long releaseRepairPersonId;

	Long exChangePersonId;

	Long replyPersonId;

	Long deptId;

	Long shiftedPersonId;

	String assistPersonId;

	String stationName;

	String siteName;

	String devName;

	String responsibleName;

	String checkPersonName;

	String applicationPersonName;

	String applicationPersonDeptName;

	String releasePersonName;

	String assistPersonName;

	String releaseRepairPersonPhone;

	String exChangePersonName;

	String replyPersonName;

	String deptName;

	String shiftedPersonName;

	String applicationPersonAvatar;

	String shiftedPersonAvatar;


	public DataFactory() {

	}

	@PostConstruct
	public void init() {
		serviceFactory = this;
	}

	public T nameFactory(T news) {

		if (news instanceof ElectricityInspection) {
			setStationId(((ElectricityInspection) news).getStationId());
			setSiteId(((ElectricityInspection) news).getSiteId());
			setDevId(((ElectricityInspection) news).getDevNumber());
			assignment(news);
			((ElectricityInspection) news).setSiteName(getSiteName());
			((ElectricityInspection) news).setStationName(getStationName());
			((ElectricityInspection) news).setDevName(getDevName());
			return news;
		}

		if (news instanceof ElectricityMaintenance) {
			setStationId(((ElectricityMaintenance) news).getStationId());
			setSiteId(((ElectricityMaintenance) news).getSiteId());
			setDevId(((ElectricityMaintenance) news).getDevName());
			assignment(news);
			((ElectricityMaintenance) news).setSiteName(getSiteName());
			((ElectricityMaintenance) news).setStationName(getStationName());
			((ElectricityMaintenance) news).setDevNameStr(getDevName());
			return news;
		}

		if (news instanceof EnergyInspection) {
			setStationId(((EnergyInspection) news).getStationId());
			setSiteId(((EnergyInspection) news).getSiteId());
			setDevId(((EnergyInspection) news).getRunDevice());
			assignment(news);
			((EnergyInspection) news).setSiteName(getSiteName());
			((EnergyInspection) news).setStationName(getStationName());
			((EnergyInspection) news).setDevName(getDevName());
			return news;
		}

		if (news instanceof EnergyMaintenance) {
			setStationId(((EnergyMaintenance) news).getStationId());
			setSiteId(((EnergyMaintenance) news).getSiteId());
			setDevId(((EnergyMaintenance) news).getDevName());
			assignment(news);
			((EnergyMaintenance) news).setSiteName(getSiteName());
			((EnergyMaintenance) news).setStationName(getStationName());
			((EnergyMaintenance) news).setDevNameStr(getDevName());
			return news;
		}

		if (news instanceof GasInspection) {
			setStationId(((GasInspection) news).getStationId());
			setSiteId(((GasInspection) news).getSiteId());
			assignment(news);
			((GasInspection) news).setSiteName(getSiteName());
			((GasInspection) news).setStationName(getStationName());
			return news;
		}

		if (news instanceof GasMaintenance) {
			setStationId(((GasMaintenance) news).getStationId());
			setSiteId(((GasMaintenance) news).getSiteId());
			assignment(news);
			((GasMaintenance) news).setSiteName(getSiteName());
			((GasMaintenance) news).setStationName(getStationName());
			return news;
		}

		if (news instanceof StandardManual) {
			setStationId(((StandardManual) news).getStationId());
			setSiteId(((StandardManual) news).getSiteId());
			setResponsibleId(((StandardManual) news).getUploadPersonId());
			assignment(news);
			((StandardManual) news).setSiteName(getSiteName());
			((StandardManual) news).setStationName(getStationName());
			((StandardManual) news).setUploadPersonName(getResponsibleName());
			return news;
		}

		if (news instanceof WaterInspection) {
			setStationId(((WaterInspection) news).getStationId());
			setSiteId(((WaterInspection) news).getSiteId());
			assignment(news);
			((WaterInspection) news).setSiteName(getSiteName());
			((WaterInspection) news).setStationName(getStationName());
			return news;
		}

		if (news instanceof WaterMaintenance) {
			setStationId(((WaterMaintenance) news).getStationId());
			setSiteId(((WaterMaintenance) news).getSiteId());
			assignment(news);
			((WaterMaintenance) news).setSiteName(getSiteName());
			((WaterMaintenance) news).setStationName(getStationName());
			return news;
		}

		if (news instanceof Job) {
			setStationId(((Job) news).getStationId());
			setSiteId(((Job) news).getSiteId());
			checkFactory(news);
			setCheckPersonId(((Job) news).getCheckPerson());
			setApplicationPersonId(((Job) news).getApplicationPerson());
			assignment(news);
			((Job) news).setSiteName(getSiteName());
			((Job) news).setStationName(getStationName());
			((Job) news).setCheckPersonName(getCheckPersonName());
			((Job) news).setApplicationPersonName(getApplicationPersonName());
			return news;
		}

		if (news instanceof OffOperating) {
			setStationId(((OffOperating) news).getStationId());
			setSiteId(((OffOperating) news).getSiteId());
			checkFactory(news);
			setCheckPersonId(((OffOperating) news).getCheckPerson());
			setApplicationPersonId(((OffOperating) news).getApplicationPerson());
			assignment(news);
			((OffOperating) news).setSiteName(getSiteName());
			((OffOperating) news).setStationName(getStationName());
			((OffOperating) news).setCheckPersonName(getCheckPersonName());
			((OffOperating) news).setApplicationPersonName(getApplicationPersonName());
			return news;
		}

		if (news instanceof PowerOutage) {
			setStationId(((PowerOutage) news).getStationId());
			setSiteId(((PowerOutage) news).getSiteId());
			checkFactory(news);
			setCheckPersonId(((PowerOutage) news).getCheckPerson());
			setApplicationPersonId(((PowerOutage) news).getApplicationPerson());
			if (Func.isNotEmpty(((PowerOutage) news).getApplicationDept()))
				setApplicationPersonDeptId(Long.parseLong(((PowerOutage) news).getApplicationDept()));
			assignment(news);
			((PowerOutage) news).setSiteName(getSiteName());
			((PowerOutage) news).setStationName(getStationName());
			((PowerOutage) news).setCheckPersonName(getCheckPersonName());
			((PowerOutage) news).setApplicationPersonName(getApplicationPersonName());
			((PowerOutage) news).setApplicationDeptName(getApplicationPersonDeptName());
			return news;
		}

		if (news instanceof HiddenTroubleNotifyCard) {
			setCheckPersonId(((HiddenTroubleNotifyCard) news).getCheckPerson());
			setApplicationPersonId(((HiddenTroubleNotifyCard) news).getApplicationPerson());
			assignment(news);
			((HiddenTroubleNotifyCard) news).setCheckPersonName(getCheckPersonName());
			((HiddenTroubleNotifyCard) news).setApplicationPersonName(getApplicationPersonName());
			return news;
		}

		if (news instanceof Patrol) {
			setStationId(((Patrol) news).getStationId());
			setSiteId(((Patrol) news).getSiteId());
			setResponsibleId(((Patrol) news).getResponsible());
			assignment(news);
			((Patrol) news).setSiteName(getSiteName());
			((Patrol) news).setStationName(getStationName());
			((Patrol) news).setResponsibleName(getResponsibleName());
			return news;
		}

		if (news instanceof Rectify) {
			setStationId(((Rectify) news).getStationId());
			setSiteId(((Rectify) news).getSiteId());
			setResponsibleId(((Rectify) news).getResponsiblePersonId());
			assignment(news);
			((Rectify) news).setSiteName(getSiteName());
			((Rectify) news).setStationName(getStationName());
			((Rectify) news).setResponsiblePersonName(getResponsibleName());
			return news;
		}

		if (news instanceof Inspection) {
			setStationId(((Inspection) news).getStationId());
			setSiteId(((Inspection) news).getSiteId());
			if (Func.isNotEmpty(((Inspection) news).getAssignedPersonId()))
				setResponsibleId(Long.parseLong(((Inspection) news).getAssignedPersonId()));
			if (!((Inspection) news).getEquipmentNo().equals(""))
				setDevId(Long.parseLong(((Inspection) news).getEquipmentNo()));
			assignment(news);
			((Inspection) news).setSiteName(getSiteName());
			((Inspection) news).setStationName(getStationName());
			((Inspection) news).setAssignedPersonName(getResponsibleName());
			((Inspection) news).setEquipmentName(getDevName());
			return news;
		}

		if (news instanceof Maintain) {
			setStationId(((Maintain) news).getStationId());
			setSiteId(((Maintain) news).getSiteId());
			setResponsibleId(Long.parseLong(((Maintain) news).getAssignedPersonId()));
			if (!((Maintain) news).getEquipmentNo().equals(""))
				setDevId(Long.parseLong(((Maintain) news).getEquipmentNo()));
			assignment(news);
			((Maintain) news).setSiteName(getSiteName());
			((Maintain) news).setStationName(getStationName());
			((Maintain) news).setAssignedPersonName(getResponsibleName());
			((Maintain) news).setEquipmentName(getDevName());
			return news;
		}

		if (news instanceof Repair) {
			setStationId(((Repair) news).getStationId());
			if (Func.isNotEmpty(((Repair) news).getSiteId()))
				setSiteId(Long.parseLong(((Repair) news).getSiteId()));
			checkFactory(news);
			if (Func.isNotEmpty(((Repair) news).getAssignedPersonId()))
				setResponsibleId(Long.parseLong(((Repair) news).getAssignedPersonId()));
			if (!((Repair) news).getEquipmentNo().equals(""))
				setDevId(Long.parseLong(((Repair) news).getEquipmentNo()));
			if (Func.isNotEmpty(((Repair) news).getReleaseRepairPersonId()))
				setReleasePersonId(Long.parseLong(((Repair) news).getReleaseRepairPersonId()));
			setCheckPersonId(((Repair) news).getCheckPerson());
			setAssistPersonId(((Repair) news).getAssistPersonId());
			if (Func.isNotEmpty(((Repair) news).getReleaseRepairPersonId()))
				setReleaseRepairPersonId(Long.parseLong(((Repair) news).getReleaseRepairPersonId()));
			assignment(news);
			((Repair) news).setSiteName(getSiteName());
			((Repair) news).setStationName(getStationName());
			((Repair) news).setAssignedPersonName(getResponsibleName());
			((Repair) news).setEquipmentName(getDevName());
			((Repair) news).setReleaseRepairPersonName(getReleasePersonName());
			((Repair) news).setCheckPersonName(getCheckPersonName());
			((Repair) news).setAssistPersonName(getAssistPersonName());
			((Repair) news).setReleaseRepairPersonPhone(getReleaseRepairPersonPhone());
			return news;
		}

		if (news instanceof SafetyTask) {
			checkFactory(news);
			setCheckPersonId(((SafetyTask) news).getCheckPerson());
			setApplicationPersonId(((SafetyTask) news).getApplicationPerson());
			assignment(news);
			((SafetyTask) news).setCheckPersonName(getCheckPersonName());
			((SafetyTask) news).setApplicationPersonName(getApplicationPersonName());
			return news;
		}

		if (news instanceof Change) {
			setExChangePersonId(((Change) news).getExchangePersonId());
			setReplyPersonId(((Change) news).getReplyPersonId());
			assignment(news);
			((Change) news).setExchangePersonName(getExChangePersonName());
			((Change) news).setReplyPersonName(getReplyPersonName());
			((Change) news).setDeptId(getDeptId());
			((Change) news).setDeptName(getDeptName());
			return news;
		}

		if (news instanceof Notice) {
			setResponsibleId(((Notice) news).getResponsibleId());
			setSiteId(((Notice) news).getSiteId());
			assignment(news);
			((Notice) news).setResponsibleName(getResponsibleName());
			((Notice) news).setSiteName(getSiteName());
			return news;
		}

		if (news instanceof Shift) {
			setApplicationPersonId(((Shift) news).getApplicant());
			setShiftedPersonId(((Shift) news).getShiftedPerson());
			setDeptId(((Shift) news).getApplicantDeptId());
			assignment(news);
			((Shift) news).setApplicantName(getApplicationPersonName());
			((Shift) news).setShiftedPersonName(getShiftedPersonName());
			((Shift) news).setApplicantDeptName(getDeptName());
			((Shift) news).setApplicantAvatar(getApplicationPersonAvatar());
			((Shift) news).setShiftedPersonAvatar(getShiftedPersonAvatar());
			return news;
		}

		return null;
	}


	public void assignment(T news) {
		AtomicReference<List<User>> users = new AtomicReference<>();
		if (Func.isNotEmpty(getResponsibleId()) ||
			Func.isNotEmpty(getCheckPersonId()) ||
			Func.isNotEmpty(getApplicationPersonId()) ||
			Func.isNotEmpty(getReleasePersonId()) ||
			Func.isNotEmpty(getAssistPersonId()) ||
			Func.isNotEmpty(getReleaseRepairPersonId()) ||
			Func.isNotEmpty(getExChangePersonId()) ||
			Func.isNotEmpty(getReplyPersonId())) {
			users.set(serviceFactory.iUserClient.userList());
		}
		if (Func.isNotEmpty(getSiteId())) {
			Site site = serviceFactory.siteService.getOne(new QueryWrapper<Site>().eq("id", getSiteId()));
			if (Func.isNotEmpty(site))
				setSiteName(site.getSiteName());
		}
		if (Func.isNotEmpty(getStationId())) {
			Station station = serviceFactory.stationService.getOne(new QueryWrapper<Station>().eq("id", getStationId()));
			if (Func.isNotEmpty(station))
				setStationName(station.getName());
		}
		if (Func.isNotEmpty(getDevId())) {
			DevNameVo devName = serviceFactory.devService.selectDevName(getDevId());
			if (Func.isNotEmpty(devName))
				setDevName(devName.getName());
		}
		if (Func.isNotEmpty(getResponsibleId())) {
			users.get().stream().filter(user -> user.getId().equals(getResponsibleId())).forEach(user -> setResponsibleName(user.getRealName()));
		}
		if (Func.isNotEmpty(getCheckPersonId())) {
			users.get().stream().filter(user -> user.getId().equals(getCheckPersonId())).forEach(user -> setCheckPersonName(user.getRealName()));
		}
		if (Func.isNotEmpty(getDeptId())) {
			Dept dept = serviceFactory.iSysClient.getDept(getDeptId()).getData();
			if (Func.isNotEmpty(dept))
				setDeptName(dept.getDeptName());
		}
		if (Func.isNotEmpty(getApplicationPersonId())) {
			users.get().stream().filter(user -> user.getId().equals(getApplicationPersonId())).forEach(user -> {
				setApplicationPersonName(user.getRealName());
				setApplicationPersonAvatar(user.getAvatar());
			});
		}
		if (Func.isNotEmpty(getShiftedPersonId())) {
			users.get().stream().filter(user -> user.getId().equals(getShiftedPersonId())).forEach(user -> {
				setShiftedPersonName(user.getRealName());
				setShiftedPersonAvatar(user.getAvatar());
			});
		}
		if (Func.isNotEmpty(getApplicationPersonDeptId())) {
			Dept dept = serviceFactory.iSysClient.getDept(getApplicationPersonDeptId()).getData();
			if (Func.isNotEmpty(dept))
				setApplicationPersonDeptName(dept.getDeptName());
		}
		if (Func.isNotEmpty(getReleasePersonId())) {
			users.get().stream().filter(user -> user.getId().equals(getReleasePersonId())).forEach(user -> setReleasePersonName(user.getRealName()));
		}
		if (Func.isNotEmpty(getAssistPersonId())) {
			String assistPersonId = getAssistPersonId();
			StringBuilder assistPersonName = new StringBuilder();
			AtomicInteger count = new AtomicInteger(1);
			Arrays.stream(assistPersonId.split(",")).forEach(id -> {
				users.get().stream().filter(user -> user.getId().equals(Long.parseLong(id))).forEach(user -> {
					assistPersonName.append((user.getRealName()));
					if (count.get() != assistPersonId.split(",").length) {
						assistPersonName.append(",");
					}
				});
				count.getAndIncrement();
			});
			setAssistPersonName(assistPersonName.toString());
		}
		if (Func.isNotEmpty(getReleaseRepairPersonId())) {
			users.get().stream().filter(user -> user.getId().equals(getReleaseRepairPersonId())).forEach(user -> setReleaseRepairPersonPhone(user.getPhone()));
		}

		if (news instanceof Change) {
			if (Func.isNotEmpty(getExChangePersonId())) {
				users.get().stream().filter(user -> user.getId().equals(getExChangePersonId())).forEach(user -> {
					setExChangePersonName(user.getRealName());
					setDeptId(Long.parseLong(user.getDeptId()));
					Dept dept = serviceFactory.iSysClient.getDept(getDeptId()).getData();
					if (Func.isNotEmpty(dept))
						setDeptName(dept.getDeptName());
				});
			}
			if (Func.isNotEmpty(getReplyPersonId())) {
				users.get().stream().filter(user -> user.getId().equals(getReplyPersonId())).forEach(user -> setReplyPersonName(user.getRealName()));
			}
		}
	}

	public List<TaskAllocationVo> taskAllocation(Long uid, String year, String month, String day, String hours, String minute, String second, Long stationId) {
		AtomicReference<List<UserJoinUserShiftVo>> taskAllocation = new AtomicReference<>();
		taskAllocation.set(serviceFactory.iUserClient.getUserShift(uid, Integer.parseInt(year), Integer.parseInt(month)));
		String time = year + "-" + month + "-" + day + " " + hours + ":" + minute + ":" + second;
		String date = year + "-" + month + "-" + day + " ";
		if (Func.isEmpty(taskAllocation) || taskAllocation.get().size() == 0) {
			taskAllocation.set(serviceFactory.iUserClient.userPage(year + "-" + month, stationId, uid));
		}
		try {
			List<PlanCount> planCounts = serviceFactory.planCountService.list();
			final long centerTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time).getTime();
			Date startTime = new Date(centerTime - 3600000);
			Date endsTime = new Date(centerTime + 3600000);
			List<PlanCount> planCountList = planCounts.stream().filter(planCount -> planCount.getTaskTime().after(startTime) && planCount.getTaskTime().before(endsTime)).collect(Collectors.toList());
			List<User> users = serviceFactory.iUserClient.userList();
			List<TaskAllocationVo> taskAllocationVos = new CopyOnWriteArrayList<>();
			taskAllocation.get().forEach(userJoinUserShiftVo -> {
				List<UserShiftVo> userShiftVos = JSONObject.parseArray(userJoinUserShiftVo.getShiftCycle(), UserShiftVo.class);
				userShiftVos.stream().filter(userShiftVo -> userShiftVo.getDay().equals(Integer.parseInt(day) - 1)).forEach(userShiftVo -> {
					TaskAllocationVo taskAllocationVo = new TaskAllocationVo();
					users.stream().filter(user -> user.getId().equals(userJoinUserShiftVo.getId())).forEach(user -> {
						Long id = user.getId();
						taskAllocationVo.setUserId(Long.toString(id));
						taskAllocationVo.setUserName(user.getRealName());
						taskAllocationVo.setUserAvatar(user.getAvatar());
						taskAllocationVo.setLevel(user.getLevel());
						final int timeComparison = Integer.parseInt(hours + minute);
						Integer voShift = userShiftVo.getShift();
						switch (voShift) {
							case 0:
								taskAllocationVo.setStatus(REST);
								taskAllocationVo.setShift(RESTING);
								taskAllocationVo.setTaskCount(0L);
								taskAllocationVo.setShiftNumber(voShift);
								break;
							case 1:
								if (timeComparison > 830 && timeComparison < 1600) {
									workStatus(planCountList, id, taskAllocationVo);
								}
								if (timeComparison >= 1600) {
									taskAllocationVo.setStatus(OFF_WORK);
								}
								if (timeComparison <= 830) {
									taskAllocationVo.setStatus(GO_WORK);
								}
								taskAllocationVo.setShift(MORNING_SHIFT);
								taskAllocationVo.setShiftNumber(voShift);
								break;
							case 2:
								if (timeComparison > 1600 && timeComparison < 2345) {
									workStatus(planCountList, id, taskAllocationVo);
								}
								if (timeComparison >= 2345) {
									taskAllocationVo.setStatus(OFF_WORK);
								}
								if (timeComparison <= 1600) {
									taskAllocationVo.setStatus(GO_WORK);
								}
								taskAllocationVo.setShift(MIDDLE_SHIFT);
								taskAllocationVo.setShiftNumber(voShift);
								break;
							case 3:
								if (timeComparison > 0 && timeComparison < 800) {
									workStatus(planCountList, id, taskAllocationVo);
								}
								if (timeComparison >= 800) {
									taskAllocationVo.setStatus(GO_WORK);
								}
								taskAllocationVo.setShift(NIGHT_SHIFT);
								taskAllocationVo.setShiftNumber(voShift);
								break;
							case 4:
								if (timeComparison > 830 && timeComparison < 1800) {
									workStatus(planCountList, id, taskAllocationVo);
								}
								if (timeComparison >= 1800) {
									taskAllocationVo.setStatus(OFF_WORK);
								}
								if (timeComparison <= 830) {
									taskAllocationVo.setStatus(GO_WORK);
								}
								taskAllocationVo.setShift(ALL_DAY);
								taskAllocationVo.setShiftNumber(voShift);
						}
						AtomicReference<String> shiftStartTime = new AtomicReference<>("");
						AtomicReference<String> shiftEndsTime = new AtomicReference<>("");
						try {
							String shift = taskAllocationVo.getShift();
							switch (shift) {
								case RESTING:
									shiftStartTime.set("24:00:00");
									shiftEndsTime.set("24:00:00");
									break;
								case MIDDLE_SHIFT:
									shiftStartTime.set("16:00:00");
									shiftEndsTime.set("23:45:00");
									break;
								case NIGHT_SHIFT:
									shiftStartTime.set("00:00:00");
									shiftEndsTime.set("08:00:00");
									break;
								default:
									shiftStartTime.set("08:30:00");
									if (shift.equals(MORNING_SHIFT)) {
										shiftEndsTime.set("16:00:00");
									}
									if (shift.equals(ALL_DAY)) {
										shiftEndsTime.set("18:00:00");
									}
							}
							Date shiftStartDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date + shiftStartTime.get());
							Date shiftEndsDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date + shiftEndsTime.get());

							taskAllocationVo.setWorkStartTime(shiftStartDate);
							taskAllocationVo.setWorkEndsTime(shiftEndsDate);
							taskAllocationVo.setWorkStartTimestamp(shiftStartDate.getTime());
							taskAllocationVo.setWorkEndsTimestamp(shiftEndsDate.getTime());
							if (taskAllocationVo.getStatus() == 1 || taskAllocationVo.getStatus() == 2) {
								String userId = taskAllocationVo.getUserId();
								taskAllocationVo.setTaskCount(
									planCountList.stream().filter(planCount -> planCount.getTaskResponsible().equals(Long.parseLong(userId)) && planCount.getTaskTime().after(shiftStartDate) && planCount.getTaskTime().before(shiftEndsDate)).count());
							} else {
								taskAllocationVo.setTaskCount(0L);
							}
						} catch (Exception e) {
							throw new ServiceException(e.getMessage());
						}
					});
					taskAllocationVos.add(taskAllocationVo);
				});
			});
			return taskAllocationVos;
		} catch (ParseException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	public void workStatus(List<PlanCount> planCounts, Long uid, TaskAllocationVo taskAllocationVo) {
		final long plan = planCounts.stream().filter(planCount -> planCount.getTaskResponsible().equals(uid)).count();
		workStatus(taskAllocationVo, plan);
		CopyOnWriteArrayList<String> taskNames = new CopyOnWriteArrayList<>();
		final long inspection = planCounts.stream().filter(planCount -> planCount.getTaskType() == 0 && planCount.getTaskResponsible().equals(uid)).count();
		final long maintain = planCounts.stream().filter(planCount -> planCount.getTaskType() == 1 && planCount.getTaskResponsible().equals(uid)).count();
		final long repair = planCounts.stream().filter(planCount -> planCount.getTaskType() == 2 && planCount.getTaskResponsible().equals(uid)).count();
		final long safetyInspection = planCounts.stream().filter(planCount -> planCount.getTaskType() == 3 && planCount.getTaskResponsible().equals(uid)).count();
		if (inspection >= 1) {
			taskNames.add("巡检");
		}
		if (maintain >= 1) {
			taskNames.add("保养");
		}
		if (repair >= 1) {
			taskNames.add("报修");
		}
		if (safetyInspection >= 1) {
			taskNames.add("安全检查");
		}
		if (taskNames.size() > 0) {
			StringBuilder taskName = new StringBuilder();
			taskName.append("正在执行");
			AtomicLong count = new AtomicLong(1);
			taskNames.stream().distinct().forEach(name -> {
				if (count.get() != taskNames.stream().distinct().count()) {
					taskName.append(name).append(",");
				} else {
					taskName.append(name).append("任务");
				}
				count.getAndIncrement();
			});
			taskAllocationVo.setUserTaskName(taskName.toString());
		}
	}

	public void workStatus(TaskAllocationVo taskAllocationVo, Long status) {
		if (status == 0) {
			taskAllocationVo.setStatus(1);
		}
		if (status > 0) {
			taskAllocationVo.setStatus(2);
		}
	}

	public void checkFactory(T news) {
		if (news instanceof Job) {
			Long userId = AuthUtil.getUserId();
			String userRole = AuthUtil.getUserRole();
			if (userRole.trim().equals("admin") || userRole.trim().equals("administrator")) {
				((Job) news).setApplicationPerson(userId);
				((Job) news).setCheckPerson(userId);
			} else {
				((Job) news).setApplicationPerson(userId);
				if (Func.isNotEmpty(serviceFactory.iUserClient.userInfoById(userId).getData()))
					((Job) news).setCheckPerson(serviceFactory.iUserClient.userInfoById(userId).getData().getCreateUser());
			}
			((Job) news).setApplicationTime(datetime);
		}

		if (news instanceof OffOperating) {
			Long userId = AuthUtil.getUserId();
			String userRole = AuthUtil.getUserRole();
			if (userRole.trim().equals("admin") || userRole.trim().equals("administrator")) {
				((OffOperating) news).setApplicationPerson(userId);
				((OffOperating) news).setCheckPerson(userId);
			} else {
				((OffOperating) news).setApplicationPerson(userId);
				if (Func.isNotEmpty(serviceFactory.iUserClient.userInfoById(userId).getData()))
					((OffOperating) news).setCheckPerson(serviceFactory.iUserClient.userInfoById(userId).getData().getCreateUser());
			}
			((OffOperating) news).setApplicationTime(datetime);
		}

		if (news instanceof PowerOutage) {
			Long userId = AuthUtil.getUserId();
			String userRole = AuthUtil.getUserRole();
			if (userRole.trim().equals("admin") || userRole.trim().equals("administrator")) {
				((PowerOutage) news).setApplicationPerson(userId);
				((PowerOutage) news).setCheckPerson(userId);
			} else {
				((PowerOutage) news).setApplicationPerson(userId);
				if (Func.isNotEmpty(serviceFactory.iUserClient.userInfoById(userId).getData()))
					((PowerOutage) news).setCheckPerson(serviceFactory.iUserClient.userInfoById(userId).getData().getCreateUser());
			}
			((PowerOutage) news).setApplicationTime(datetime);
		}

		if (news instanceof SafetyTask) {
			Long userId = AuthUtil.getUserId();
			String userRole = AuthUtil.getUserRole();
			if (userRole.trim().equals("admin") || userRole.trim().equals("administrator")) {
				((SafetyTask) news).setApplicationPerson(userId);
				((SafetyTask) news).setCheckPerson(userId);
			} else {
				((SafetyTask) news).setApplicationPerson(userId);
				if (Func.isNotEmpty(serviceFactory.iUserClient.userInfoById(userId).getData()))
					((SafetyTask) news).setCheckPerson(serviceFactory.iUserClient.userInfoById(userId).getData().getCreateUser());
			}
			((SafetyTask) news).setApplicationTime(datetime);
		}

		if (news instanceof Repair) {
			Long userId = AuthUtil.getUserId();
			String userRole = AuthUtil.getUserRole();
			if (userRole.trim().equals("admin") || userRole.trim().equals("administrator")) {
				((Repair) news).setCheckPerson(userId);
			} else {
				if (Func.isNotEmpty(serviceFactory.iUserClient.userInfoById(userId).getData()))
					((Repair) news).setCheckPerson(serviceFactory.iUserClient.userInfoById(userId).getData().getCreateUser());
			}
			((Repair) news).setApplicationTime(datetime);
		}
	}

	public Repair historyFault(Repair repair) {
		Map<String, String> repairHistoryFault = new HashMap<>();
		if (Func.isNotEmpty(repair.getEquipmentName())) {
			repairHistoryFault.put("dev", repair.getEquipmentName());
		} else {
			repairHistoryFault.put("dev", "");
		}
		if (Func.isNotEmpty(repair.getDispatchTime())) {
			repairHistoryFault.put("time", new SimpleDateFormat("yyyy-MM-dd HH:ss:mm").format(repair.getDispatchTime()));
		} else {
			repairHistoryFault.put("time", "");
		}
		if (Func.isNotEmpty(repair.getRepairContent())) {
			repairHistoryFault.put("detail", repair.getRepairContent());
		} else {
			repairHistoryFault.put("detail", "");
		}
		if (Func.isNotEmpty(repair.getAssignedPersonName())) {
			repairHistoryFault.put("name", repair.getAssignedPersonName());
		} else {
			repairHistoryFault.put("name", "");
		}
		repair.setFaultHistory(JSONObject.toJSONString(repairHistoryFault));
		return repair;
	}


	public static String administratorId(Long userId) {
		User user = serviceFactory.iUserClient.userInfoById(userId).getData();
		Role role = serviceFactory.iSysClient.getRole(Long.parseLong(user.getRoleId())).getData();
		if (Func.isEmpty(user) || Func.isEmpty(role)) {
			throw new ServiceException("无效的用户id");
		}

		String roleAlias = role.getRoleAlias();
		AtomicReference<Long> adminId = new AtomicReference<>(0L);
		if (roleAlias.trim().equals("admin") || roleAlias.trim().equals("administrator")) {
			adminId.set(userId);
		} else {
			adminId.set(user.getCreateUser());
		}
		return adminId.get() + "," + user.getSid();
	}

	public static Boolean isAdministrator(Long userId) {
		User user = serviceFactory.iUserClient.userInfoById(userId).getData();
		if (Func.isEmpty(user)) {
			throw new ServiceException("用户id无效");
		}
		Role role = serviceFactory.iSysClient.getRole(Long.parseLong(user.getRoleId())).getData();
		if (Func.isEmpty(role)) {
			throw new ServiceException("用户id无效");
		}
		String roleAlias = role.getRoleAlias();
		return roleAlias.trim().equals("admin") || roleAlias.trim().equals("administrator");
	}

	public String automaticStaffAssignment(String dateTime, Long administratorId, Long responsibleId, Long stationId, Boolean isAuto) {
		try {
			String[] date = dateTime.substring(0, 10).split("-");
			String[] time = dateTime.substring(11, 19).split(":");
			List<TaskAllocationVo> taskAllocationVos = taskAllocation(administratorId, date[0], date[1], date[2], time[0], time[1], time[2], stationId);
			AtomicInteger count = new AtomicInteger(0);
			AtomicBoolean bool = new AtomicBoolean(false);
			AtomicReference<String> userId = new AtomicReference<>("");
			StringBuilder shiftAndStartTime = new StringBuilder();
			if (isAuto) {
				if (taskAllocationVos.stream().filter(taskAllocationVo -> taskAllocationVo.getStatus() == 1).count() >= 1) {
					taskAllocationVos.stream().filter(taskAllocationVo -> taskAllocationVo.getStatus() == 1).sorted(Comparator.comparing(TaskAllocationVo::getTaskCount).thenComparing(TaskAllocationVo::getLevel)).forEach(taskAllocationVo -> {
						if (count.get() == 0) {
							userId.set(taskAllocationVo.getUserId());
							processShiftAndStartTimeSplit(shiftAndStartTime, taskAllocationVo);
						}
						count.getAndIncrement();
					});
					return processUserName(userId.get()) + "," + shiftAndStartTime.toString();
				}
				if (taskAllocationVos.stream().filter(taskAllocationVo -> taskAllocationVo.getStatus() == 2).count() >= 1) {
					taskAllocationVos.stream().filter(taskAllocationVo -> taskAllocationVo.getStatus() == 2).sorted(Comparator.comparing(TaskAllocationVo::getTaskCount).thenComparing(TaskAllocationVo::getLevel)).forEach(taskAllocationVo -> {
						if (count.get() == 0) {
							userId.set(taskAllocationVo.getUserId());
							processShiftAndStartTimeSplit(shiftAndStartTime, taskAllocationVo);
						}
						count.getAndIncrement();
					});
					return processUserName(userId.get()) + "," + shiftAndStartTime.toString();
				}
				if (taskAllocationVos.stream().filter(taskAllocationVo -> taskAllocationVo.getStatus() == 3).count() >= 1) {
					taskAllocationVos.stream().filter(taskAllocationVo -> taskAllocationVo.getStatus() == 3).sorted(Comparator.comparing(TaskAllocationVo::getWorkStartTime).thenComparing(TaskAllocationVo::getLevel)).forEach(taskAllocationVo -> {
						if (count.get() == 0) {
							userId.set(taskAllocationVo.getUserId());
							processShiftAndStartTimeSplit(shiftAndStartTime, taskAllocationVo);
						}
						count.getAndIncrement();
					});
					return processUserName(userId.get()) + "," + shiftAndStartTime.toString();
				}
			} else {
				taskAllocationVos.stream().filter(taskAllocationVo -> taskAllocationVo.getUserId().equals(responsibleId.toString())).forEach(taskAllocationVo -> {
					Integer allocationVoStatus = taskAllocationVo.getStatus();
					if (allocationVoStatus == 0 || allocationVoStatus == 4) {
						bool.set(true);
					}
					processShiftAndStartTimeSplit(shiftAndStartTime, taskAllocationVo);

				});
				if (bool.get()) {
					return "".trim();
				}
				return processUserName(responsibleId.toString()) + "," + shiftAndStartTime.toString();
			}
			return "".trim();
		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}
	}

	public static String processUserName(String userId) {
		User user = serviceFactory.iUserClient.userInfoById(Long.parseLong(userId)).getData();
		if (Func.isNotEmpty(user)) {
			return userId + "," + user.getRealName();
		}
		return userId + "　";
	}

	public String processAutomaticStaffAssignment(String datetime, Long administratorId, Long stationId, Boolean isAuto) {
		return automaticStaffAssignment(datetime, Long.parseLong(administratorId(administratorId).split(",")[0]), administratorId, stationId, isAuto);
	}

	public static Long processPrimaryKey() {
		return Long.parseLong(LocalDate.now().toString().replaceAll("-", "") + LocalTime.now().toString().replaceAll(":", "").replaceAll("\\.", ""));
	}

	public void processShiftAndStartTimeSplit(StringBuilder shiftAndStartTime, TaskAllocationVo taskAllocationVo) {
		shiftAndStartTime.append(taskAllocationVo.getStatus()).append(",").append(taskAllocationVo.getShiftNumber()).append(",").append(taskAllocationVo.getWorkStartTimestamp()).append(",").append(taskAllocationVo.getWorkEndsTimestamp()).append(",").append(taskAllocationVo.getTaskCount());
	}

	public ApiTaskCountVo apiCount(String time, Integer type, Long stationId, Long responsibleId) {
		String[] datetime = timeLimit(time, type).split(",");
		AtomicBoolean responsible = new AtomicBoolean(false);
		if (Func.isNotEmpty(responsibleId)) {
			responsible.set(true);
		}
		List<Repair> repairs = serviceFactory.repairService.list(new QueryWrapper<Repair>().eq(responsible.get(), "assigned_person_id", responsibleId).eq("station_id", stationId).eq("is_create", 1).between("dispatch_time", datetime[0], datetime[1]));
		List<InspectionTask> inspectionTasks = serviceFactory.inspectionRecordService.list(new QueryWrapper<InspectionTask>().eq(responsible.get(), "assigned_person_id", responsibleId).eq("station_id", stationId).eq("is_count", 1).between("task_time", datetime[0], datetime[1]));
		ApiTaskCountVo apiTaskCountVo = new ApiTaskCountVo();
		final int repairCount = repairs.size();
		final int taskCount = repairCount + inspectionTasks.size();
		apiTaskCountVo.setTaskCount(taskCount + "");
		final long inspectionCompleteCount = inspectionTasks.stream().filter(inspectionTask -> inspectionTask.getAccomplish() == 0 && inspectionTask.getTaskType() == 1).count();
		final long maintainCompleteCount = inspectionTasks.stream().filter(inspectionTask -> inspectionTask.getAccomplish() == 0 && inspectionTask.getTaskType() == 2).count();
		final long repairCompleteCount = repairs.stream().filter(repair -> repair.getTaskStatus().equals("1")).count();
		apiTaskCountVo.setCompleteCount((inspectionCompleteCount + maintainCompleteCount + repairCompleteCount) + "");
		final long inspectionTaskTimelyCompleteCount = inspectionTasks.stream().filter(inspectionTask -> inspectionTask.getAccomplish() == 0 && inspectionTask.getRecordStatus() == 0).count();
		final long repairTimelyCompleteCount = repairs.stream().filter(repair -> repair.getTaskStatus().equals("1") && repair.getIsExpired() == 0).count();
		final long timelyComplete = inspectionTaskTimelyCompleteCount + repairTimelyCompleteCount;
		if (taskCount != 0) {
			apiTaskCountVo.setTimelyCompleteRate(ReportForm.rate(taskCount + "", timelyComplete + ""));
			if (repairCount != 0) {
				apiTaskCountVo.setRepairTimelyCompleteRate(ReportForm.rate(repairCount + "", repairTimelyCompleteCount + ""));
			}
			apiTaskCountVo.setRepairCompleteCount(repairCompleteCount + "");
			repairActualWorkHours(repairs, apiTaskCountVo);
			apiTaskCountVo.setInspectionCompleteCount(inspectionCompleteCount + "");
			final long inspectionExpiredCount = inspectionTasks.stream().filter(inspectionTask -> inspectionTask.getAccomplish() == 1 && inspectionTask.getTaskType() == 1 && inspectionTask.getRecordStatus() == 1).count();
			apiTaskCountVo.setInspectionExpiredCount(inspectionExpiredCount + "");
			inspectionTaskActualWorkHours(inspectionTasks, apiTaskCountVo, 1);
			apiTaskCountVo.setMaintainCompleteCount(maintainCompleteCount + "");
			final long maintainExpiredCount = inspectionTasks.stream().filter(inspectionTask -> inspectionTask.getAccomplish() == 1 && inspectionTask.getTaskType() == 2 && inspectionTask.getRecordStatus() == 1).count();
			apiTaskCountVo.setMaintainExpiredCount(maintainExpiredCount + "");
			inspectionTaskActualWorkHours(inspectionTasks, apiTaskCountVo, 2);
		}
		return apiTaskCountVo;
	}

	public String timeLimit(String time, Integer type) {
		String[] date = time.split("-");
		String year = date[0];
		String month = date[1];
		String day = date[2];
		Calendar calendar = Calendar.getInstance();
		AtomicReference<String> range = new AtomicReference<>("");
		String yearMonth = year + "-" + month + "-";
		switch (type) {
			case 1:
				range.set(yearMonth + day + " 00:00:00" + "," + yearMonth + day + " 23:59:59");
				break;
			case 2: //周
				try {
					calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time + " 00:00:00"));
					calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONTH);
					String firstWeek = new SimpleDateFormat("dd").format(calendar.getTime());
					String endsWeek = firstWeek + 6;
					range.set(yearMonth + firstWeek + " 00:00:00" + "," + yearMonth + endsWeek + " 23:59:59");
				} catch (ParseException e) {
					e.printStackTrace();
				}
				break;
			case 3: //月
				calendar.set(Calendar.YEAR, Integer.parseInt(year));
				calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);
				final int firstDay = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
				final int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				range.set(yearMonth + firstDay + " 00:00:00" + "," + yearMonth + lastDay + " 23:59:59");
				break;
		}
		return range.get();
	}

	public void repairActualWorkHours(List<Repair> repairs, ApiTaskCountVo apiTaskCountVo) {
		AtomicInteger sum = new AtomicInteger(0);
		try {
			repairs.forEach(repair -> {
				if (repair.getTaskStatus().equals("1")) {
					if (Func.isNotEmpty(repair.getStartTime()) && Func.isNotEmpty(repair.getRestoreTime())) {
						actualWorkHours(repair.getStartTime(), repair.getRestoreTime(), sum);
					} else {
						sum.set(0);
					}
				}
			});
			if (sum.get() >= 60) {
				String hours = Integer.toString(sum.get() / 60).intern();
				String minute = Integer.toString(sum.get() - (Integer.parseInt(hours) * 60)).intern();
				apiTaskCountVo.setRepairActualWorkHours(hours + "时" + minute + "分");
			} else if (sum.get() > 0 && sum.get() < 60) {
				apiTaskCountVo.setRepairActualWorkHours(sum.get() + "分");
			} else if (sum.get() == 0) {
				apiTaskCountVo.setRepairActualWorkHours("0");
			}

		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}
	}

	public void inspectionTaskActualWorkHours(List<InspectionTask> inspectionTasks, ApiTaskCountVo apiTaskCountVo, Integer type) {
		AtomicInteger sum = new AtomicInteger(0);
		try {
			inspectionTasks.stream().filter(inspectionTask -> inspectionTask.getTaskType().equals(type)).forEach(inspectionTask -> {
				if (inspectionTask.getAccomplish() == 0) {
					if (Func.isNotEmpty(inspectionTask.getActualStartTime()) && Func.isNotEmpty(inspectionTask.getActualEndTime())) {
						actualWorkHours(inspectionTask.getActualStartTime(), inspectionTask.getActualEndTime(), sum);
					} else {
						sum.set(0);
					}
				}
			});
			if (sum.get() >= 60) {
				String hours = Integer.toString(sum.get() / 60).intern();
				String minute = Integer.toString(sum.get() - (Integer.parseInt(hours) * 60)).intern();
				if (type == 1) {
					apiTaskCountVo.setInspectionActualWorkHours(hours + "时" + minute + "分");
				} else {
					apiTaskCountVo.setMaintainActualWorkHours(hours + "时" + minute + "分");
				}
			} else if (sum.get() > 0 && sum.get() < 60) {
				if (type == 1) {
					apiTaskCountVo.setInspectionActualWorkHours(sum.get() + "分");
				} else {
					apiTaskCountVo.setMaintainActualWorkHours(sum.get() + "分");
				}
			} else if (sum.get() == 0) {
				if (type == 1) {
					apiTaskCountVo.setInspectionActualWorkHours("0");
				} else {
					apiTaskCountVo.setMaintainActualWorkHours("0");
				}
			}
		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}
	}

	public void actualWorkHours(Date firstTime, Date lastTime, AtomicInteger sum) {
		String startTime = new SimpleDateFormat("HHmm").format(firstTime).intern();
		String endsTime = new SimpleDateFormat("HHmm").format(lastTime).intern();
		String startHours = startTime.substring(0, 2).intern();
		String endsHours = endsTime.substring(0, 2).intern();
		String startMinute = startTime.substring(2, 4).intern();
		String endsMinute = endsTime.substring(2, 4).intern();
		sum.addAndGet((((Integer.parseInt(endsHours) - Integer.parseInt(startHours)) * 60) + Integer.parseInt(endsMinute)) - Integer.parseInt(startMinute));
	}
}
