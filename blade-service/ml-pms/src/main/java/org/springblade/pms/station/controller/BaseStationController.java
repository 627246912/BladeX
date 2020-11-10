/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package org.springblade.pms.station.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Synchronized;
import org.springblade.bean.DeviceItem;
import org.springblade.bean.DeviceItemRealTimeData;
import org.springblade.bean.DeviceSub;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.enums.ProductSid;
import org.springblade.pms.bigscreen.handler.ShowTypeAndRealTimeValue;
import org.springblade.pms.enums.UserGroup;
import org.springblade.pms.factory.ModulToDataCenter;
import org.springblade.pms.gw.feign.IDataItemClient;
import org.springblade.pms.gw.feign.IDeviceClient;
import org.springblade.pms.gw.feign.ITopicClient;
import org.springblade.pms.station.dto.BaseStationDTO;
import org.springblade.pms.station.dto.RtuSetReq;
import org.springblade.pms.station.dto.RtuSetResp;
import org.springblade.pms.station.dto.StationExcel;
import org.springblade.pms.station.entity.BaseStation;
import org.springblade.pms.station.entity.RtuSet;
import org.springblade.pms.station.entity.SysArea;
import org.springblade.pms.station.entity.TimeControl;
import org.springblade.pms.station.service.IBaseStationService;
import org.springblade.pms.station.service.IRtuSetService;
import org.springblade.pms.station.service.ISysAreaService;
import org.springblade.pms.station.service.ITimeControlService;
import org.springblade.pms.station.vo.BaseStationVO;
import org.springblade.pms.station.wrapper.BaseStationWrapper;
import org.springblade.util.excel.util.ExcelUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 *  控制器
 *
 * @author bond
 * @since 2020-08-18
 */
@RestController
@AllArgsConstructor
@RequestMapping("/basestation")
@Api(value = "站点", tags = "站点管理接口")
public class BaseStationController extends BladeController {
	private IBaseStationService baseStationService;
	private IRtuSetService iGwcomSetService;
	@Autowired
	private IDataItemClient iDataItemClient;
	private IDeviceClient iDeviceClient;
	@Autowired
	private YaoKControllerFactory yaoKControllerFactory;
	private ITimeControlService iTimeControlService;
	private ShowTypeAndRealTimeValue showTypeAndRealTimeValue;
	private ITopicClient iTopicClient;
	private ISysAreaService iSysAreaService;

	private ModulToDataCenter modulToDataCenter;

