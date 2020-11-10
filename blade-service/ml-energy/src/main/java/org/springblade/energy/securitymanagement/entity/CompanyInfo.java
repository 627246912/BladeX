package org.springblade.energy.securitymanagement.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springblade.core.mp.base.BaseEntity;
import org.springblade.core.tenant.mp.TenantEntity;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_company")
@ApiModel(value = "CompanyInfo对象", description = "安全管理--公司信息")
public final class CompanyInfo extends TenantEntity implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * 公司名称
	 */
	@ApiModelProperty("公司名称")
	@NotBlank(message = "公司名称不能为空")
	private String companyName;

	/**
	 * 公司地址
	 */
	@ApiModelProperty("公司地址")
	@NotBlank(message = "公司地址不能为空")
	private String companyAddress;

	/**
	 * 联系人
	 */
	@ApiModelProperty("联系人")
	@NotBlank(message = "联系人不能为空")
	private String contactPerson;

	/**
	 * 手机号码
	 */
	@ApiModelProperty("手机号码")
	@NotBlank(message = "手机号码不能为空")
	private String phone;

}
