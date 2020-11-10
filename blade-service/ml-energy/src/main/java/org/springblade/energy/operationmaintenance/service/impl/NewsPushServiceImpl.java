package org.springblade.energy.operationmaintenance.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.operationmaintenance.entity.*;
import org.springblade.energy.operationmaintenance.mapper.InspectionMapper;
import org.springblade.energy.operationmaintenance.mapper.MaintainMapper;
import org.springblade.energy.operationmaintenance.mapper.NewsPushMapper;
import org.springblade.energy.operationmaintenance.service.*;
import org.springblade.energy.operationmaintenance.vo.InspectionQualityStandardVo;
import org.springblade.energy.operationmaintenance.vo.ProgressTrackVo;
import org.springblade.energy.qualitymanagement.entity.*;
import org.springblade.energy.qualitymanagement.service.*;
import org.springblade.energy.securitymanagement.entity.Patrol;
import org.springblade.energy.securitymanagement.entity.SafetyTask;
import org.springblade.energy.securitymanagement.mapper.PatrolMapper;
import org.springblade.energy.securitymanagement.service.SafetyTaskService;
import org.springblade.energy.securitymanagement.util.DataFactory;
import org.springblade.system.user.entity.User;
import org.springblade.system.user.feign.IUserClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class NewsPushServiceImpl extends BaseServiceImpl<NewsPushMapper, NewsPush> implements NewsPushService {


	PatrolMapper patrolMapper;

	SafetyTaskService safetyTaskService;

	InspectionTaskService inspectionTaskService;

	InspectionMapper inspectionMapper;

	MaintainMapper maintainMapper;

	PlanCountService planCountService;

	NoticeService noticeService;

	MaintainRecordService maintainRecordService;

	IUserClient iUserClient;

	ElectricityInspectionService electricityInspectionService;

	ElectricityMaintenanceService electricityMaintenanceService;

	WaterInspectionService waterInspectionService;

	WaterMaintenanceService waterMaintenanceService;

	GasInspectionService gasInspectionService;

	GasMaintenanceService gasMaintenanceService;

	EnergyInspectionService energyInspectionService;

	EnergyMaintenanceService energyMaintenanceService;

	ActiveService activeService;

	protected static final String TENANT = "CRRCZhuzhou";

	@Override
	public void push() {
		final long systemTime = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
		final long currentTime = Long.parseLong(Long.toString(systemTime).replace(Long.toString(systemTime).substring(9, 13), "0000"));
		List<Inspection> inspections = inspectionMapper.selectList(new QueryWrapper<Inspection>().eq("is_deleted", 0).orderByDesc("create_time"));
		List<Patrol> patrols = patrolMapper.selectList(new QueryWrapper<Patrol>().eq("is_deleted", 0).orderByDesc("create_time"));
		List<User> users = iUserClient.userList();
		List<ElectricityInspection> electricityInspections = electricityInspectionService.list();
		List<ElectricityMaintenance> electricityMaintenanceList = electricityMaintenanceService.list();
		List<WaterInspection> waterInspections = waterInspectionService.list();
		List<WaterMaintenance> waterMaintenanceList = waterMaintenanceService.list();
		List<GasInspection> gasInspections = gasInspectionService.list();
		List<GasMaintenance> gasMaintenanceList = gasMaintenanceService.list();
		List<EnergyInspection> energyInspections = energyInspectionService.list();
		List<EnergyMaintenance> energyMaintenanceList = energyMaintenanceService.list();
		List<Maintain> maintains = maintainMapper.selectList(new QueryWrapper<Maintain>().eq("is_deleted", 0).orderByDesc("create_time"));
		abnormalPushData(currentTime, inspections, maintains, patrols, users, electricityInspections, electricityMaintenanceList, waterInspections, waterMaintenanceList, gasInspections, gasMaintenanceList, energyInspections, energyMaintenanceList);
		normalPushData(currentTime, inspections, maintains, patrols, users, electricityInspections, electricityMaintenanceList, waterInspections, waterMaintenanceList, gasInspections, gasMaintenanceList, energyInspections, energyMaintenanceList);
	}

	private void dataFactory(NewsPush push, NewsPush newsPush, Date taskDate, Date remindDate, Date currentDate, List<Inspection> inspections, List<Maintain> maintains, List<Patrol> patrols, List<User> users, List<ElectricityInspection> electricityInspections, List<ElectricityMaintenance> electricityMaintenanceList, List<WaterInspection> waterInspections, List<WaterMaintenance> waterMaintenanceList, List<GasInspection> gasInspections, List<GasMaintenance> gasMaintenanceList, List<EnergyInspection> energyInspections, List<EnergyMaintenance> energyMaintenanceList) {
		try {
			Integer type = newsPush.getOperationType();
			switch (type) {
				case 0:
					/* TODO----------------------------  巡检  ----------------------------*/
					addInspectionTask(newsPush, push, taskDate, remindDate, currentDate, users, inspections, electricityInspections, electricityMaintenanceList, waterInspections, waterMaintenanceList, gasInspections, gasMaintenanceList, energyInspections, energyMaintenanceList);
					/* TODO----------------------------  巡检  ----------------------------*/
					break;
				case 1:
					/* TODO----------------------------  保养  ----------------------------*/
					addMaintainTask(newsPush, push, taskDate, remindDate, currentDate, users, maintains, electricityInspections, electricityMaintenanceList, waterInspections, waterMaintenanceList, gasInspections, gasMaintenanceList, energyInspections, energyMaintenanceList);
					/* TODO----------------------------  保养  ----------------------------*/
					break;
				case 2:
					/* TODO----------------------------  维修  ----------------------------*/
					/* TODO----------------------------  维修  ----------------------------*/
					break;
				case 3:
					/* TODO----------------------------  安全检查  ----------------------------*/
					addSecurityCheckRecord(newsPush, taskDate, patrols, currentDate, users);
					/* TODO----------------------------  安全检查  ----------------------------*/
					break;
			}
			if (baseMapper.update(push, new QueryWrapper<NewsPush>().eq("id", newsPush.getId())) == 0) /* 推送时间 */
				throw new ServiceException("更新消息推送失败");
		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}
	}

	private void normalPushData(Long currentTime, List<Inspection> inspections, List<Maintain> maintains, List<Patrol> patrols, List<User> users, List<ElectricityInspection> electricityInspections, List<ElectricityMaintenance> electricityMaintenanceList, List<WaterInspection> waterInspections, List<WaterMaintenance> waterMaintenanceList, List<GasInspection> gasInspections, List<GasMaintenance> gasMaintenanceList, List<EnergyInspection> energyInspections, List<EnergyMaintenance> energyMaintenanceList) {
		try {
			List<NewsPush> newsPushes = baseMapper.selectList(new QueryWrapper<NewsPush>()
				.eq("schedule_time", currentTime)
				.eq("is_deleted", 0));
			if (newsPushes.size() <= 0)
				return;
			Date currentDate = new Date(currentTime);
			ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());
			CountDownLatch countDownLatch = new CountDownLatch(newsPushes.size());
			newsPushes.forEach((newsPush) ->
				threadPoolExecutor.execute(() -> {
					synchronized (this) {
						AtomicLong newPushTime = new AtomicLong();
						NewsPush push = new NewsPush();
						Integer isPolling = newsPush.getIsPolling();
						Long scheduleTime = newsPush.getScheduleTime();
						Long pollingTime = newsPush.getPollingTime();
						Long advanceTime = newsPush.getAdvanceTime();
						if (isPolling > 0) {
							newPushTime.set(scheduleTime + pollingTime);
							push.setLastPushTime(new Date(scheduleTime));
							push.setScheduleTime(newPushTime.get());
						}

						Date remindDate = new Date(newPushTime.get());
						push.setTime(remindDate);
						final long taskTime = newPushTime.get() + advanceTime;
						Date taskDate = new Date(taskTime);
						push.setTaskTime(taskDate);

						/* TODO ---------------------------------------------  工厂  --------------------------------------------- */
						dataFactory(push, newsPush, taskDate, remindDate, currentDate, inspections, maintains, patrols, users, electricityInspections, electricityMaintenanceList, waterInspections, waterMaintenanceList, gasInspections, gasMaintenanceList, energyInspections, energyMaintenanceList);
						/* TODO  ---------------------------------------------  工厂  --------------------------------------------- */

						/* TODO ---------------------------------------------  消息推送  --------------------------------------------- */
						//System.out.println("-------------------------------------  欢迎来到定制化推送处理区  -------------------------------------");
						/* TODO ---------------------------------------------  消息推送  --------------------------------------------- */
						countDownLatch.countDown();
					}
				}));
			try {
				countDownLatch.await(30, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				throw new ServiceException(e.getMessage());
			}
			threadPoolExecutor.shutdownNow();
		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}
	}

	private void abnormalPushData(Long currentTime, List<Inspection> inspections, List<Maintain> maintains, List<Patrol> patrols, List<User> users, List<ElectricityInspection> electricityInspections, List<ElectricityMaintenance> electricityMaintenanceList, List<WaterInspection> waterInspections, List<WaterMaintenance> waterMaintenanceList, List<GasInspection> gasInspections, List<GasMaintenance> gasMaintenanceList, List<EnergyInspection> energyInspections, List<EnergyMaintenance> energyMaintenanceList) {
		try {
			String[] newTime = new SimpleDateFormat("yyyy-MM-dd").format(currentTime).split("-");
			final long startTime = LocalDateTime.of(Integer.parseInt(newTime[0]), Integer.parseInt(newTime[1]), Integer.parseInt(newTime[2]), 0, 0, 0).toInstant(ZoneOffset.of("+8")).toEpochMilli();
			List<NewsPush> expired = baseMapper.selectList(new QueryWrapper<NewsPush>()
				.lt("schedule_time", System.currentTimeMillis())
				.eq("is_deleted", 0));
			ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());
			CountDownLatch countDownLatch = new CountDownLatch((int) expired.stream().distinct().count());
			Date currentDate = new Date(currentTime);
			if (expired.size() > 0) {
				expired.stream().distinct().forEach((newsPush ->
					threadPoolExecutor.execute(() -> {
						synchronized (this) {
							if (newsPush.getIsPolling() <= 0 && Func.isNotEmpty(newsPush.getLastPushTime())) {
								return;
							}
							Long triggerTime = newsPush.getTriggerTime();
							Long advanceTime = newsPush.getAdvanceTime();
							Long pollingTime = newsPush.getPollingTime();
							Long scheduleTime = newsPush.getScheduleTime();
							if (Long.toString(triggerTime).length() == 13) {
								String[] oldTime = new SimpleDateFormat("yyyy-MM-dd").format(triggerTime).split("-");
								final long endsTime = LocalDateTime.of(Integer.parseInt(oldTime[0]), Integer.parseInt(oldTime[1]), Integer.parseInt(oldTime[2])
									, 0, 0, 0).toInstant(ZoneOffset.of("+8")).toEpochMilli();
								if (scheduleTime < currentTime) {
									NewsPush push = new NewsPush();
									final long strideTime = (triggerTime - advanceTime) - endsTime;
									AtomicLong time = new AtomicLong(startTime + strideTime);
									final long spacingTime = startTime - endsTime;
									if (spacingTime % pollingTime == 0) {
										if (time.get() > currentTime) {
											time.set(time.get());
										} else {
											time.set(time.get() + pollingTime);
										}
									} else {
										if (time.get() > currentTime) {
											time.set(time.get() + pollingTime - (spacingTime % pollingTime));
										} else {
											time.set((time.get() + pollingTime - (spacingTime % pollingTime)) + pollingTime);
										}
									}
									Date remindDate = new Date(time.get());
									push.setTime(remindDate);
									Date taskDate = new Date(time.get() + advanceTime);
									push.setTaskTime(taskDate);
									push.setScheduleTime(time.get());
									dataFactory(push, newsPush, taskDate, remindDate, currentDate, inspections, maintains, patrols, users, electricityInspections, electricityMaintenanceList, waterInspections, waterMaintenanceList, gasInspections, gasMaintenanceList, energyInspections, energyMaintenanceList);
									if (!update(push, new QueryWrapper<NewsPush>().eq("id", newsPush.getId()))) {
										throw new ServiceException("异常任务无法恢复");
									}
								}
							} else {
								remove(new QueryWrapper<NewsPush>().eq("id", newsPush.getId()));
							}
							countDownLatch.countDown();
						}
					})));
			}
			try {
				countDownLatch.await(30, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				throw new ServiceException(e.getMessage());
			}
			threadPoolExecutor.shutdownNow();
		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}
	}

	private void addSecurityCheckRecord(NewsPush newsPush, Date taskDate, List<Patrol> patrols, Date currentDate, List<User> users) {
		try {
			Long newsPushId = newsPush.getId();
			Long taskId = newsPush.getTaskId();
			Long primaryKey = DataFactory.processPrimaryKey();
		/*	if (safetyTaskService.list(new QueryWrapper<SafetyTask>().eq("create_time", currentDate).eq("task_id", newsPushId)).size() > 0) {
				return;
			}*/
			SafetyTask safetyInspectionRecord = new SafetyTask();
			PlanCount planCount = new PlanCount();
			Notice notice = new Notice();
			patrols.stream().distinct().filter(patrol -> patrol.getId().equals(taskId)).forEach(patrol -> {
				Long responsible = patrol.getResponsible();
				if (DataFactory.isAdministrator(responsible)) {
					planCount.setIsAdmin(0);
					safetyInspectionRecord.setIsCount(0);
					safetyInspectionRecord.setTaskStatus("");
				} else {
					planCount.setTaskTime(taskDate);
					safetyInspectionRecord.setTaskTime(patrol.getNextTime());
					safetyInspectionRecord.setRemindTime(new Date(newsPush.getScheduleTime()));
				}
				Long stationId = patrol.getStationId();
				String stationName = patrol.getStationName();
				Long siteId = patrol.getSiteId();
				String siteName = patrol.getSiteName();
				String taskName = patrol.getTaskName();
				String standardDetails = patrol.getStandardDetails();
				String checkContent = patrol.getCheckContent();
				Integer normWork = patrol.getNormWork();
				Long checkDuration = patrol.getCheckDuration();
				Long adminId = patrol.getCreateUser();
				Long dept = patrol.getCreateDept();
				planCount.setStationId(stationId);
				planCount.setSiteId(siteId);
				planCount.setPlanId(taskId);
				planCount.setTaskResponsible(responsible);
				planCount.setTaskType(3);
				planCount.setCreateUser(adminId);
				planCount.setCreateDept(dept);
				planCount.setTenantId(TENANT);
				planCount.setCreateTime(currentDate);
				planCount.setUpdateUser(-1L);
				safetyInspectionRecord.setId(primaryKey);
				safetyInspectionRecord.setTaskId(newsPushId);
				safetyInspectionRecord.setTaskType(1);
				safetyInspectionRecord.setStationId(stationId);
				safetyInspectionRecord.setStationName(stationName);
				safetyInspectionRecord.setSiteId(siteId.toString());
				safetyInspectionRecord.setSiteName(siteName);
				safetyInspectionRecord.setTaskName(taskName);
				safetyInspectionRecord.setCheckStandard(standardDetails);
				safetyInspectionRecord.setCheckContent(checkContent);
				safetyInspectionRecord.setResponsiblePersonId(responsible);
				safetyInspectionRecord.setCreateUser(adminId);
				safetyInspectionRecord.setCreateDept(dept);
				safetyInspectionRecord.setTenantId(TENANT);
				users.stream().distinct().filter(user -> user.getId().equals(responsible)).forEach(user ->
					safetyInspectionRecord.setResponsiblePersonName(user.getRealName()));
				safetyInspectionRecord.setStandardTime(normWork);
				safetyInspectionRecord.setExpectTime(Integer.parseInt(Long.toString(checkDuration)));
				safetyInspectionRecord.setPushStatus(0);
				safetyInspectionRecord.setPushAbnormalTime(currentDate);
				safetyInspectionRecord.setCreateTime(currentDate);
				notice.setTaskId(primaryKey);
				notice.setSiteId(newsPush.getSiteId());
				notice.setStationId(newsPush.getStationId());
				notice.setResponsibleId(newsPush.getResponsibleId());
				notice.setLeaderId(newsPush.getSuperiorsId());
				notice.setNoticeType(2);
				notice.setNoticeName("安全巡查任务");
				notice.setNoticeTime(currentDate);
				notice.setTaskType(newsPush.getOperationType());
				notice.setSafetyInspectionType(safetyInspectionRecord.getTaskType());
				notice.setTenantId(TENANT);
				notice.setCreateUser(adminId);
				notice.setCreateDept(dept);
				if (!safetyTaskService.save(new DataFactory<SafetyTask>().nameFactory(safetyInspectionRecord))) {
					throw new ServiceException("安全巡查任务添加失败");
				}
				if (!planCountService.save(planCount)) {
					throw new ServiceException("安全检查计划统计添加失败");
				}
				if (!noticeService.save(notice)) {
					throw new ServiceException("安全巡查通知失败");
				}
				if (patrol.getIsEnable() > 0) {
					patrol.setNextTime(taskDate);
					int id = patrolMapper.update(patrol, new QueryWrapper<Patrol>().eq("id", taskId));
					if (id == 0) {
						throw new ServiceException("新安全检查更新失败");
					}
				}
			});
		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}
	}

	private void addInspectionTask(NewsPush newsPush, NewsPush push, Date taskDate, Date remindDate, Date currentDate, List<User> users, List<Inspection> inspections, List<ElectricityInspection> electricityInspections, List<ElectricityMaintenance> electricityMaintenanceList, List<WaterInspection> waterInspections, List<WaterMaintenance> waterMaintenanceList, List<GasInspection> gasInspections, List<GasMaintenance> gasMaintenanceList, List<EnergyInspection> energyInspections, List<EnergyMaintenance> energyMaintenanceList) {
		try {
			Long newsPushId = newsPush.getId();
		/*	if (inspectionTaskService.list(new QueryWrapper<InspectionTask>().eq("create_time", currentDate).eq("task_type", 1).eq("task_id", newsPushId)).size() > 0) {
				return;
			}*/
			Long primaryKey = DataFactory.processPrimaryKey();
			Long taskId = newsPush.getTaskId();
			InspectionTask inspectionRecord = new InspectionTask();
			PlanCount planCount = new PlanCount();
			Notice notice = new Notice();
			inspections.stream().distinct().filter(inspection -> inspection.getId().equals(taskId)).forEach(inspection -> {
				inspectionRecord.setId(primaryKey);
				inspectionRecord.setTaskId(newsPushId);
				/* 任务类型 1：巡检 2：保养 */
				inspectionRecord.setTaskType(1);
				/* 任务状态 0：未检 1：进行中 2：暂停 3：已检 4：已取消 默认未检 */
				String assignedPersonId = inspection.getAssignedPersonId().intern();
				if (DataFactory.isAdministrator(Long.parseLong(assignedPersonId))) {
					planCount.setIsAdmin(0);
					inspectionRecord.setIsCount(0);
					inspectionRecord.setTaskStatus("");
				} else {
					timeManagement(newsPush, inspectionRecord, inspection, null);
				}
				Integer inspectionType = inspection.getInspectionType();
				inspectionRecord.setInspectionType(inspectionType);
				inspectionRecord.setInspectionCycle(inspection.getInspectionCycle());
				inspectionRecord.setInspectionNumber(inspection.getInspectionNumber());
				inspectionRecord.setSourceType(inspection.getSourceType());
				inspectionRecord.setWorkHours(inspection.getWorkHours());
				inspectionRecord.setExpectHours(inspection.getInspectionAfter());
				inspectionRecord.setCreateUser(inspection.getCreateUser());
				inspectionRecord.setCreateDept(inspection.getCreateDept());
				inspectionRecord.setCreateTime(currentDate);
				inspectionRecord.setTenantId(TENANT);
				inspectionRecord.setStationId(inspection.getStationId().toString());
				inspectionRecord.setStationName(inspection.getStationName());
				inspectionRecord.setSiteId(inspection.getSiteId().toString());
				inspectionRecord.setSiteName(inspection.getSiteName());
				inspectionRecord.setAssignedPhone(newsPush.getPhone());
				inspectionRecord.setTimeQuantum(inspection.getTimeQuantum());
				inspectionRecord.setAssignedPersonName(inspection.getAssignedPersonName());
				if (Func.isNotEmpty(inspection.getAssignedPersonId()))
					inspectionRecord.setAssignedPersonId(Long.parseLong(assignedPersonId));
				if (Func.isNotEmpty(inspection.getEquipmentNo()))
					inspectionRecord.setEquipmentNo(Long.parseLong(inspection.getEquipmentNo()));
				inspectionRecord.setEquipmentName(inspection.getEquipmentName());
				inspectionRecord.setSuperiorsId(newsPush.getSuperiorsId());
				inspectionRecord.setInspectionItem(inspectionItem(newsPush.getOperationType(), inspection.getSourceType(), inspection.getStationId(), inspection.getSiteId(), inspection.getInspectionType(), primaryKey, electricityInspections, electricityMaintenanceList, waterInspections, waterMaintenanceList, gasInspections, gasMaintenanceList, energyInspections, energyMaintenanceList));
				inspectionRecord.setProgressTrack(progressTrack(newsPush.getUpdateTime(), newsPush.getScheduleTime()));
				Long createUser = inspection.getCreateUser();
				Long createDept = inspection.getCreateDept();
				planCount.setCreateUser(createUser);
				planCount.setCreateDept(createDept);
				planCount.setTenantId(TENANT);
				planCount.setCreateTime(currentDate);
				planCount.setUpdateUser(-1L);
				notice.setTaskId(primaryKey);
				notice.setSiteId(newsPush.getSiteId());
				notice.setStationId(newsPush.getStationId());
				notice.setResponsibleId(newsPush.getResponsibleId());
				notice.setLeaderId(newsPush.getSuperiorsId());
				notice.setNoticeType(2);
				notice.setNoticeTime(currentDate);
				notice.setTaskType(newsPush.getOperationType());
				notice.setInspectionType(inspectionType);
				notice.setCreateUser(createUser);
				notice.setCreateDept(createDept);
				switch (inspectionType) {
					case 1:
						notice.setNoticeName("巡检任务");
						break;
					case 2:
						notice.setNoticeName("巡查任务");
						break;
				}
				notice.setInspectionSourceType(inspection.getSourceType());
				notice.setTenantId(TENANT);
				if (!inspectionTaskService.save(inspectionRecord)) {
					throw new ServiceException("巡检任务添加失败");
				}
				if (!noticeService.save(notice)) {
					throw new ServiceException("巡检通知失败");
				}
				if (inspection.getAssignedType() == 1) {
					inspection.setNextTime(taskDate);
					inspection.setRemindTime(remindDate);
					inspectionMapper.update(inspection, new QueryWrapper<Inspection>().eq("id", newsPush.getTaskId()));
				} else {
					autoDistributionResponsiblePerson(newsPush, planCount, taskDate, remindDate, push, users);
				}
			});
		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}
	}

	public void addMaintainTask(NewsPush newsPush, NewsPush push, Date taskDate, Date remindDate, Date currentDate, List<User> users, List<Maintain> maintains, List<ElectricityInspection> electricityInspections, List<ElectricityMaintenance> electricityMaintenanceList, List<WaterInspection> waterInspections, List<WaterMaintenance> waterMaintenanceList, List<GasInspection> gasInspections, List<GasMaintenance> gasMaintenanceList, List<EnergyInspection> energyInspections, List<EnergyMaintenance> energyMaintenanceList) {
		try {
			Long newsPushId = newsPush.getId();
			/*if (inspectionTaskService.list(new QueryWrapper<InspectionTask>().eq("create_time", currentDate).eq("task_type", 2).eq("task_id", newsPushId)).size() > 0) {
				return;
			}*/
			Long primaryKey = DataFactory.processPrimaryKey();
			Long taskId = newsPush.getTaskId();
			InspectionTask inspectionRecord = new InspectionTask();
			PlanCount planCount = new PlanCount();
			Notice notice = new Notice();
			maintains.stream().distinct().filter(maintain -> maintain.getId().equals(taskId)).forEach(maintain -> {
				inspectionRecord.setId(primaryKey);
				inspectionRecord.setTaskId(newsPushId);
				/* 任务类型 1：巡检 2：保养 */
				inspectionRecord.setTaskType(2);
				/* 任务状态 0：未检 1：进行中 2：暂停 3：已检 4：已取消 默认未检 */
				String assignedPersonId = maintain.getAssignedPersonId();
				if (DataFactory.isAdministrator(Long.parseLong(assignedPersonId))) {
					planCount.setIsAdmin(0);
					inspectionRecord.setIsCount(0);
					inspectionRecord.setTaskStatus("");
				} else {
					timeManagement(newsPush, inspectionRecord, null, maintain);
				}
				inspectionRecord.setMaintainCycle(maintain.getMaintainCycle());
				inspectionRecord.setSourceType(maintain.getSourceType());
				inspectionRecord.setWorkHours(maintain.getWorkHours());
				inspectionRecord.setExpectHours(maintain.getMaintainAfter());
				inspectionRecord.setCreateUser(maintain.getCreateUser());
				inspectionRecord.setCreateDept(maintain.getCreateDept());
				inspectionRecord.setCreateTime(currentDate);
				inspectionRecord.setTenantId(TENANT);
				inspectionRecord.setStationId(maintain.getStationId().toString());
				inspectionRecord.setStationName(maintain.getStationName());
				inspectionRecord.setSiteId(maintain.getSiteId().toString());
				inspectionRecord.setSiteName(maintain.getSiteName());
				inspectionRecord.setAssignedPhone(newsPush.getPhone());
				inspectionRecord.setAssignedPersonName(maintain.getAssignedPersonName());
				if (Func.isNotEmpty(maintain.getAssignedPersonId()))
					inspectionRecord.setAssignedPersonId(Long.parseLong(maintain.getAssignedPersonId()));
				if (Func.isNotEmpty(maintain.getEquipmentNo()))
					inspectionRecord.setEquipmentNo(Long.parseLong(maintain.getEquipmentNo()));
				inspectionRecord.setEquipmentName(maintain.getEquipmentName());
				inspectionRecord.setSuperiorsId(newsPush.getSuperiorsId());
				inspectionRecord.setInspectionItem(inspectionItem(newsPush.getOperationType(), maintain.getSourceType(), maintain.getStationId(), maintain.getSiteId(), null, primaryKey, electricityInspections, electricityMaintenanceList, waterInspections, waterMaintenanceList, gasInspections, gasMaintenanceList, energyInspections, energyMaintenanceList));
				Long createUser = maintain.getCreateUser();
				Long createDept = maintain.getCreateDept();
				planCount.setCreateUser(createUser);
				planCount.setCreateDept(createDept);
				planCount.setTenantId(TENANT);
				planCount.setCreateTime(currentDate);
				planCount.setUpdateUser(-1L);
				notice.setTaskId(primaryKey);
				notice.setSiteId(newsPush.getSiteId());
				notice.setStationId(newsPush.getStationId());
				notice.setResponsibleId(newsPush.getResponsibleId());
				notice.setLeaderId(newsPush.getSuperiorsId());
				notice.setNoticeType(2);
				notice.setNoticeName("保养任务");
				notice.setNoticeTime(currentDate);
				notice.setTaskType(newsPush.getOperationType());
				notice.setInspectionSourceType(maintain.getSourceType());
				notice.setTenantId(TENANT);
				notice.setCreateUser(createUser);
				notice.setCreateDept(createDept);
				if (!inspectionTaskService.save(inspectionRecord)) {
					throw new ServiceException("保养任务添加失败");
				}
				if (!noticeService.save(notice)) {
					throw new ServiceException("保养通知失败");
				}
				if (maintain.getAssignedType() == 1) {
					maintain.setNextTime(taskDate);
					maintain.setRemindTime(remindDate);
					maintainMapper.update(maintain, new QueryWrapper<Maintain>().eq("id", newsPush.getTaskId()));
				} else {
					autoDistributionResponsiblePerson(newsPush, planCount, taskDate, remindDate, push, users);
				}
			});
		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}
	}

	private void autoDistributionResponsiblePerson(NewsPush newsPush, PlanCount planCount, Date taskDate, Date remindDate, NewsPush push, List<User> users) {
		try {
			String datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(taskDate);
			AtomicReference<Long> pushResponsibleId = new AtomicReference<>(newsPush.getResponsibleId());
			AtomicReference<String> pushResponsibleName = new AtomicReference<>("");
			String processAutomaticStaffAssignment = new DataFactory<>().processAutomaticStaffAssignment(datetime, pushResponsibleId.get(), newsPush.getStationId(), true);
			if (processAutomaticStaffAssignment.equals("".trim())) {
				if (!DataFactory.isAdministrator(pushResponsibleId.get())) {
					pushResponsibleId.set(Long.parseLong(DataFactory.administratorId(pushResponsibleId.get()).split(",")[0]));
				}
				push.setResponsibleId(pushResponsibleId.get());
				String[] user = DataFactory.processUserName(Long.toString(pushResponsibleId.get())).split(",");
				pushResponsibleName.set(user[1]);
			} else {
				String[] responsiblePerson = processAutomaticStaffAssignment.split(",");
				long responsiblePersonId = Long.parseLong(responsiblePerson[0]);
				pushResponsibleId.set(responsiblePersonId);
				String responsibleName = responsiblePerson[1];
				push.setResponsibleId(responsiblePersonId);
				pushResponsibleName.set(responsibleName);
				push.setWorkStatus(Integer.parseInt(responsiblePerson[2]));
				push.setWorkShift(Integer.parseInt(responsiblePerson[3]));
				push.setWorkTime(new Date(Long.parseLong(responsiblePerson[4])));
				push.setOffWorkTime(new Date(Long.parseLong(responsiblePerson[5])));
			}
			users.stream().distinct().filter(user -> user.getId().equals(pushResponsibleId.get())).forEach(user -> {
				push.setSendSms(user.getSendSms());
				push.setSendEmail(user.getSendEmail());
				push.setSendApp(user.getSendStation());
				push.setPhone(user.getPhone());
				push.setEmail(user.getEmail());
			});
			Long id = newsPush.getTaskId();

			planCount.setStationId(newsPush.getStationId());
			planCount.setSiteId(newsPush.getSiteId());
			planCount.setPlanId(newsPush.getTaskId());
			planCount.setTaskTime(taskDate);
			planCount.setTaskResponsible(pushResponsibleId.get());
			switch (newsPush.getOperationType()) {
				case 0:
					/*  TODO 巡检 */
					Inspection inspection = new Inspection();
					inspection.setAssignedPersonId(pushResponsibleId.get().toString());
					inspection.setAssignedPersonName(pushResponsibleName.get());
					inspection.setNextTime(taskDate);
					inspection.setRemindTime(remindDate);

					planCount.setTaskType(0);
					if (inspectionMapper.update(inspection, new QueryWrapper<Inspection>().eq("id", id)) == 0) {
						throw new ServiceException("新巡检更新失败");
					}
					break;
				case 1:
					/*  TODO 保养 */
					Maintain maintain = new Maintain();
					maintain.setAssignedPersonId(pushResponsibleId.get().toString());
					maintain.setAssignedPersonName(pushResponsibleName.get());
					maintain.setNextTime(taskDate);
					maintain.setRemindTime(remindDate);

					planCount.setTaskType(1);
					if (maintainMapper.update(maintain, new QueryWrapper<Maintain>().eq("id", id)) == 0) {
						throw new ServiceException("新保养更新失败");
					}
					break;
				case 2:
					/*  TODO 维修 */
					break;
				case 3:
					/*  TODO 安全检查 */
					break;
			}

			if (!planCountService.save(planCount)) {
				throw new ServiceException("计划统计添加失败");
			}
		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}
	}

	private String inspectionItem(Integer taskType, Integer sourceType, Long stationId, Long siteId, Integer inspectionType, Long primaryKey, List<ElectricityInspection> electricityInspections, List<ElectricityMaintenance> electricityMaintenanceList, List<WaterInspection> waterInspections, List<WaterMaintenance> waterMaintenanceList, List<GasInspection> gasInspections, List<GasMaintenance> gasMaintenanceList, List<EnergyInspection> energyInspections, List<EnergyMaintenance> energyMaintenanceList) {
		/* TODO 资源类型 1：电 2：水 3：气 4：重点能耗 */
		/*  TODO任务类型 0：巡检 1：保养 */
		try {
			List<InspectionQualityStandardVo> list = new ArrayList<>();
			AtomicInteger index = new AtomicInteger(1);
			switch (sourceType) {
				case 1:
					switch (taskType) {
						case 0:
							electricityInspections.stream().distinct().filter(electricityInspection -> electricityInspection.getStationId().equals(stationId) && electricityInspection.getSiteId().equals(siteId) && electricityInspection.getInspectionType().equals(inspectionType)).sorted(Comparator.comparing(ElectricityInspection::getCreateTime).reversed()).forEach(electricityInspection -> {
								InspectionQualityStandardVo inspectionQualityStandardVo = new InspectionQualityStandardVo();
								inspectionQualityStandardShared(inspectionQualityStandardVo, index.get(), primaryKey, taskType, sourceType, electricityInspection.getIsAppPhoto(), 0, 0);
								inspectionQualityStandardVo.setItem(electricityInspection.getInspectionItem());
								inspectionQualityStandardVo.setCheckMethod(electricityInspection.getInspectionMethod());
								inspectionQualityStandardVo.setNormal(electricityInspection.getNormal());
								inspectionQualityStandardVo.setAbnormal(electricityInspection.getAbnormal());
								if (inspectionType == 2) {
									inspectionQualityStandardDevShared(inspectionQualityStandardVo, electricityInspection.getDevNumber(), electricityInspection.getDevName());
								}
								inspectionQualityStandardVo.setInspectionContent(electricityInspection.getInspectionContent());
								list.add(inspectionQualityStandardVo);
								index.getAndIncrement();
							});
							break;
						case 1:
							electricityMaintenanceList.stream().distinct().filter(electricityMaintenance -> electricityMaintenance.getStationId().equals(stationId) && electricityMaintenance.getSiteId().equals(siteId)).sorted(Comparator.comparing(ElectricityMaintenance::getCreateTime).reversed()).forEach(electricityMaintenance -> {
								InspectionQualityStandardVo inspectionQualityStandardVo = new InspectionQualityStandardVo();
								inspectionQualityStandardShared(inspectionQualityStandardVo, index.get(), primaryKey, taskType, sourceType, electricityMaintenance.getIsAppPhoto(), 0, 0);
								inspectionQualityStandardMaintenanceShared(inspectionQualityStandardVo, electricityMaintenance.getType(), electricityMaintenance.getLocation(), electricityMaintenance.getActivityItem(), electricityMaintenance.getPrecautions(), electricityMaintenance.getUtilAndSkill(), electricityMaintenance.getJudgementStandard());
								inspectionQualityStandardDevShared(inspectionQualityStandardVo, electricityMaintenance.getDevName(), electricityMaintenance.getDevNameStr());
								list.add(inspectionQualityStandardVo);
								index.getAndIncrement();
							});
							break;
					}
					break;
				case 2:
					switch (taskType) {
						case 0:
							waterInspections.stream().distinct().filter(waterInspection -> waterInspection.getStationId().equals(stationId) && waterInspection.getSiteId().equals(siteId)).sorted(Comparator.comparing(WaterInspection::getCreateTime).reversed()).forEach(waterInspection -> {
								InspectionQualityStandardVo inspectionQualityStandardVo = new InspectionQualityStandardVo();
								inspectionQualityStandardShared(inspectionQualityStandardVo, index.get(), primaryKey, taskType, sourceType, waterInspection.getIsAppPhoto(), 0, 0);
								inspectionQualityStandardVo.setUnitStatus(waterInspection.getUnitStatus());
								inspectionQualityStandardVo.setItem(waterInspection.getInspectionItem());
								inspectionQualityStandardVo.setStandardRange(waterInspection.getStandardRange());
								list.add(inspectionQualityStandardVo);
								index.getAndIncrement();
							});
							break;
						case 1:
							waterMaintenanceList.stream().distinct().filter(waterMaintenance -> waterMaintenance.getStationId().equals(stationId) && waterMaintenance.getSiteId().equals(siteId)).sorted(Comparator.comparing(WaterMaintenance::getCreateTime).reversed()).forEach(waterMaintenance -> {
								InspectionQualityStandardVo inspectionQualityStandardVo = new InspectionQualityStandardVo();
								inspectionQualityStandardShared(inspectionQualityStandardVo, index.get(), primaryKey, taskType, sourceType, waterMaintenance.getIsAppPhoto(), 0, 0);
								inspectionQualityStandardMaintenanceShared(inspectionQualityStandardVo, waterMaintenance.getType(), waterMaintenance.getLocation(), waterMaintenance.getActivityItem(), waterMaintenance.getPrecautions(), waterMaintenance.getUtilAndSkill(), waterMaintenance.getJudgementStandard());
								list.add(inspectionQualityStandardVo);
								index.getAndIncrement();
							});
							break;
					}
					break;
				case 3:
					switch (taskType) {
						case 0:
							gasInspections.stream().distinct().filter(gasInspection -> gasInspection.getStationId().equals(stationId) && gasInspection.getSiteId().equals(siteId)).sorted(Comparator.comparing(GasInspection::getCreateTime).reversed()).forEach(gasInspection -> {
								InspectionQualityStandardVo inspectionQualityStandardVo = new InspectionQualityStandardVo();
								inspectionQualityStandardShared(inspectionQualityStandardVo, index.get(), primaryKey, taskType, sourceType, gasInspection.getIsAppPhoto(), 0, 0);
								inspectionQualityStandardVo.setUnitStatus(gasInspection.getUnitStatus());
								inspectionQualityStandardVo.setItem(gasInspection.getInspectionItem());
								inspectionQualityStandardVo.setStandardRange(gasInspection.getStandardRange());
								list.add(inspectionQualityStandardVo);
								index.getAndIncrement();
							});
							break;
						case 1:
							gasMaintenanceList.stream().distinct().filter(gasMaintenance -> gasMaintenance.getStationId().equals(stationId) && gasMaintenance.getSiteId().equals(siteId)).sorted(Comparator.comparing(GasMaintenance::getCreateTime).reversed()).forEach(gasMaintenance -> {
								InspectionQualityStandardVo inspectionQualityStandardVo = new InspectionQualityStandardVo();
								inspectionQualityStandardShared(inspectionQualityStandardVo, index.get(), primaryKey, taskType, sourceType, gasMaintenance.getIsAppPhoto(), 0, 0);
								inspectionQualityStandardMaintenanceShared(inspectionQualityStandardVo, gasMaintenance.getType(), gasMaintenance.getLocation(), gasMaintenance.getActivityItem(), gasMaintenance.getPrecautions(), gasMaintenance.getUtilAndSkill(), gasMaintenance.getJudgementStandard());
								list.add(inspectionQualityStandardVo);
								index.getAndIncrement();
							});
							break;
					}
					break;
				case 4:
					switch (taskType) {
						case 0:
							energyInspections.stream().distinct().filter(energyInspection -> energyInspection.getStationId().equals(stationId) && energyInspection.getSiteId().equals(siteId)).sorted(Comparator.comparing(EnergyInspection::getCreateTime).reversed()).forEach(energyInspection -> {
								InspectionQualityStandardVo inspectionQualityStandardVo = new InspectionQualityStandardVo();
								inspectionQualityStandardShared(inspectionQualityStandardVo, index.get(), primaryKey, taskType, sourceType, energyInspection.getIsAppPhoto(), 0, 0);
								inspectionQualityStandardVo.setItem(energyInspection.getInspectionItem());
								inspectionQualityStandardVo.setCheckType(energyInspection.getCheckType());
								inspectionQualityStandardDevShared(inspectionQualityStandardVo, energyInspection.getRunDevice(), energyInspection.getDevName());
								list.add(inspectionQualityStandardVo);
								index.getAndIncrement();
							});
							break;
						case 1:
							energyMaintenanceList.stream().distinct().filter(energyMaintenance -> energyMaintenance.getStationId().equals(stationId) && energyMaintenance.getSiteId().equals(siteId)).sorted(Comparator.comparing(EnergyMaintenance::getCreateTime).reversed()).forEach(energyMaintenance -> {
								InspectionQualityStandardVo inspectionQualityStandardVo = new InspectionQualityStandardVo();
								inspectionQualityStandardShared(inspectionQualityStandardVo, index.get(), primaryKey, taskType, sourceType, energyMaintenance.getIsAppPhoto(), 0, 0);
								inspectionQualityStandardMaintenanceShared(inspectionQualityStandardVo, energyMaintenance.getType(), energyMaintenance.getLocation(), energyMaintenance.getActivityItem(), energyMaintenance.getPrecautions(), energyMaintenance.getUtilAndSkill(), energyMaintenance.getJudgementStandard());
								inspectionQualityStandardDevShared(inspectionQualityStandardVo, energyMaintenance.getDevName(), energyMaintenance.getDevNameStr());
								list.add(inspectionQualityStandardVo);
								index.getAndIncrement();
							});
							break;
					}
			}
			return JSONObject.toJSONString(list);
		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}
	}

	public void inspectionQualityStandardShared(
		InspectionQualityStandardVo inspectionQualityStandardVo, Integer itemId, Long primaryKey, Integer taskType, Integer sourceType, Integer isAppPhoto, Integer isRepair, Integer isInspection) {
		inspectionQualityStandardVo.setItemId(itemId);
		inspectionQualityStandardVo.setTaskId(primaryKey);
		inspectionQualityStandardVo.setTaskType(taskType);
		inspectionQualityStandardVo.setSourceType(sourceType);
		inspectionQualityStandardVo.setIsAppPhoto(isAppPhoto);
		inspectionQualityStandardVo.setIsRepair(isRepair);
		inspectionQualityStandardVo.setIsInspection(isInspection);
	}

	public void inspectionQualityStandardMaintenanceShared(
		InspectionQualityStandardVo inspectionQualityStandardVo, Integer type, String location, String activityItem, String precautions, String utilAndSkill, String JudgementStandard) {
		inspectionQualityStandardVo.setType(type);
		inspectionQualityStandardVo.setLocation(location);
		inspectionQualityStandardVo.setActivityItem(activityItem);
		inspectionQualityStandardVo.setPrecautions(precautions);
		inspectionQualityStandardVo.setUtilAndSkill(utilAndSkill);
		inspectionQualityStandardVo.setJudgementStandard(JudgementStandard);
	}

	public void inspectionQualityStandardDevShared(InspectionQualityStandardVo inspectionQualityStandardVo, Long devId, String devName) {
		inspectionQualityStandardVo.setDev(devId);
		inspectionQualityStandardVo.setDevName(devName);
	}

	public String progressTrack(Date createTime, Long remindTime) {
		try {
			List<ProgressTrackVo> progressTrackVos = new ArrayList<>();
			IntStream.range(0, 4).forEach((process) -> {
				ProgressTrackVo progressTrackVo = new ProgressTrackVo();
				progressTrackVo.setProcess(process);
				if (process == 0) {
					progressTrackVo.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createTime));
					progressTrackVo.setStatus(1);
				}
				if (process == 1) {
					progressTrackVo.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(remindTime)));
					progressTrackVo.setStatus(1);
				}
				if (process > 1) {
					progressTrackVo.setStatus(0);
					progressTrackVo.setTime("");
				}
				progressTrackVos.add(progressTrackVo);
			});
			return JSONObject.toJSONString(progressTrackVos);
		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}
	}

	public void timeManagement(NewsPush newsPush, InspectionTask inspectionRecord, Inspection inspection, Maintain maintain) {
		Integer pushWorkStatus = newsPush.getWorkStatus();
		Date pushWorkTime = newsPush.getWorkTime();
		AtomicReference<Date> workTime = new AtomicReference<>();
		if (Func.isNotEmpty(inspection)) {
			workTime.set(inspection.getNextTime());
		}
		if (Func.isNotEmpty(maintain)) {
			workTime.set(maintain.getNextTime());
		}
		if (pushWorkStatus == 3) {
			inspectionRecord.setTaskTime(pushWorkTime);
		} else {
			inspectionRecord.setTaskTime(workTime.get());
		}
	}
}
