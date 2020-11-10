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
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 网关 Feign接口类
 *
 * @author bond
 */
@FeignClient(
	value = "ml-gw-server"
)
public interface IDeviceClient {

	String DEVICEBYTENANTID="get/deviceByTenantId";
	String DEVICEBYSTATIONID ="get/deviceByStationId";
	String DEVICEBYGWIDS="get/deviceBygwids";
	String DEVICEBYGWID="get/deviceBygwid";

	String GETDEVICESUBSBYTENANTID="get/getDeviceSubsByTenantId";
	String GETDEVICESUBSBYGWIDS="get/getDeviceSubsByGwids";
	String GETDEVICESUBSBYGWID="get/getDeviceSubsByGwid";
	String GETDEVICESUBSBYRTUIDCBS="get/getDeviceSubsByRtuidcbs";
	String GETDEVICESUBSBYRTUIDCB="get/getDeviceSubsByRtuidcb";

	String GETDEVICEITEMINFOSBYTENANTID="get/getDeviceItemInfosByTenantId";
	String GETDEVICEITEMINFOSBYGWIDS="get/getDeviceItemInfosByGwids";
	String GETDEVICEITEMINFOSBYRTUIDCBS="get/getDeviceItemInfosByRtuidcbs";
	String GETDEVICEITEMINFOSBYRTUIDCB="get/getDeviceItemInfosByRtuidcb";
	String GETDEVICEITEMINFOSBYITEMIDS="get/getDeviceItemInfosByItemids";
	String GETDEVICEITEMINFOSBYITEMID="get/getDeviceItemInfosByItemid";
	/**
	 * 网关信息读取
	 * @return
	 */
	@GetMapping(DEVICEBYTENANTID)
	List<Device> getDevice(@RequestParam("tenantId") String tenantId);

	@GetMapping(DEVICEBYSTATIONID)
	List<Device> getDeviceByStationId(@RequestParam("tenantId") String tenantId,@RequestParam("stationId") String stationId);

	@PostMapping(DEVICEBYGWIDS)
	List<Device> getDeviceBygwids(@RequestBody List<String> gwids);

	@GetMapping(DEVICEBYGWID)
	Device getDeviceBygwid(@RequestParam("gwid") String gwid);
	/**
	 * 网关rtuid信息读取
	 * @return
	 */
	@GetMapping(GETDEVICESUBSBYTENANTID)
	List<DeviceSub> getDeviceSubs(@RequestParam("tenantId") String tenantId);

	@PostMapping(GETDEVICESUBSBYGWIDS)
	List<DeviceSub> getDeviceSubsByGwids(@RequestBody List<String> gwids);

	@GetMapping(GETDEVICESUBSBYGWID)
	List<DeviceSub> getDeviceSubsByGwid(@RequestParam("gwids") String gwids);

	@GetMapping(GETDEVICESUBSBYRTUIDCBS)
	List<DeviceSub> getDeviceSubsByRtuidcbs(@RequestParam("rtuidcbs") List<String> rtuidcbs);

	@GetMapping(GETDEVICESUBSBYRTUIDCB)
	DeviceSub getDeviceSubsByRtuidcb(@RequestParam("rtuidcb")String rtuidcb);



	/**
	 * 获取数据项数据
	 * @param tenantId 客户id
	 * @return
	 */
	@GetMapping(GETDEVICEITEMINFOSBYTENANTID)
	List<DeviceItem> getDeviceItemInfosByTenantId(@RequestParam("tenantId") String tenantId);

	@PostMapping(GETDEVICEITEMINFOSBYGWIDS)
	List<DeviceItem> getDeviceItemInfosByGwids(@RequestBody List<String> gwids);

	@PostMapping(GETDEVICEITEMINFOSBYRTUIDCBS)
	List<DeviceItem> getDeviceItemInfosByRtuidcbs(@RequestBody List<String> rtuidcbs);

	@GetMapping(GETDEVICEITEMINFOSBYRTUIDCB)
	List<DeviceItem> getDeviceItemInfosByRtuidcb(@RequestParam("rtuidcb") String rtuidcb);

	@PostMapping(GETDEVICEITEMINFOSBYITEMIDS)
	Map<String,DeviceItem> getDeviceItemInfosByItemids(@RequestBody List<String> itemids);

	@GetMapping(GETDEVICEITEMINFOSBYITEMID)
	DeviceItem getDeviceItemInfosByItemid(@RequestParam("itemid") String itemid);

	@PostMapping("/getDeviceItemRealTimeDataByCodes")
	Map<String, List<DeviceItemRealTimeData>> getDeviceItemRealTimeDataByCodes(@RequestBody List<String> gwids);

	@PostMapping("/setjfpgDate")
	 boolean setjfpgDate(@RequestBody List<DevieceJFPGDate> devieceJFPGDates);

	}
