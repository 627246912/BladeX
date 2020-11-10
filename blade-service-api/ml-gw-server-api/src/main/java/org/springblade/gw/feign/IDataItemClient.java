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


import org.springblade.bean.DeviceItem;
import org.springblade.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 网关 Feign接口类
 *
 * @author bond
 */
@FeignClient(
	value = "ml-gw-server"
)
public interface IDataItemClient {

	/**
	 * 设置遥控值
	 * @param itemId
	 * @param val
	 * @return
	 */
	@PostMapping("/setYK")
	Boolean setYk(@RequestParam String itemId,@RequestParam Integer val);
	/**
	 * 修改遥测传输参数
	 *
	 * @param ycTranReq
	 * @return
	 */
	@PostMapping("/setYcTranInfos")
	boolean setYcTranInfos(@RequestBody YcTranReq ycTranReq);

	/**
	 * 修改遥控传输参数
	 *
	 * @param ykTranReq
	 * @return
	 */
	@PostMapping("/setYkTranInfos")
	boolean setYkTranInfos(@RequestBody YkTranReq ykTranReq);

	/**
	 * 修改遥信传输参数
	 *
	 * @param yxTranReq
	 * @return
	 */
	@PostMapping("/setYxTranInfos")
	boolean setYxTranInfos(@RequestBody YxTranReq yxTranReq);

	/**
	 * 设置遥测传输参数
	 *
	 * @param ycStoreReq
	 * @return
	 */
	@PostMapping("/setYcStoreInfos")
	Boolean setYcStoreInfos(@RequestBody YcStoreReq ycStoreReq);

	/**
	 * 遥测告警参数设置
	 *
	 * @param ycAlarmConfigReq
	 * @return
	 */
	@PostMapping("/setYcAlarmConfig")
	Boolean setYcAlarmConfig(@RequestBody YcAlarmConfigReq ycAlarmConfigReq);

	/**
	 * 遥信告警参数设置
	 *
	 * @param yxAlarmConfigReq
	 * @return
	 */
	@PostMapping("/setYxAlarmConfig")
	Boolean setYxAlarmConfig(@RequestBody YxAlarmConfigReq yxAlarmConfigReq);
	/**
	 * add遥信/遥测运算库数据项
	 * @param deviceItem
	 * @return
	 */
	@PostMapping("/addCalcItem")
	Boolean addCalcItem(@RequestBody DeviceItem deviceItem);
	/**
	 * 修改遥测运算库数据项
	 * @param ycCalcReq
	 * @return
	 */
	@PostMapping("/setYcCalcItem")
	Boolean setYcCalcItem(@RequestBody YcCalcReq ycCalcReq);

	/**
	 * 修改遥信运算库数据项
	 * @param yxCalcReq
	 * @return
	 */
	@PostMapping("/setYxCalcItem")
	Boolean setYxCalcItem(@RequestBody YxCalcReq yxCalcReq);


	/**
	 * 电量价格数据项尖峰平谷时间和对应价格设置
	 * @param jfpgPriceNews
	 * @return
	 */
	@PostMapping("/setJfpgPriceNew")
	Boolean setJfpgPriceNew(List<JfpgPriceNew> jfpgPriceNews);

	/**
	 * 设置水气的价格
	 * @param waterOrGasPrices
	 * @return
	 */
	@PostMapping("/setWaterOrGasPrice")
	Boolean setWaterOrGasPrice(List<WaterOrGasPrice> waterOrGasPrices);

}
