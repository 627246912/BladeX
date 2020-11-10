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
package org.springblade.system.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springblade.system.user.entity.User;
import org.springblade.system.user.vo.UserJoinUserShiftVo;

import java.util.List;

/**
 * Mapper 接口
 *
 * @author Chill
 */
public interface UserMapper extends BaseMapper<User> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param user
	 * @param deptIdList
	 * @param tenantId
	 * @return
	 */
	List<User> selectUserPage(IPage<User> page, @Param("user") User user, @Param("deptIdList") List<Long> deptIdList, @Param("tenantId") String tenantId);

	/**
	 * 获取用户
	 *
	 * @param tenantId
	 * @param account
	 * @return
	 */
	User getUser(String tenantId, String account);

	List<User> getUsers(List<Long> userIds);

	@Select("SELECT blade_user.id,blade_user.real_name,t_user_shift.shift_cycle FROM blade_user,t_user_shift WHERE blade_user.id  = t_user_shift.user_id AND blade_user.create_user = #{uid} AND t_user_shift.shift_year = #{year} AND t_user_shift.shift_month = #{month}")
	List<UserJoinUserShiftVo> userJoinShiftPage(IPage<UserJoinUserShiftVo> page, @Param("uid") Long uid,@Param("year") Integer year, @Param("month") Integer month);

	@Select("SELECT blade_user.id,blade_user.real_name,t_user_shift.shift_cycle FROM blade_user,t_user_shift WHERE blade_user.id  = t_user_shift.user_id AND blade_user.create_user = #{uid} AND t_user_shift.shift_year = #{year} AND t_user_shift.shift_month = #{month} AND blade_user.id in(${ids})")
	List<UserJoinUserShiftVo> userJoinShiftEditPage(IPage<UserJoinUserShiftVo> page, @Param("uid") Long uid, @Param("ids") String ids,@Param("year") Integer year, @Param("month") Integer month);

	@Select("SELECT blade_user.id,blade_user.real_name,t_user_shift.shift_cycle FROM blade_user,t_user_shift WHERE blade_user.id  = t_user_shift.user_id AND blade_user.id = #{uid} AND t_user_shift.shift_year = #{year} AND t_user_shift.shift_month = #{month}")
	UserJoinUserShiftVo userJoinShiftCount(@Param("uid") Long uid, @Param("year") Integer year, @Param("month") Integer month);

}
