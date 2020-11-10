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
package org.springblade.gw.feign;

import org.springblade.bean.*;
import org.springblade.gw.cache.DeviceCache;
import org.springblade.gw.repository.DeviceItemDataRepository;
import org.springblade.gw.restTemplate.DeviceItemRepository;
import org.springblade.gw.restTemplate.DeviceRepository;
import org.springblade.gw.restTemplate.DeviceSubRepository;
import org.springblade.pms.gw.feign.IDeviceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 网关服务Feign实现类
 *
 * @author bond
 */
@RestController
public class DeviceClient implements IDeviceClient {
	@Autowired
	private DeviceRepository deviceRepository;
	@Autowired
	private DeviceSubRepository deviceSubRepository;
	@Autowired
	private DeviceItemRepository deviceItemRepository;
	@Autowired
	private DeviceItemDataRepository deviceItemDataRepository;
	@Autowired
	private DeviceCache deviceCache;



	@Override
	@GetMapping(DEVICEBYTENANTID)
	public List<Device> getDevice(String tenantId) {
		return deviceRepository.getDevices(tenantId,null,null);
	}
	@GetMapping(DEVICEBYSTATIONID)
	public List<Device> getDeviceByStationId(@RequestParam("tenantId") String tenantId, @RequestParam("stationId") String stationId){
		return deviceRepository.getDevices(tenantId,stationId,null);
	}
	@Override
	@PostMapping(DEVICEBYGWIDS)
	public List<Device> getDeviceBygwids(@RequestBody List<String> gwids) {
		return deviceRepository.getDevices(null,null,gwids);
	}

	@Override
	@GetMapping(DEVICEBYGWID)
	public Device getDeviceBygwid(String gwid) {
		return deviceRepository.getDevice(gwid);
	}

	@Override
	@GetMapping(GETDEVICESUBSBYTENANTID)
	public List<DeviceSub> getDeviceSubs(String tenantId) {
		return deviceSubRepository.getDeviceSubs(tenantId,null,null,null);
	}

	@Override
	@PostMapping(GETDEVICESUBSBYGWIDS)
	public List<DeviceSub> getDeviceSubsByGwids(@RequestBody List<String> gwids) {
		return deviceSubRepository.getDeviceSubs(null,null,gwids,null);
	}

	@Override
	@GetMapping(GETDEVICESUBSBYGWID)
	public List<DeviceSub> getDeviceSubsByGwid(String gwid) {
		return deviceSubRepository.getDeviceSubsByGwId(gwid);
	}

	@Override
	@PostMapping(GETDEVICESUBSBYRTUIDCBS)
	public List<DeviceSub> getDeviceSubsByRtuidcbs(@RequestBody List<String> rtuidcbs) {
		return deviceSubRepository.getDeviceSubs(null,null,null,rtuidcbs);
	}

	@Override
	@GetMapping(GETDEVICESUBSBYRTUIDCB)
	public DeviceSub getDeviceSubsByRtuidcb(String rtuidcb) {
		return deviceSubRepository.getDeviceSubsByRtuId(rtuidcb);
	}


	@Override
	@GetMapping(GETDEVICEITEMINFOSBYTENANTID)
	public List<DeviceItem> getDeviceItemInfosByTenantId(String tenantId) {
		return deviceItemRepository.getItemInfos(tenantId,null,null,null,null);
	}

	@Override
	@PostMapping(GETDEVICEITEMINFOSBYGWIDS)
	public List<DeviceItem> getDeviceItemInfosByGwids(@RequestBody List<String> gwids) {
		return deviceItemRepository.getItemInfos(null,null,gwids,null,null);
	}

	@Override
	@PostMapping(GETDEVICEITEMINFOSBYRTUIDCBS)
	public List<DeviceItem> getDeviceItemInfosByRtuidcbs(@RequestBody List<String> rtuidcbs) {
		return deviceItemRepository.getItemInfos(null,null,null,rtuidcbs,null);
	}
	@GetMapping(GETDEVICEITEMINFOSBYRTUIDCB)
	public List<DeviceItem> getDeviceItemInfosByRtuidcb(String rtuidcb){
		List<String> rtuidcbs =new ArrayList<>();
		rtuidcbs.add(rtuidcb);
		return deviceItemRepository.getItemInfos(null,null,null,rtuidcbs,null);
	}

	@PostMapping(GETDEVICEITEMINFOSBYITEMIDS)
	public Map<String,DeviceItem> getDeviceItemInfosByItemids(@RequestBody List<String> itemids){
		return deviceItemDataRepository.updateId2ItemInfo(deviceItemRepository.getItemInfos(null,null,null,null,itemids));
	}

	@Override
	@GetMapping(GETDEVICEITEMINFOSBYITEMID)
	public DeviceItem getDeviceItemInfosByItemid(String itemid) {
		return deviceItemRepository.getItemInfosByItemid(itemid);
	}

	@PostMapping("/getDeviceItemRealTimeDataByCodes")
	public Map<String, List<DeviceItemRealTimeData>> getDeviceItemRealTimeDataByCodes(@RequestBody List<String> gwids){
		return deviceItemDataRepository.getDeviceItemRealTimeDataByCodes(gwids);
	}

	@PostMapping("/setjfpgDate")
	public boolean setjfpgDate(@RequestBody List<DevieceJFPGDate> devieceJFPGDates){
		return deviceRepository.setjfpgDate(devieceJFPGDates);
	}

	/**
	 * 根据 code列表 获取网关状态
	 * @param codes
	 * @return
	 */
	@Override
	@PostMapping("/getStatussByCodes")
	public Map<String,DeviceCommunicationStatus> getStatussByCodes(@RequestBody List<String> codes){
		return deviceCache.getStatussByCodes(codes);
	}

	/**
	 * 根据 code列表 获取网关状态
	 * @param code
	 * @return
	 */
	@Override
	@GetMapping("/getStatussByCode")
	public DeviceCommunicationStatus getStatussByCode(String code){
		return  deviceCache.getStatusByCode(code);
	}
}
