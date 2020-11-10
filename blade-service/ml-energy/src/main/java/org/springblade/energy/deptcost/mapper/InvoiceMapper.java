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
package org.springblade.energy.deptcost.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.energy.deptcost.dto.DeptInvoiceDTO;
import org.springblade.energy.deptcost.entity.Invoice;
import org.springblade.energy.deptcost.vo.InvoiceVO;

import java.util.List;

/**
 *  Mapper 接口
 *
 * @author bond
 * @since 2020-07-24
 */
public interface InvoiceMapper extends BaseMapper<Invoice> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param invoice
	 * @return
	 */
	List<InvoiceVO> selectInvoicePage(IPage page, Invoice invoice);

	List<Invoice> selectInvoicePage(Invoice invoice);

	public List<DeptInvoiceDTO> selectDeptInvoicePage(IPage<DeptInvoiceDTO> page, Invoice invoice);
}
