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
package org.springblade.resource;

import org.springblade.core.cloud.feign.EnableBladeFeign;
import org.springblade.core.launch.BladeApplication;
import org.springblade.core.launch.constant.AppConstant;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.Bean;

import javax.servlet.MultipartConfigElement;
import java.io.File;

/**
 * 资源启动器
 *
 * @author Chill
 */
@EnableBladeFeign
@SpringCloudApplication
public class ResourceApplication {

	public static void main(String[] args) {
		BladeApplication.run(AppConstant.APPLICATION_RESOURCE_NAME, ResourceApplication.class, args);
	}
	@Bean
	MultipartConfigElement multipartConfigElement() {
		MultipartConfigFactory factory = new MultipartConfigFactory();
		String location = System.getProperty("user.dir") + "/data/tmp";
		File tmpFile = new File(location);
		if (!tmpFile.exists()) {
			tmpFile.mkdirs();
		}
		factory.setLocation(location);
		return factory.createMultipartConfig();
	}
}

