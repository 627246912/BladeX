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
package org.springblade.energy.diagram.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 实体类
 *
 * @author bond
 * @since 2020-05-18
 */
@Data
@TableName("t_diagram_camera")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "DiagramCamera对象", description = "DiagramCamera对象")
public class DiagramCamera extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 站点id
	*/
		@ApiModelProperty(value = "站点id")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long stationId;
	/**
	* 位置id
	*/
		@ApiModelProperty(value = "位置id")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long siteId;
	/**
	* 系统图ID
	*/
		@ApiModelProperty(value = "系统图ID")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long diagramId;
	/**
	* 名称
	*/
		@ApiModelProperty(value = "名称")
		private String name;
	/**
	* url
	*/
		@ApiModelProperty(value = "url")
		private String src;

	/**
	 * 视频来源ip
	 */
	@ApiModelProperty(value = "videoIp")
	private String videoIp;

	/**
	 * 自定义视频名
	 */
	@ApiModelProperty(value = "videoName")
	private String videoName;

	/**
	 * 视频状态
	 */
	@ApiModelProperty(value = "videoStatus")
	private String videoStatus;

	/**
	 * 自定义视频名
	 */
	@ApiModelProperty(value = "video_account")
	private String videoAccount;

	/**
	 * 自定义视频名
	 */
	@ApiModelProperty(value = "videoPwd")
	private String videoPwd;

	/**
	 * 流媒体服务器ip
	 */
	@ApiModelProperty(value = "media_server_ip")
	private String mediaServerIp;

}
