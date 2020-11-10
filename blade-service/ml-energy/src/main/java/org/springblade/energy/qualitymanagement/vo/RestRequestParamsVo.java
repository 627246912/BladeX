package org.springblade.energy.qualitymanagement.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("all")
public class RestRequestParamsVo {

	/**
	 * 请求类型
	 */
	private String contentType;


	private String bladeAuth;


	private String Authorization = "Basic c3dvcmQ6c3dvcmRfc2VjcmV0";


	private String TenantId = "CRRCZhuzhou";

}
