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
package org.springblade.system.user.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.annotation.PreAuth;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.constant.BladeConstant;
import org.springblade.core.tool.constant.RoleConstant;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringPool;
import org.springblade.energy.feign.IUserStationClient;
import org.springblade.system.entity.Role;
import org.springblade.system.feign.ISysClient;
import org.springblade.system.user.entity.Shift;
import org.springblade.system.user.entity.User;
import org.springblade.system.user.entity.UserShift;
import org.springblade.system.user.page.PageQuery;
import org.springblade.system.user.page.PageUtils;
import org.springblade.system.user.service.IUserService;
import org.springblade.system.user.service.ShiftService;
import org.springblade.system.user.service.UserShiftService;
import org.springblade.system.user.vo.UserJoinUserShiftVo;
import org.springblade.system.user.vo.UserShiftVo;
import org.springblade.system.user.vo.UserVO;
import org.springblade.system.user.wrapper.UserWrapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.springblade.core.cache.constant.CacheConstant.USER_CACHE;

/**
 * 控制器
 *
 * @author Chill
 */
@RestController
@RequestMapping
@AllArgsConstructor
public class UserController {

	IUserService userService;
	IUserStationClient iUserStationClient;
	UserShiftService userShiftService;
	ISysClient iSysClient;
	ShiftService shiftService;


