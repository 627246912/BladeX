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
package org.springblade.energy.runningmanagement.station.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.bean.Device;
import org.springblade.bean.DevieceJFPGDate;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.secure.utils.SecureUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.device.entity.DeviceRelation;
import org.springblade.energy.runningmanagement.station.entity.Station;
import org.springblade.energy.runningmanagement.station.service.ISiteService;
import org.springblade.energy.runningmanagement.station.service.IStationService;
import org.springblade.energy.runningmanagement.station.vo.StationVO;
import org.springblade.energy.runningmanagement.station.wrapper.StationWrapper;
import org.springblade.gw.feign.IDeviceClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 站点信息表 控制器
 *
 * @author bond
 * @since 2020-03-16
 */
@RestController
@AllArgsConstructor
@RequestMapping("/station")
@Api(value = "站点信息表", tags = "站点信息表接口")
public class StationController extends BladeController {

	private IStationService stationService;
	private ISiteService siteService;
	private IDeviceClient iDeviceClient;


	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入station")
	public R<StationVO> detail(Station station) {
		Station detail = stationService.getOne(Condition.getQueryWrapper(station));
		StationVO stationVO=StationWrapper.build().entityVO(detail);

//		Site site=new Site();
//		site.setSid(detail.getId());
//		site.setParentId(0L);
//		Site siteOne=siteService.getOne(Condition.getQueryWrapper(site));
//		if(siteOne!=null) {
//			stationVO.setSiteName(siteOne.getSiteName());
//		}
		return R.data(stationVO);
	}

	/**
	 * 分页 站点信息表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入station")
	public R<IPage<StationVO>> list(Station station, Query query) {
		IPage<Station> pages = stationService.page(Condition.getPage(query), Condition.getQueryWrapper(station));
		return R.data(StationWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 站点信息表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入station")
	public R<IPage<StationVO>> page(StationVO station, Query query) {
		IPage<StationVO> pages = stationService.selectStationPage(Condition.getPage(query), station);
//		List<StationVO> list=pages.getRecords();
//		for (StationVO stationVO :list){
//			Site site=new Site();
//			site.setSid(stationVO.getId());
//			site.setParentId(0L);
//			Site siteOne=siteService.getOne(Condition.getQueryWrapper(site));
//			if(siteOne!=null) {
//				stationVO.setSiteName(siteOne.getSiteName());
//			}
//		}

		return R.data(pages);
	}

	/**
	 * 新增 站点信息表
	 */
	@ApiLog("站点新增")
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入station")
	public R save(@Valid @RequestBody Station station) {
		return R.status(stationService.save(station));
	}

	/**
	 * 修改 站点信息表
	 */
	@ApiLog("站点修改")
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入station")
	public R update(@Valid @RequestBody Station station) {
		boolean res=stationService.updateById(station);
		if(res){
			String tenantId= AuthUtil.getTenantId();
			List<Device> deviceIds = iDeviceClient.getDeviceByStationId(tenantId,String.valueOf(station.getId()));
			//站点有所属网关，并且尖峰平谷时间段有所改变
			if(Func.isNotEmpty(deviceIds) && (!Objects.equals(station.getFlat(),station.getFlat())
				|| !Objects.equals(station.getValley(),station.getValley())
				|| !Objects.equals(station.getTop(),station.getTop())
				|| !Objects.equals(station.getPeak(),station.getPeak()))){
				List<DevieceJFPGDate> devieceJFPGDates = new ArrayList<>();
				DevieceJFPGDate devieceJFPGDate;
				for(Device device:deviceIds){
					devieceJFPGDate = new DevieceJFPGDate();
					devieceJFPGDate.setGwid(device.getCode());
					devieceJFPGDate.setTop(station.getTop());
					devieceJFPGDate.setValley(station.getValley());
					devieceJFPGDate.setPeak(station.getPeak());
					devieceJFPGDate.setFlat(station.getFlat());
					devieceJFPGDates.add(devieceJFPGDate);
				}
				iDeviceClient.setjfpgDate(devieceJFPGDates);
			}
		}

		return R.status(res);
	}

	/**
	 * 新增或修改 站点信息表
	 */
	@ApiLog("站点新增修改")
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入station")
	public R submit(@Valid @RequestBody Station station) {
		Station stationDb = stationService.getById(station.getId());
		boolean res=stationService.saveOrUpdate(station);
		if(res){
			String tenantId= AuthUtil.getTenantId();
			List<Device> deviceIds = iDeviceClient.getDeviceByStationId(tenantId,String.valueOf(station.getId()));
			//站点有所属网关，并且尖峰平谷时间段有所改变
			if(Func.isNotEmpty(deviceIds) && (!Func.equals(station.getFlat(),stationDb.getFlat())
				|| !Objects.equals(station.getValley(),stationDb.getValley())
				|| !Objects.equals(station.getTop(),stationDb.getTop())
				|| !Objects.equals(station.getPeak(),stationDb.getPeak()))){
				List<DevieceJFPGDate> devieceJFPGDates = new ArrayList<>();
				DevieceJFPGDate devieceJFPGDate;
				for(Device device:deviceIds){
					devieceJFPGDate = new DevieceJFPGDate();
					devieceJFPGDate.setGwid(device.getCode());
					devieceJFPGDate.setTop(station.getTop());
					devieceJFPGDate.setValley(station.getValley());
					devieceJFPGDate.setPeak(station.getPeak());
					devieceJFPGDate.setFlat(station.getFlat());
					devieceJFPGDates.add(devieceJFPGDate);
				}
				iDeviceClient.setjfpgDate(devieceJFPGDates);
			}
		}

		return R.status(res);
	}


	/**
	 * 删除 站点信息表
	 */
	@ApiLog("删除站点")
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(stationService.deleteLogic(Func.toLongList(ids)));
	}


	/**
	 * 关联网关
	 */
	@GetMapping("/bindDevices")
	@ApiOperationSupport(order = 0)
	@ApiOperation(value = "关联网关", notes = "传入deviceRelation")
	public R<List<Device>>bindDevices(DeviceRelation deviceRelation) {
		if(deviceRelation.getStationId()==null){
			throw new Error("站点ID为空");
		}
		String tenantId = SecureUtil.getTenantId();
		List<Device> list=null;
		//List<Device> list= deviceController.getDevicesByCodes(null, Config.,tenantId);
		return R.data(list);
	}

}
