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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.system.user.entity.Shift;
import org.springblade.system.user.entity.User;
import org.springblade.system.user.entity.UserInfo;
import org.springblade.system.user.entity.UserShift;
import org.springblade.system.user.service.IUserService;
import org.springblade.system.user.service.ShiftService;
import org.springblade.system.user.service.UserShiftService;
import org.springblade.system.user.vo.UserJoinUserShiftVo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户服务Feign实现类
 *
 * @author Chill
 */
@RestController
@AllArgsConstructor
public class UserClient implements IUserClient {

	IUserService service;
	UserShiftService userShiftService;
	ShiftService shiftService;

	@Override
	@GetMapping(USER_INFO_BY_ID)
	public R<User> userInfoById(Long userId) {
		return R.data(service.getById(userId));
	}

	@GetMapping(GET_USERS)
	public List<User> getUsers(List<Long> userIds) {
		return service.getUsers(userIds);
	}

	@Override
	@GetMapping(USER_INFO)
	public R<UserInfo> userInfo(String tenantId, String account) {
		return R.data(service.userInfo(tenantId, account));
	}

	@Override
	@PostMapping(SAVE_USER)
	public R<Boolean> saveUser(@RequestBody User user) {
		return R.data(service.submit(user));
	}

	@Override
	@PostMapping(USER_ADD)
	public R<Boolean> userAdd(User user) {
		return R.data(service.save(user));
	}

	@Override
	@PutMapping(USER_UPDATE)
	public R<Boolean> userUpdate(User user) {
		return R.data(service.update(user, new QueryWrapper<User>().eq("id", user.getId())));
	}

	@Override
	@GetMapping(USER_LIST)
	public List<User> getUserList(Long sid, Long createUser) {
		User user = new User();
		user.setSid(String.valueOf(sid));
		user.setCreateUser(createUser);
		return service.list(Condition.getQueryWrapper(user));
	}

	@Override
	@GetMapping(USERS)
	public List<User> userList() {
		return service.list();
	}

	@Override
	@GetMapping(SHIFT_USERS)
	public List<UserJoinUserShiftVo> getUserShift(Long uid, Integer year, Integer month) {
		Query query = new Query();
		query.setCurrent(1);
		query.setSize(120);
		return service.userJoinShiftPage(Condition.getPage(query), uid, year, month).getRecords();
	}

	@Override
	@GetMapping(SHIFT_PAGE)
	public List<UserJoinUserShiftVo> userPage(String time, Long stationId, Long uid) {
		Query query = new Query();
		query.setCurrent(1);
		query.setSize(120);
		IPage<UserJoinUserShiftVo> userJoinUserShiftVoIPage = userShiftService.userShiftList(query, time, stationId, uid).getData();
		return userJoinUserShiftVoIPage.getRecords();
	}

	@Override
	@PostMapping(SHIFT_SAVE)
	public R shiftSave(Shift shift) {
		return R.status(shiftService.save(shift));
	}

	@Override
	@PutMapping(SHIFT_UPDATE)
	public R shiftUpdate(Shift shift) {
		return R.status(shiftService.update(shift, new QueryWrapper<Shift>().eq("id", shift.getId())));
	}

	@Override
	@GetMapping(SHIFT_LIST)
	public List<UserShift> userShiftList() {
		return userShiftService.list();
	}

	@Override
	@PutMapping(SHIFT_EXCHANGE)
	public Boolean exchangeShift(UserShift userShift) {
		return userShiftService.update(userShift, new QueryWrapper<UserShift>().eq("id", userShift.getId()));
	}
}