	/**
	 * 查询单条
	 */
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "查看详情", notes = "传入id")
	@GetMapping("/detail")
	//@PreAuth(RoleConstant.HAS_ROLE_ADMIN)
	public R<UserVO> detail(User user) {
		User detail = userService.getOne(Condition.getQueryWrapper(user));
		return R.data(UserWrapper.build().entityVO(detail));
	}

	/**
	 * 查询单条
	 */
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "查看详情", notes = "传入id")
	@GetMapping("/info")
	public R<UserVO> info(BladeUser user) {
		User detail = userService.getById(user.getUserId());
		return R.data(UserWrapper.build().entityVO(detail));
	}

	/**
	 * 用户列表
	 */
	@GetMapping("/list")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "account", value = "账号名", paramType = "query", dataType = "string"),
		@ApiImplicitParam(name = "realName", value = "姓名", paramType = "query", dataType = "string")
	})
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "列表", notes = "传入account和realName")
	@PreAuth(RoleConstant.HAS_ROLE_ADMIN)
	public R<IPage<UserVO>> list(@ApiIgnore @RequestParam Map<String, Object> user, Query query, BladeUser bladeUser) {
		QueryWrapper<User> queryWrapper = Condition.getQueryWrapper(user, User.class);
		IPage<User> pages = userService.page(Condition.getPage(query), (!bladeUser.getTenantId().equals(BladeConstant.ADMIN_TENANT_ID)) ? queryWrapper.lambda().eq(User::getTenantId, bladeUser.getTenantId()) : queryWrapper);
		return R.data(UserWrapper.build().pageVO(pages));
	}

	/**
	 * 自定义用户列表
	 */
	@GetMapping("/page")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "account", value = "账号名", paramType = "query", dataType = "string"),
		@ApiImplicitParam(name = "realName", value = "姓名", paramType = "query", dataType = "string")
	})
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "列表", notes = "传入account和realName")
	@PreAuth(RoleConstant.HAS_ROLE_ADMIN)
	public R<IPage<UserVO>> page(@ApiIgnore User user, Query query, Long deptId, BladeUser bladeUser) {
		IPage<User> pages = userService.selectUserPage(Condition.getPage(query), user, deptId, (bladeUser.getTenantId().equals(BladeConstant.ADMIN_TENANT_ID) ? StringPool.EMPTY : bladeUser.getTenantId()));
		return R.data(UserWrapper.build().pageVO(pages));
	}

	/**
	 * 新增或修改
	 */
	@ApiLog("新增或修改 用户")
	@PostMapping("/submit")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增或修改", notes = "传入User")
	@PreAuth(RoleConstant.HAS_ROLE_ADMIN)
	@CacheEvict(cacheNames = {USER_CACHE}, allEntries = true)
	public R submit(@Valid @RequestBody User user) {
		//iUserStationClient.saveStationClient(user.getId(),user.getSid());
		return R.status(userService.submit(user));
	}

	/**
	 * 修改
	 */
	//@ApiLog("修改 用户")
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入User")
	@CacheEvict(cacheNames = {USER_CACHE}, allEntries = true)
	public R update(@Valid @RequestBody User user) {
		//iUserStationClient.saveStationClient(user.getId(),user.getSid());
		return R.status(userService.updateUser(user));
	}


	/**
	 * 删除
	 */
	@ApiLog("删除 用户")
	@PostMapping("/remove")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "删除", notes = "传入id集合")
	@PreAuth(RoleConstant.HAS_ROLE_ADMIN)
	@CacheEvict(cacheNames = {USER_CACHE}, allEntries = true)
	public R remove(@RequestParam String ids) {
		return R.status(userService.removeUser(ids));
	}

	/**
	 * 设置菜单权限
	 *
	 * @param userIds
	 * @param roleIds
	 * @return
	 */
	@ApiLog("用户权限设置")
	@PostMapping("/grant")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "权限设置", notes = "传入roleId集合以及menuId集合")
	@PreAuth(RoleConstant.HAS_ROLE_ADMIN)
	public R grant(@ApiParam(value = "userId集合", required = true) @RequestParam String userIds,
				   @ApiParam(value = "roleId集合", required = true) @RequestParam String roleIds) {
		boolean temp = userService.grant(userIds, roleIds);
		return R.status(temp);
	}

	@PostMapping("/reset-password")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "初始化密码", notes = "传入userId集合")
	@PreAuth(RoleConstant.HAS_ROLE_ADMIN)
	public R resetPassword(@ApiParam(value = "userId集合", required = true) @RequestParam String userIds) {
		boolean temp = userService.resetPassword(userIds);
		return R.status(temp);
	}

	/**
	 * 修改密码
	 *
	 * @param oldPassword
	 * @param newPassword
	 * @param newPassword1
	 * @return
	 */
	@ApiLog("修改密码 用户")
	@PostMapping("/update-password")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "修改密码", notes = "传入密码")
	public R updatePassword(BladeUser user, @ApiParam(value = "旧密码", required = true) @RequestParam String oldPassword,
							@ApiParam(value = "新密码", required = true) @RequestParam String newPassword,
							@ApiParam(value = "新密码", required = true) @RequestParam String newPassword1) {
		boolean temp = userService.updatePassword(user.getUserId(), oldPassword, newPassword, newPassword1);
		return R.status(temp);
	}

	/**
	 * 用户列表
	 *
	 * @param user
	 * @return
	 */
	@GetMapping("/user-list")
	@ApiOperationSupport(order = 10)
	@ApiOperation(value = "用户列表", notes = "传入user")
	public R<List<User>> userList(User user, BladeUser bladeUser) {
		QueryWrapper<User> queryWrapper = Condition.getQueryWrapper(user);
		List<User> list = userService.list((!AuthUtil.isAdministrator()) ? queryWrapper.lambda().eq(User::getTenantId, bladeUser.getTenantId()) : queryWrapper);
		return R.data(list);
	}


	@ApiOperationSupport(order = 11)
	@ApiOperation("人员排班列表")
	@GetMapping("/shift-list")
	public R<IPage<UserJoinUserShiftVo>> userShiftList(Query query, @RequestParam String time, @RequestParam Long stationId) {
		return userShiftService.userShiftList(query, time, stationId, AuthUtil.getUserId());
	}

	@ApiOperationSupport(order = 12)
	@ApiOperation("人员排班编辑列表")
	@GetMapping("/edit-list")
	public R<IPage<UserJoinUserShiftVo>> userShiftEditList(Query query, @RequestParam String ids, @RequestParam String time) {
		String[] date = time.split("-");
		Integer year = Integer.parseInt(date[0]);
		Integer month = Integer.parseInt(date[1]);
		return R.data(userService.userJoinShiftEditPage(Condition.getPage(query), AuthUtil.getUserId(), ids, year, month));
	}

	/**
	 * @return 班次
	 */
	@ApiOperationSupport(order = 13)
	@ApiOperation("编辑排班")
	@PutMapping("/edit-shift")
	public R<Boolean> updateShift(@RequestBody List<UserShiftVo> users, @RequestParam String time) {
		String[] date = time.split("-");
		Integer year = Integer.parseInt(date[0]);
		Integer month = Integer.parseInt(date[1]);
		users.stream().distinct().forEach((user) -> {
			Long id = user.getId();
			UserJoinUserShiftVo joinUserShiftVo = userService.userJoinShiftCount(id, year, month);
			String shiftCycle = joinUserShiftVo.getShiftCycle();
			List<UserShiftVo> userShiftVoList = JSONObject.parseArray(shiftCycle, UserShiftVo.class);
			UserShiftVo attached = new UserShiftVo();
			Integer day = user.getDay();
			UserShiftVo getAttached = userShiftVoList.get(day);
			attached.setId(id);
			attached.setName(getAttached.getName());
			attached.setDay(day);
			Integer shift = user.getShift();
			Integer resRange = user.getResRange();
			Integer originalShift = getAttached.getShift();
			Integer originalResRange = getAttached.getResRange();
			if (shift != null) {
				attached.setShift(shift);
			} else {
				attached.setShift(originalShift);
			}
			if (resRange != null) {
				attached.setResRange(resRange);
			} else {
				attached.setResRange(originalResRange);
			}
			attached.setWeek(getAttached.getWeek());
			userShiftVoList.set(day, attached);
			UserShift userShift = new UserShift();
			userShift.setShiftCycle(JSONObject.toJSONString(userShiftVoList));
			userShiftService.update(userShift, new QueryWrapper<UserShift>().eq("user_id", id).eq("shift_year", year).eq("shift_month", month));
		});
		return R.success("编辑成功");
	}

	@ApiOperationSupport(order = 14)
	@ApiOperation("获取下属")
	@GetMapping("/subordinate")
	public R<PageUtils> getSubordinate(PageQuery pageQuery, Long stationId, Long userId) {
		List<User> users = userService.list(new QueryWrapper<User>().eq("sid", stationId).eq("create_user", userId));
		return R.data(new PageUtils(users, (long) users.size(), (long) pageQuery.getSize(), (long) pageQuery.getCurrent(), true));
	}

	@ApiOperationSupport(order = 15)
	@ApiOperation("APP--我的排班页面")
	@GetMapping("/api/shift")
	public R<List<UserShiftVo>> appShift(String time, Integer day) {
		Query query = new Query();
		query.setCurrent(1);
		query.setSize(100);
		Long userId = AuthUtil.getUserId();
		String userRole = AuthUtil.getUserRole();
		List<UserShiftVo> userShifts = new ArrayList<>();
		List<UserJoinUserShiftVo> userJoinUserShiftVos = userShiftService.userShiftList(query, time, Long.parseLong(administratorId(userId).split(",")[1]), Long.parseLong(administratorId(userId).split(",")[0])).getData().getRecords();

		if (userRole.trim().equals("admin") || userRole.trim().equals("administrator")) {
			userJoinUserShiftVos.forEach(userJoinUserShiftVo -> {
				List<UserShiftVo> userShiftVos = JSONObject.parseArray(userJoinUserShiftVo.getShiftCycle(), UserShiftVo.class);
				userShiftVos.stream().filter(userShiftVo -> userShiftVo.getDay().equals(day - 1)).forEach(userShiftVo -> {
					Integer shift = userShiftVo.getShift();
					if (shift == 1 || shift == 2 || shift == 3) {
						userShifts.add(userShiftVo);
					}
				});
			});
		} else {
			userJoinUserShiftVos.stream().filter(userJoinUserShiftVo -> userJoinUserShiftVo.getId().equals(userId)).forEach(userJoinUserShiftVo -> {
				List<UserShiftVo> userShiftVos = JSONObject.parseArray(userJoinUserShiftVo.getShiftCycle(), UserShiftVo.class);
				userShifts.addAll(userShiftVos);
			});
		}
		return R.data(userShifts);
	}

	@ApiOperationSupport(order = 16)
	@ApiOperation("APP--我的申请列表")
	@GetMapping("/api/shift/list")
	public R<PageUtils> shiftPage(PageQuery pageQuery, Shift shift) {
		Page<Shift> page = shiftService.page(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), new QueryWrapper<>(shift).orderByDesc("create_time"));
		return R.data(new PageUtils(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
	}

	public String administratorId(Long userId) {
		User user = userService.getById(userId);
		Role role = iSysClient.getRole(Long.parseLong(user.getRoleId())).getData();
		if (Func.isEmpty(user) || Func.isEmpty(role)) {
			throw new ServiceException("无效的用户id");
		}
		String roleAlias = role.getRoleAlias();
		AtomicReference<Long> adminId = new AtomicReference<>(0L);
		if (roleAlias.trim().equals("admin") || roleAlias.trim().equals("administrator")) {
			adminId.set(userId);
		} else {
			adminId.set(user.getCreateUser());
		}
		return adminId.get() + "," + user.getSid();
	}
}
