package org.springblade.resource.listener;

import org.springblade.bean.Product;
import org.springblade.bean.ProductProperty;
import org.springblade.bean.ProductType;
import org.springblade.bean.Ptype;
import org.springblade.constants.ProductConstant;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.utils.Func;
import org.springblade.resource.service.IProductPropertyService;
import org.springblade.resource.service.IProductService;
import org.springblade.resource.service.IProductTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Auther: bond
 * @Date: 2020/4/22
 * @Description:
 */
@Component
public class ApplicationReadyEventListener implements ApplicationListener<ApplicationReadyEvent> {

	@Autowired
	IProductPropertyService iProductPropertyService;
	@Autowired
	IProductService iProductService;
	@Autowired
	IProductTypeService iProductTypeService;
	@Autowired
	private BladeRedisCache redisCache;

	@Override public void onApplicationEvent(ApplicationReadyEvent
	  applicationReadyEvent) {

		redisCache.del(ProductConstant.PRODUCT_KEY);
		redisCache.del(ProductConstant.PROPERTY_KEY);
		redisCache.del(ProductConstant.PTYPE_ALL_LIST);
		redisCache.del(ProductConstant.PRODUCT_TYPE_KEY);
		redisCache.del(ProductConstant.PRODUCT_PROPERTY_KEY);

	  List<Product> products = iProductService.selectProduct(new Product());

	  List<ProductType> productTypes = iProductTypeService.list();
		Collections.sort(productTypes, new Comparator<ProductType>() {
			public int compare(ProductType u1, ProductType u2) {
				return new Double(u1.getId()).compareTo(new Double(u2.getId())); //升序
				// return new Double(u2.getSalary()).compareTo(new Double(u2.getSalary())); //降序
			}
		});
	  for(Product product : products){
			  Map<Object,Object> mapP=new HashMap<>();
			  mapP.put(product.getId(),product);
			  redisCache.hMset(ProductConstant.PRODUCT_KEY,mapP);

			  List<Long> productids= new ArrayList<Long>();
			  productids.add(product.getId());
			  List<ProductProperty> productPropertys=iProductPropertyService.selectProductPropertyByPids(productids);
			  if(Func.isNotEmpty(productPropertys)) {
				  Map<Object, Object> map = new HashMap<>();
				  map.put(product.getId(), productPropertys);
				  redisCache.hMset(ProductConstant.PRODUCT_PROPERTY_KEY, map);
			  }
			  for(ProductProperty productProperty :productPropertys){
				  Map<Object, Object> map = new HashMap<>();
				  map.put(productProperty.getId(), productProperty);
				  redisCache.hMset(ProductConstant.PROPERTY_KEY, map);

			  }
		}

		for (ProductType productType:productTypes){
				Map<Object, Object> mapP = new HashMap<>();
				mapP.put(productType.getId(), productType);
				redisCache.hMset(ProductConstant.PRODUCT_TYPE_KEY, mapP);
		}

		List<Ptype> ptypeList=iProductPropertyService.selectProductPropertyPType();
		redisCache.set(ProductConstant.PTYPE_ALL_LIST,ptypeList);

	  }


}
