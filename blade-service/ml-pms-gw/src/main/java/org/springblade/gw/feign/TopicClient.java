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

import lombok.AllArgsConstructor;
import org.springblade.bean.DeviceItem;
import org.springblade.gw.repository.DeviceItemDataRepository;
import org.springblade.gw.restTemplate.DeviceItemRepository;
import org.springblade.gw.util.MqttTopicUtil;
import org.springblade.pms.gw.feign.ITopicClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 网关服务Feign实现类
 *
 * @author bond
 */
@RestController
@AllArgsConstructor
public class TopicClient implements ITopicClient {

	@Autowired
	private DeviceItemRepository deviceItemRepository;
	@Autowired
	private DeviceItemDataRepository deviceItemDataRepository;
	/**
	 *
	 * @param gwids
	 */
	@Override
	@PostMapping(ADDDEVICETOPIC)
	public void addDeviceTopic(List<String> gwids) {
	for(String gwid:gwids){
		MqttTopicUtil.addTopic(gwid);
	}
		List<DeviceItem> deviceItemList=deviceItemRepository.getItemInfosByGwids(gwids);
		deviceItemDataRepository.updateId2ItemInfo(deviceItemList);

	}

	/**
	 * 主题订阅
	 * @return
	 */
	@PostMapping(ADDTOPIC)
	public void addTopic( String topic){
		MqttTopicUtil.addTopic(topic);
	}

}
