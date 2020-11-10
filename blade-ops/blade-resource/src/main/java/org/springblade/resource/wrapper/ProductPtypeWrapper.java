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
package org.springblade.resource.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.resource.entity.ProductPtype;
import org.springblade.resource.vo.ProductPtypeVO;
import java.util.Objects;

/**
 * 产品模型编码包装类,返回视图层所需的字段
 *
 * @author bond
 * @since 2020-07-14
 */
public class ProductPtypeWrapper extends BaseEntityWrapper<ProductPtype, ProductPtypeVO>  {

	public static ProductPtypeWrapper build() {
		return new ProductPtypeWrapper();
 	}

	@Override
	public ProductPtypeVO entityVO(ProductPtype productPtype) {
		ProductPtypeVO productPtypeVO = Objects.requireNonNull(BeanUtil.copy(productPtype, ProductPtypeVO.class));

		return productPtypeVO;
	}

}
