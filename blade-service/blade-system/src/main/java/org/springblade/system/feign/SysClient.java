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
package org.springblade.system.feign;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import oracle.jdbc.proxy.annotation.Post;
import org.springblade.core.tool.api.R;
import org.springblade.system.entity.*;
import org.springblade.system.service.*;
import org.springblade.system.user.entity.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 系统服务Feign实现类
 *
 * @author Chill
 */
@ApiIgnore
@RestController
@AllArgsConstructor
public class SysClient implements ISysClient {

	IDeptService deptService;

	IRoleService roleService;

	IMenuService menuService;

	ITenantService tenantService;

	IParamService paramService;

	IRoleMenuService iRoleMenuService;

	@Override
	@GetMapping(MENU)
	public R<Menu> getMenu(Long id) {
		return R.data(menuService.getById(id));
	}

	@Override
	@GetMapping(DEPT)
	public R<Dept> getDept(Long id) {
		return R.data(deptService.getById(id));
	}

	@Override
	@GetMapping(DEPT_NAME)
	public R<String> getDeptName(Long id) {
		return R.data(deptService.getById(id).getDeptName());
	}

	@Override
	@GetMapping(ROLE)
	public R<Role> getRole(Long id) {
		return R.data(roleService.getById(id));
	}

	@Override
	@GetMapping(ROLE_NAME)
	public R<String> getRoleName(Long id) {
		return R.data(roleService.getById(id).getRoleName());
	}

	@Override
	@GetMapping(ROLE_ALIAS)
	public R<String> getRoleAlias(Long id) {
		return R.data(roleService.getById(id).getRoleAlias());
	}

	@Override
	@GetMapping(DEPT_NAMES)
	public R<List<String>> getDeptNames(String deptIds) {
		return R.data(deptService.getDeptNames(deptIds));
	}

	@Override
	@GetMapping(DEPT_CHILD)
	public R<List<Dept>> getDeptChild(Long deptId) {
		return R.data(deptService.getDeptChild(deptId));
	}

	@Override
	@GetMapping(ROLE_NAMES)
	public R<List<String>> getRoleNames(String roleIds) {
		return R.data(roleService.getRoleNames(roleIds));
	}

	@Override
	@GetMapping(ROLE_ALIASES)
	public R<List<String>> getRoleAliases(String roleIds) {
		return R.data(roleService.getRoleAliases(roleIds));
	}

	@Override
	@GetMapping(TENANT)
	public R<Tenant> getTenant(Long id) {
		return R.data(tenantService.getById(id));
	}

	@Override
	@GetMapping(TENANT_ID)
	public R<Tenant> getTenant(String tenantId) {
		return R.data(tenantService.getByTenantId(tenantId));
	}

	@Override
	@GetMapping(PARAM)
	public R<Param> getParam(Long id) {
		return R.data(paramService.getById(id));
	}

	@Override
	@GetMapping(PARAM_VALUE)
	public R<String> getParamValue(String paramKey) {
		return R.data(paramService.getValue(paramKey));
	}

	@Override
	@PostMapping(ROLE_ADD)
	public R<Boolean> roleCustomSubmit(Role role) {
		if (
			roleService.count(new QueryWrapper<Role>().eq("id", role.getId()).eq("is_deleted", 0)) > 0) {
			return R.data(roleService.update(role, new QueryWrapper<Role>().eq("id", role.getId())));
		}
		return R.data(roleService.save(role));
	}

	@Override
	@PostMapping(ROLE_DELETED)
	public R<Boolean> roleDelete(Role role) {
		return R.data(roleService.remove(new QueryWrapper<Role>().eq("id", role.getId())));
	}

	@Override
	@PostMapping(MENU_GRANT_ADD)
	public R<Boolean> menuCustomSubmit(RoleMenu roleMenu) {
		return R.data(iRoleMenuService.save(roleMenu));
	}

	@Override
	@GetMapping(ALL_DEPT)
	public R<List<Dept>> getAllDept() {
		return R.data(deptService.getAllDept());
	}
}
