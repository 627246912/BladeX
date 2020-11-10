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
public interface ITopicClient {

	String ADDDEVICETOPIC="addTopic/addDeviceTopic";

	String ADDTOPIC="addTopic/addTopic";
	/**
	 * 网关信息订阅
	 * @return
	 */
	@PostMapping(ADDDEVICETOPIC)
	public void addDeviceTopic(@RequestBody List<String> gwids);


	/**
	 * 主题订阅
	 * @return
	 */
	@PostMapping(ADDTOPIC)
	public void addTopic(@RequestParam String gwids);

}
