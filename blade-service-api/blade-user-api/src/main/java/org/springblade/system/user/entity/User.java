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
package org.springblade.system.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * 实体类
 *
 * @author Chill
 */
@Data
@TableName("blade_user")
@EqualsAndHashCode(callSuper = true)
public class User extends TenantEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 账号
	 */
	@ApiModelProperty("账号")
	private String account;

	/**
	 * 密码
	 */
	@ApiModelProperty("密码")
	private String password;

	/**
	 * 昵称
	 */
	@ApiModelProperty("昵称")
	private String name;

	/**
	 * 真名
	 */
	@ApiModelProperty("姓名")
	private String realName;

	/**
	 * 头像
	 */
	@ApiModelProperty("头像")
	private String avatar;

	/**
	 * 邮箱
	 */
	@ApiModelProperty("邮件")
	private String email;

	/**
	 * 手机
	 */
	@ApiModelProperty("手机")
	private String phone;

	/**
	 * 生日
	 */
	@ApiModelProperty("生日")
	private Date birthday;

	/**
	 * 图片
	 */
	@ApiModelProperty("图片")
	private String image;

	/**
	 * 性别
	 */
	@ApiModelProperty("性别")
	private Integer sex;

	/**
	 * 角色id
	 */
	@ApiModelProperty("角色id")
	private String roleId;

	/**
	 * 部门id
	 */
	@ApiModelProperty("部门id")
	private String deptId;

	/**
	 * 站点id
	 */
	@ApiModelProperty("站点id")
	private String sid;

	/**
	 * 是否发送邮件
	 */
	@ApiModelProperty("是否发送邮件")
	private Integer sendEmail;

	/**
	 * 是否发送短信
	 */
	@ApiModelProperty("是否发送短信")
	private Integer sendSms;

	/**
	 * 是否站内推送
	 */
	@ApiModelProperty("是否站内推送")
	private Integer SendStation;

	/**
	 * 派单优先级
	 */
	@ApiModelProperty("派单优先级")
	private Integer level;

	/**
	 * 警报优先级
	 */
	@ApiModelProperty("警报优先级")
	private Integer alarmType;


}