	@ApiLog("配置端新增站点基本信息")
	@PostMapping("/save")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "新增站点基本信息", notes = "传入baseStation")
	public R save(@Valid @RequestBody BaseStation baseStation) {
		if(Func.isNotEmpty(baseStation.getProvinceCode())){
			SysArea province= iSysAreaService.getSysArea(baseStation.getProvinceCode());
			if(Func.isNotEmpty(province)){
				baseStation.setProvinceName(province.getAreaName());
			}
		}
		if(Func.isNotEmpty(baseStation.getCityCode())){
			SysArea city= iSysAreaService.getSysArea(baseStation.getProvinceCode());
			if(Func.isNotEmpty(city)){
				baseStation.setCityName(city.getAreaName());
			}
		}
		if(Func.isNotEmpty(baseStation.getCountyCode())){
			SysArea county= iSysAreaService.getSysArea(baseStation.getCountyCode());
			if(Func.isNotEmpty(county)){
				baseStation.setCountyName(county.getAreaName());
			}
		}
		boolean	r=baseStationService.save(baseStation);
		return R.status(r);
	}



	@GetMapping("/page")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "站点配置列表", notes = "传入baseStation")
	public R<IPage<BaseStationDTO>> page(BaseStationVO baseStation, Query query) {
//		String tenantId= AuthUtil.getTenantId();
//		//tenantId="pmsdemo";
//		List<Device> deviceLits=iDeviceClient.getDevice(tenantId);
//		List<String> gwids= new ArrayList<>();
//		for (Device device:deviceLits){
//			gwids.add(device.getCode());
//		}
//
//		iTopicClient.addDeviceTopic(gwids);
//		List<String> deviceLits1=new ArrayList<>();
//		if(Func.isNotEmpty(deviceLits)) {
//			for (Device device : deviceLits) {
//				deviceLits1.add(device.getCode());
//			}
//		}
//		List<BaseStation> gwinfoList=baseStationService.list();
//		List<String> deviceLits2=new ArrayList<>();
//		for(BaseStation station:gwinfoList){
//			deviceLits2.add(station.getGwId());
//		}
//		// 差集 (list1 - list2)
//		List<String> reduce1 = deviceLits1.stream().filter(item -> !deviceLits2.contains(item)).collect(toList());
//
//		List<BaseStation> insertGwinfos=new ArrayList<>();
//		if(Func.isNotEmpty(reduce1)){
//			for(String gwId:reduce1) {
//				BaseStation gwinfo=new BaseStation();
//				gwinfo.setGwId(gwId);
//				gwinfo.setCreateTime(new Date());
//				insertGwinfos.add(gwinfo);
//			}
//			baseStationService.saveBatch(insertGwinfos);
//		}

		IPage<BaseStationDTO> pages = baseStationService.selectBaseStationPage(Condition.getPage(query), baseStation);
		return R.data(pages);
	}

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "站点基本信息", notes = "传入baseStation")
	public R<BaseStationVO> detail(BaseStation baseStation) {
		BaseStation detail = baseStationService.getOne(Condition.getQueryWrapper(baseStation));
		return R.data(BaseStationWrapper.build().entityVO(detail));
	}

	/**
	 * 修改
	 */
	@ApiLog("配置端修改站点基本信息")
	@PostMapping("/update")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "站点基本信息修改", notes = "传入baseStation")
	public R update(@Valid @RequestBody BaseStation baseStation) {

		if(Func.isNotEmpty(baseStation.getProvinceCode())){
			SysArea province= iSysAreaService.getSysArea(baseStation.getProvinceCode());
			if(Func.isNotEmpty(province)){
				baseStation.setProvinceName(province.getAreaName());
			}
		}
		if(Func.isNotEmpty(baseStation.getCityCode())){
			SysArea city= iSysAreaService.getSysArea(baseStation.getCityCode());
			if(Func.isNotEmpty(city)){
				baseStation.setCityName(city.getAreaName());
			}
		}
		if(Func.isNotEmpty(baseStation.getCountyCode())){
			SysArea county= iSysAreaService.getSysArea(baseStation.getCountyCode());
			if(Func.isNotEmpty(county)){
				baseStation.setCountyName(county.getAreaName());
			}
		}

		BaseStation have=baseStationService.getById(baseStation.getId());
		boolean r=false;
		if(Func.isNotEmpty(have)){
			r= baseStationService.updateById(baseStation);
		}else{
			r=baseStationService.save(baseStation);
			//配置产品数据项

		}
		if(Func.isNotEmpty(baseStation.getGwId())) {
			mateProducrModul(baseStation.getGwId());
		}
		return R.status(r);
	}

	@ApiLog("配置端站点网关同步产品模型")
	@PostMapping("/mateProducrModul")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "站点网关同步产品模型", notes = "传入gwid")
	public R mateProducrModul(String gwId) {

		if(Func.isNotEmpty(gwId)) {
			iTopicClient.addTopic(gwId);
			//发送数据中心初始化产品模型数据
			modulToDataCenter.setProductDataItem(gwId);
		}
		return R.success("同步成功");
	}

	/**
	 * excel批量上传站点
	 * @param excel
	 * @return
	 */
	@PostMapping("/uploadStation")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "excel批量上传站点", notes = "")
	public R uploadStation(@RequestParam MultipartFile excel) {

		List<StationExcel> list= new ArrayList<>();
		list=ExcelUtils.read(excel,StationExcel.class);

		List<BaseStation> baseStationList= new ArrayList<>();
		for(StationExcel stationExcel:list){
			BaseStation baseStation=new BaseStation();
			BeanUtils.copyProperties(stationExcel,baseStation);
			baseStationList.add(baseStation);
		}
		boolean r= baseStationService.saveOrUpdateBatch(baseStationList);
		return R.data(r);
	}

