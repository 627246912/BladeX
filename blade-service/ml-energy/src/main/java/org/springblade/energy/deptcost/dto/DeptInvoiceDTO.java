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
package org.springblade.energy.deptcost.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springblade.energy.deptcost.entity.Invoice;

import java.util.List;

/**
 * 数据传输对象实体类
 *
 * @author bond
 * @since 2020-07-24
 */
@Data
public class DeptInvoiceDTO {
	private static final long serialVersionUID = 1L;

	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "部门id")
	private Long deptId;

	@ApiModelProperty(value = "部门名称")
	private String deptName;

	@ApiModelProperty(value = "用量")
	private Float val;

	@ApiModelProperty(value = "费用")
	private Float cost;

	@ApiModelProperty(value = "对比值")
	private Float contrast;

	@ApiModelProperty(value = "误差比")
	private String errRate;

	@ApiModelProperty(value = "发票信息")
	private List<Invoice> invoiceList;

}
