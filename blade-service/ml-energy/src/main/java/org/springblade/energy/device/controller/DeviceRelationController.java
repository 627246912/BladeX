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
package org.springblade.energy.device.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.bean.Device;
import org.springblade.bean.DeviceItem;
import org.springblade.bean.DeviceSub;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.dto.DidComRtuReq;
import org.springblade.gw.feign.IDeviceClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 站点网关表 控制器
 *
 * @author bond
 * @since 2020-03-16
 */
@RestController
@AllArgsConstructor
@RequestMapping("/devicerelation")
@Api(value = "站点网关表", tags = "站点网关表接口")
public class DeviceRelationController extends BladeController {

	private IDeviceClient deviceClient;
	/**
	 * 查询绑定网关列表
	 * feign 远程调用ml-gw-server服务接口
	 */
	@GetMapping("/getDeviceBySid")
	@ApiOperationSupport(order = 0)
	@ApiOperation(value = "根据站点查询网关列表", notes = "stationId")
	public R<List<Device>>getDeviceBySid(String stationId) {
		String tenantId= AuthUtil.getTenantId();
		//String tenantId="mailian";
		//List<Device> list=deviceClient.getDeviceByStationId(tenantId,stationId);
		return R.data(deviceClient.getDeviceByStationId(tenantId,stationId));
	}

	@GetMapping("/getDevice")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "查询全部网关列表")
	public R<List<Device>>getDevice() {
		String tenantId=AuthUtil.getTenantId();
		//String tenantId="mailian";
		return R.data(deviceClient.getDevice(tenantId));
	}

/*	@GetMapping("/getDeviceByGwids")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "查询网关信息", notes = "gwid")
	public R<Device>getDeviceByGwids(String gwid) {
		//String gwid="1807139EDF57";
		return R.data(deviceClient.getDeviceBygwid(gwid));
	}*/

	@GetMapping("/getDeviceSubsByGwid")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "根据网关查询RTU信息", notes = "gwids")
	public R<List<DidComRtuReq>>getDeviceSubsByGwids(String gwids) {
		List<String> dids= Arrays.asList(gwids.split(","));
		List<DeviceSub> deviceSubList =deviceClient.getDeviceSubsByGwids(dids);

		List<DidComRtuReq> didComRtuReqList=new ArrayList<>();
		Map<Integer,List<DeviceSub>> comRtuMap=new HashMap<>();
		List<DeviceSub> deviceSubs =new ArrayList<>();
		for(String gwid:dids){
			DidComRtuReq didComRtuReq=new DidComRtuReq();
			didComRtuReq.setGwid(gwid);
			comRtuMap=new HashMap<>();
			for(DeviceSub deviceSub:deviceSubList){
				if(Func.equals(gwid,deviceSub.getGwid())){
					Integer comid=deviceSub.getComid();
					if(comRtuMap.containsKey(comid)){
						comRtuMap.get(comid).add(deviceSub);
					}else{
						deviceSubs = new ArrayList<>();
						deviceSubs.add(deviceSub);
						comRtuMap.put(comid,deviceSubs);
					}
				}
			}
			didComRtuReq.setComRtus(comRtuMap);
			didComRtuReqList.add(didComRtuReq);
		}

		return R.data(didComRtuReqList);
	}

	@GetMapping("/getDeviceSubsByRtuidcb")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "根据RTUID查询信息", notes = "rtuidcb")
	public R<DeviceSub>getDeviceSubsByRtuidcb(String rtuidcb) {
		//String gwid="1807139EDF57";
		return R.data(deviceClient.getDeviceSubsByRtuidcb(rtuidcb));
	}

	@GetMapping("/getDeviceItemInfosByRtuidcb")
	public List<DeviceItem> getDeviceItemInfosByRtuidcb(String rtuidcb) {
		List<String> rtuidcbs =new ArrayList<>();
		rtuidcbs.add(rtuidcb);
		return deviceClient.getDeviceItemInfosByRtuidcbs(rtuidcbs);
	}

	@GetMapping("/getDeviceItemInfosByRtuidcbs")
	public List<DeviceItem> getDeviceItemInfosByRtuidcbs(List<String> rtuidcbs) {
		return deviceClient.getDeviceItemInfosByRtuidcbs(rtuidcbs);
	}


	@GetMapping("/getDeviceItemInfosByItemids")
	public Map<String,DeviceItem> getDeviceItemInfosByItemids(List<String> items) {
		return deviceClient.getDeviceItemInfosByItemids(items);
	}
}
