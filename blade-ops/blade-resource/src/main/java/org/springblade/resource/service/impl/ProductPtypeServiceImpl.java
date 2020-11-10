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
package org.springblade.resource.service.impl;

import org.springblade.resource.entity.ProductPtype;
import org.springblade.resource.vo.ProductPtypeVO;
import org.springblade.resource.mapper.ProductPtypeMapper;
import org.springblade.resource.service.IProductPtypeService;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 产品模型编码 服务实现类
 *
 * @author bond
 * @since 2020-07-14
 */
@Service
public class ProductPtypeServiceImpl extends BaseServiceImpl<ProductPtypeMapper, ProductPtype> implements IProductPtypeService {

	@Override
	public IPage<ProductPtypeVO> selectProductPtypePage(IPage<ProductPtypeVO> page, ProductPtypeVO productPtype) {
		return page.setRecords(baseMapper.selectProductPtypePage(page, productPtype));
	}

}
