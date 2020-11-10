package org.springblade.energy.securitymanagement.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springblade.core.tenant.mp.TenantEntity;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_agreement")
@ApiModel(value = "FileAgreement对象", description = "安全管理--安全协议")
public final class FileAgreement extends TenantEntity implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * 文件名称
	 */
	@ApiModelProperty("文件名称")
	@NotBlank(message = "文件名称不能为空")
	private String name;

	/**
	 * 文件
	 */
	@ApiModelProperty("文件")
	@NotBlank(message = "文件不能为空")
	private String file;

}
