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
package org.springblade.energy.diagram.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springblade.energy.diagram.entity.DiagramItem;

import java.util.List;

/**
 * 系统图网关数据项数据传输对象实体类
 *
 * @author bond
 * @since 2020-04-15
 */
@Data
public class QueryDiagramItemResq {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "业务分类")
	private String ftype;
	@ApiModelProperty(value = "业务分类名称")
	private String ftypeName;

	private List<DiagramItem> diagramItemList;
}
