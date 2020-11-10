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
package org.springblade.system.user.feign;

import org.springblade.core.launch.constant.AppConstant;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.system.user.entity.Shift;
import org.springblade.system.user.entity.User;
import org.springblade.system.user.entity.UserInfo;
import org.springblade.system.user.entity.UserShift;
import org.springblade.system.user.vo.UserJoinUserShiftVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User Feign接口类
 *
 * @author Chill
 */
@FeignClient(
	value = AppConstant.APPLICATION_USER_NAME
)
public interface IUserClient {

	String API_PREFIX = "/client";
	String USER_INFO = API_PREFIX + "/user-info";
	String USER_INFO_BY_ID = API_PREFIX + "/user-info-by-id";
	String SAVE_USER = API_PREFIX + "/save-user";

	String GET_USERS = API_PREFIX + "/get-users";
	String USER_ADD = API_PREFIX + "/user-add";
	String USER_UPDATE = API_PREFIX + "/user-update";
	String USER_LIST = API_PREFIX + "/user-list";
	String USERS = API_PREFIX + "/list";
	String SHIFT_USERS = API_PREFIX + "/shift-list";
	String SHIFT_PAGE = API_PREFIX + "/shift-page";
	String SHIFT_SAVE = API_PREFIX + "/shift-save";
	String SHIFT_UPDATE = API_PREFIX + "/shift-update";
	String SHIFT_EXCHANGE = API_PREFIX + "/shift-exchange";
	String SHIFT_LIST = API_PREFIX + "/list-user-shift";


	/**
	 * 获取用户信息
	 *
	 * @param userId 用户id
	 * @return
	 */
	@GetMapping(USER_INFO_BY_ID)
	R<User> userInfoById(@RequestParam("userId") Long userId);

	@GetMapping(GET_USERS)
	List<User> getUsers(@RequestBody List<Long> userIds);

	/**
	 * 获取用户信息
	 *
	 * @param tenantId 租户ID
	 * @param account  账号
	 * @return
	 */
	@GetMapping(USER_INFO)
	R<UserInfo> userInfo(@RequestParam("tenantId") String tenantId, @RequestParam("account") String account);

	/**
	 * 新建用户
	 *
	 * @param user 用户实体
	 * @return
	 */
	@PostMapping(SAVE_USER)
	R<Boolean> saveUser(@RequestBody User user);

	/**
	 * 用户添加
	 */
	@PostMapping(USER_ADD)
	R<Boolean> userAdd(@RequestBody User user);

	/**
	 * 修改用户
	 */
	@PutMapping(USER_UPDATE)
	R<Boolean> userUpdate(@RequestBody User user);

	@GetMapping(USER_LIST)
	List<User> getUserList(@RequestParam("sid") Long sid, @RequestParam("createUser") Long createUser);

	@GetMapping(USERS)
	List<User> userList();

	@GetMapping(SHIFT_USERS)
	List<UserJoinUserShiftVo> getUserShift(@RequestParam Long uid, @RequestParam Integer year, @RequestParam Integer month);

	@GetMapping(SHIFT_PAGE)
	List<UserJoinUserShiftVo> userPage(@RequestParam String time, @RequestParam Long stationId, @RequestParam Long uid);

	@PostMapping(SHIFT_SAVE)
	R shiftSave(@RequestBody Shift shift);

	@PutMapping(SHIFT_UPDATE)
	R shiftUpdate(@RequestBody Shift shift);

	@GetMapping(SHIFT_LIST)
	List<UserShift> userShiftList();

	@PutMapping(SHIFT_EXCHANGE)
	Boolean exchangeShift(@RequestBody UserShift userShift);
}
