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
package org.springblade.resource.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

/**
 * 产品模型编码数据传输对象实体类
 *
 * @author bond
 * @since 2020-07-14
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(30)
@ColumnWidth(25)
public class ProductPtypeDTO {
	@ExcelProperty(
		value = {"产品模型统一编码", "sid"},
		index = 0
	)
	private String ptype;
	@ExcelProperty(
		value = {"产品模型统一编码", "简称"},
		index = 1
	)
	private String cname;
	@ExcelProperty(
		value = {"产品模型统一编码", "名称"},
		index = 2
	)
	private String name;
	@ExcelProperty(
		value = {"产品模型统一编码", "单位"},
		index = 3
	)
	private String unit;
}
