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
import org.springblade.dto.*;
import org.springblade.gw.restTemplate.DeviceItemRepository;
import org.springblade.pms.gw.feign.IDataItemClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 网关服务Feign实现类
 *
 * @author bond
 */
@RestController
public class DataItemClient implements IDataItemClient {


	@Autowired
	private DeviceItemRepository deviceItemRepository;

	/**
	 * 设置遥控值
	 * @param itemId
	 * @param val
	 * @return
	 */
	@PostMapping("/setYK")
	public Boolean setYk(@RequestParam String itemId, @RequestParam Integer val){
		return deviceItemRepository.setYk(itemId,val);
	}
	/**
	 * 修改遥测传输参数
	 * @param ycTranReq
	 * @return
	 */
	@PostMapping("/setYcTranInfos")
	public boolean setYcTranInfos(@RequestBody YcTranReq ycTranReq){
		List<YcTran> ycTranInfos=ycTranReq.getYcTranInfos();
		String rtuIds=ycTranReq.getRtuIds();
		return deviceItemRepository.setYcTranInfos(ycTranInfos,rtuIds);
	}
	/**
	 * 修改遥信传输参数
	 * @param yxTranReq
	 * @return
	 */
	@PostMapping("/setYxTranInfos")
	public boolean setYxTranInfos(@RequestBody YxTranReq yxTranReq){
		List<YxTran> yxTranInfos=yxTranReq.getYxTranInfos();
		String rtuIds=yxTranReq.getRtuIds();
		return deviceItemRepository.setYxTranInfos(yxTranInfos,rtuIds);
	}
	/**
	 * 修改遥控传输参数
	 * @param ykTranReq
	 * @return
	 */
	@PostMapping("/setYkTranInfos")
	public boolean setYkTranInfos(@RequestBody YkTranReq ykTranReq){
		List<YkTran> ykTranInfos=ykTranReq.getYkTranInfos();
		String rtuIds=ykTranReq.getRtuIds();
		return deviceItemRepository.setYkTranInfos(ykTranInfos,rtuIds);
	}


	/**
	 * 设置遥测传输参数
	 * @param ycStoreReq
	 * @return
	 */
	@PostMapping("/setYcStoreInfos")
	public Boolean setYcStoreInfos(@RequestBody YcStoreReq ycStoreReq){
		List<YcStore> ycStores=ycStoreReq.getYcStore();
		List<String> gwIds=ycStoreReq.getGwIds();
		List<String> rtuIds=ycStoreReq.getRtuIds();
		return deviceItemRepository.setYcStoreInfos(ycStores,gwIds,rtuIds);
	}

	/**
	 * 遥测告警参数设置
	 * @param ycAlarmConfigReq
	 * @return
	 */
	@PostMapping("/setYcAlarmConfig")
	public Boolean setYcAlarmConfig(@RequestBody YcAlarmConfigReq ycAlarmConfigReq){
		List<YcAlarmConfig> ycAlarmConfigs=ycAlarmConfigReq.getYcAlarmConfig();
		List<String> gwIds=ycAlarmConfigReq.getGwIds();
		List<String> rtuIds=ycAlarmConfigReq.getRtuIds();
		return deviceItemRepository.setYcAlarmConfig(ycAlarmConfigs,gwIds,rtuIds);
	}

	/**
	 * 遥信告警参数设置
	 * @param yxAlarmConfigReq
	 * @return
	 */
	@PostMapping("/setYxAlarmConfig")
	public 	Boolean setYxAlarmConfig(@RequestBody YxAlarmConfigReq yxAlarmConfigReq){
		List<YxAlarmConfig> yxAlarmConfigs=yxAlarmConfigReq.getYxAlarmConfig();
		List<String> gwIds=yxAlarmConfigReq.getGwIds();
		List<String> rtuIds=yxAlarmConfigReq.getRtuIds();
		return deviceItemRepository.setYxAlarmConfig(yxAlarmConfigs,gwIds,rtuIds);
	}

	/**
	 * add遥信/遥测运算库数据项
	 * @param deviceItem
	 * @return
	 */
	@PostMapping("/addCalcItem")
	public Boolean addCalcItem(@RequestBody DeviceItem deviceItem){
		return deviceItemRepository.addCalcItem(deviceItem,deviceItem.getGwid());
	}

	/**
	 * 修改遥测运算库数据项
	 * @param ycCalcReq
	 * @return
	 */
	@PostMapping("/setYcCalcItem")
	public Boolean setYcCalcItem(@RequestBody YcCalcReq ycCalcReq){
		List<YcCalc> ycCalcs=ycCalcReq.getYcCalcs();
		String deviceCodes=ycCalcReq.getDeviceCodes();
		return deviceItemRepository.setYcCalcItem(ycCalcs,deviceCodes);
	}

	/**
	 * 修改遥信运算库数据项
	 * @param yxCalcReq
	 * @return
	 */
	@PostMapping("/setYxCalcItem")
	public Boolean setYxCalcItem(@RequestBody YxCalcReq yxCalcReq){
		List<YxCalc> yxCalcs=yxCalcReq.getYxCalcs();
		String deviceCodes=yxCalcReq.getDeviceCodes();
		return deviceItemRepository.setYxCalcItem(yxCalcs,deviceCodes);
	}


	/**
	 * 电量价格数据项尖峰平谷时间和对应价格设置
	 * @param jfpgPriceNews
	 * @return
	 */
	@PostMapping("/setJfpgPriceNew")
	public Boolean setJfpgPriceNew(@RequestBody List<JfpgPriceNew> jfpgPriceNews) {
		return deviceItemRepository.setJfpgPriceNew(jfpgPriceNews);
	}

	/**
	 * 设置水气的价格
	 * @param waterOrGasPrices
	 * @return
	 */
	@PostMapping("/setWaterOrGasPrice")
	public Boolean setWaterOrGasPrice(@RequestBody List<WaterOrGasPrice> waterOrGasPrices) {
		return deviceItemRepository.setWaterOrGasPrice(waterOrGasPrices);
	}


	/**
	 * 设置遥控值
	 * @param itemId
	 * @param val
	 * @return
	 */
	@PostMapping("/setCTRL5G")
	public Boolean setCTRL5G(@RequestParam String itemId, @RequestParam Integer val){
		return deviceItemRepository.setCTRL5G(itemId,val);
	}

	/**
	 * 设置摇调
	 * @param setYts
	 * @return
	 */
	@PostMapping("/setYt")
	public Boolean setYt(@RequestBody List<SetYt> setYts ){
		return deviceItemRepository.setYt(setYts);
	}
}