//	@GetMapping("/downloadTemplate")
//	@ApiOperationSupport(order = 3)
//	@ApiOperation(value = "下载上传站点模版", notes = "")
//	public void downloadTemplateExport(HttpServletResponse response) {
//
//		List<StationExcel> list= new ArrayList<>();
//		String time= DateUtil.format(new Date(),DateUtil.TIME_NUMBER_PATTERN);
//		ExcelUtils.export(response,"站点上传模板"+time,"模板",list,StationExcel.class);
//	}


		@GetMapping("/getRtuInfo")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "站点端口配置", notes = "传入gwId")
	public R<List<RtuSetResp>> getRtuInfo(String gwId) {
		if(Func.isEmpty(gwId)){
			return R.fail("网关id不能为空");
		}
		String tenantId= AuthUtil.getTenantId();
		//tenantId="pmsdemo";
		List<DeviceSub> deviceSubLits=iDeviceClient.getDeviceSubsByGwid(gwId);
		Map<String,DeviceSub> deviceSubsMap=new HashMap<>();
		List<String> rtuLits1=new ArrayList<>();
		for(DeviceSub deviceSub:deviceSubLits){
			//rtu1为总路不分配给用户
			if(!Func.equals("1",deviceSub.getRtuid())) {
				rtuLits1.add(deviceSub.getRtuidcb());
				deviceSubsMap.put(deviceSub.getRtuidcb(), deviceSub);
			}
		}

		Map<String,Object> map=new HashMap<>();
		map.put("gw_id",gwId);
		List<RtuSet> GwcomList=iGwcomSetService.listByMap(map);
		List<String> rtuLits2=new ArrayList<>();
		for(RtuSet gwcomSet:GwcomList){
			rtuLits2.add(gwcomSet.getRtuidcb());
		}
		// 差集 (list1 - list2)
		List<String> reduce1 = rtuLits1.stream().filter(item -> !rtuLits2.contains(item)).collect(toList());

		List<RtuSet> insertGwcomSets=new ArrayList<>();
		if(Func.isNotEmpty(reduce1)){
			for(String rtuidcb:reduce1) {
				RtuSet gwcomSet=new RtuSet();

				List<DeviceItem> gwItemList = iDeviceClient.getDeviceItemInfosByRtuidcb(rtuidcb);
				List<String> itemids = new ArrayList<>();
				for (DeviceItem ditem : gwItemList) {
					if (Func.equals(String.valueOf(ditem.getSid()), ProductSid.SID1521.id)) {
						itemids.add(ditem.getId());
						Map<String, DeviceItemRealTimeData> deviceItemRealTimeDatas = showTypeAndRealTimeValue.getDeviceItemRealTimeDatas(itemids);
						if(Func.isNotEmpty(deviceItemRealTimeDatas)) {
							if(Func.isNotEmpty(deviceItemRealTimeDatas.get(ditem.getId()))){
								Double userId= deviceItemRealTimeDatas.get(ditem.getId()).getVal();
								gwcomSet.setUserGroup(Func.isEmpty(userId)?"0":String.valueOf(userId.intValue()));
							}

						}
					}
				}

				DeviceSub deviceSub=deviceSubsMap.get(rtuidcb);
				gwcomSet.setGwId(deviceSub.getGwid());
				gwcomSet.setRtuid(deviceSub.getRtuid());
				gwcomSet.setRtuidcb(deviceSub.getRtuidcb());
				gwcomSet.setRtuname(deviceSub.getRtuname());
				gwcomSet.setCreateTime(new Date());
				gwcomSet.setPort(Integer.valueOf(deviceSub.getRtuid())-1);

				insertGwcomSets.add(gwcomSet);
			}
			iGwcomSetService.saveBatch(insertGwcomSets);
		}
		Map<String,Object> querymap=new HashMap<>();
		querymap.put("gwId",gwId);
		List<RtuSet> list=iGwcomSetService.selectGwcomSetList(querymap);
		List<RtuSetResp> rtuSetRespList=new ArrayList<>();
		for(RtuSet rtuSet:list){
			RtuSetResp rtuSetResp=new RtuSetResp();
			BeanUtils.copyProperties(rtuSet,rtuSetResp);
			TimeControl entity=new TimeControl();
			entity.setRtuSetId(rtuSet.getId());
			List<TimeControl> timeControlList =iTimeControlService.list(Condition.getQueryWrapper(entity));
			rtuSetResp.setTimeControlList(timeControlList);
			rtuSetRespList.add(rtuSetResp);
		}
		return R.data(rtuSetRespList);
	}

	@ApiLog("配置端设置端口配置")
	@PostMapping("/updateRtuSet")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "站点端口配置修改", notes = "传入gwcomSets")
	@Synchronized
	public R updateRtuSet(@Valid @RequestBody List<RtuSetReq> gwcomSets) {
		if(Func.isEmpty(gwcomSets)){
			return R.fail("提交数据为空");
		}
		List<RtuSet> rtuSetList=new ArrayList<>();
		for(RtuSetReq dto: gwcomSets){
			RtuSet rtuSet =	iGwcomSetService.getById(dto.getId());
			rtuSet.setUserGroup(dto.getUserGroup());
			rtuSet.setRtuname(dto.getRtuname());
			rtuSet.setAlarmI(dto.getAlarmI());
			rtuSet.setDayMaxPower(dto.getDayMaxPower());
			rtuSet.setWarnI(dto.getWarnI());
			rtuSet.setRatedI(dto.getRatedI());
			rtuSet.setDischargeTimes(dto.getDischargeTimes());
			rtuSet.setDischargeVoltage(dto.getDischargeVoltage());
			if(Func.isNotEmpty(UserGroup.getValue(dto.getUserGroup()))){
				rtuSet.setActivateTime(new Date());
			}

			rtuSetList.add(rtuSet);
		}
		boolean r=false;
		r=iGwcomSetService.updateBatchById(rtuSetList);
		//如果成功则发送遥控指令
		if(r){
			yaoKControllerFactory.updateYKYXYCYT(rtuSetList);
		}
		return R.status(r);
	}
	@ApiLog("配置端遥控口开关")
	@PostMapping("/setSwitch/pms/{rtuidcb}")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "站点端口启用关闭接口", notes = "")
	public R updatePortStatus(@RequestParam(value = "id", required = true)String id,@RequestParam(value = "switchStatus", required = true)int switchStatus) {
		RtuSet rtu=iGwcomSetService.getById(id);
		if(Func.isEmpty(rtu)){
			return R.fail("id不存在");
		}
		boolean r=false;
		rtu.setSwitchStatus(switchStatus);
		//rtu.setActivateTime(new Date());
		r=iGwcomSetService.updateById(rtu);

		List<DeviceItem> gwItemList = iDeviceClient.getDeviceItemInfosByRtuidcb(rtu.getRtuidcb());
		for (DeviceItem ditem : gwItemList) {
			//判断开关//摇控
			if (Func.equals(String.valueOf(ditem.getSid()), ProductSid.SID2003.id)) {
				r=iDataItemClient.setYk(ditem.getId(), switchStatus);
			}
		}
		return R.status(r);
	}

	@ApiLog("配置端设置定时开关启用")
	@PostMapping("/updateTimeContorStatus/pms/{rtuidcb}")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "端口定时开关启用关闭接口", notes = "")
	public R updateTimeContorStatus(@RequestParam(value = "id", required = true)String id,@RequestParam(value = "timeContorStatus", required = true)int timeContorStatus) {
		return yaoKControllerFactory.updateTimeContorStatus(id,timeContorStatus);
	}

	@ApiLog("配置端设置遥控定时控制")
	@PostMapping("/submitTimeControl/pms/{rtuidcb}")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "提交端口定时控制", notes = "")
	public R submitTimeControl(@Valid @RequestBody List<TimeControl> timeControls) {
		return yaoKControllerFactory.submitTimeControl(timeControls);
	}
}
