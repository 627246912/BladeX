package org.springblade.resource.feign;

import lombok.AllArgsConstructor;
import org.springblade.bean.ProductProperty;
import org.springblade.resource.service.IProductPropertyService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bond
 * @date 2020/4/15 17:58
 * @desc
 */
@RestController
@AllArgsConstructor
public class ProductClient implements IProductClient {

	 private IProductPropertyService iProductPropertyService;

	/**
	 * 根据产品ID查询产品属性
	 * @return
	 */
	@PostMapping(GET_PRODUCTPROPERTYBYPRODUCTID)
	public List<ProductProperty> getProductPropertyByProductId(Long productId) {
		List<Long> productIds =new ArrayList<>();
		productIds.add(productId);
		return iProductPropertyService.selectProductPropertyByPids(productIds);
	}
	/**
	 *查询产品属性
	 */
	@PostMapping(GET_PRODUCTPROPERTYBYPRODUCTIDS)
	public List<ProductProperty> selectProductPropertyByPids(List<Long> productIds){
		return iProductPropertyService.selectProductPropertyByPids(productIds);

	}
}
