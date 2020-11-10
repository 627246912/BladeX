package org.springblade.energy.securitymanagement.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springblade.core.tenant.mp.TenantEntity;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_construction")
@ApiModel(value = "ConstructionNotifyCard对象", description = "安全管理--安全管理通告卡")
public final class ConstructionNotifyCard extends TenantEntity implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * 施工日期
	 */
	@ApiModelProperty("施工日期")
	@NotNull(message = "施工日期不能为空")
	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	private Date constructionDate;
	/**
	 * 施工单位
	 */
	@ApiModelProperty("施工单位")
	@NotBlank(message = "施工单位不能为空")
	private String constructionOrg;

	/**
	 * 施工项目
	 */
	@ApiModelProperty("施工项目")
	@NotBlank(message = "施工项目不能为空")
	private String constructionItem;

	/**
	 * 施工地址
	 */
	@ApiModelProperty("施工地址")
	@NotBlank(message = "施工地址不能为空")
	private String constructionAddress;

	/**
	 * 施工人员
	 */
	@ApiModelProperty("施工人员")
	@NotBlank(message = "施工人员不能为空")
	private String constructionPerson;


	/**
	 * 教育日期
	 */
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8"
	)
	@ApiModelProperty("教育日期")
	@NotNull(message = "教育日期能不为空")
	private Date educationDate;

	/**
	 * 授课人
	 */
	@ApiModelProperty("授课人")
	@NotBlank(message = "授课人不能为空")
	private String lecturer;

	/**
	 * 现场负责人
	 */
	@ApiModelProperty("现场负责人")
	@NotBlank(message = "现场负责人不能为空")
	private String sitePrincipal;

	/**
	 * 简要教育内容
	 */
	@ApiModelProperty("简要教育内容")
	private String educationContent;

	/**
	 * 作业人数
	 */
	@ApiModelProperty("作业人数")
	@NotNull(message = "作业人数不能为空")
	private Integer operationNumber;

	/**
	 * 施教人名称
	 */
	@ApiModelProperty("施教人名称")
	@NotBlank(message = "施教人名称不能为空")
	private String educatorName;

	/**
	 * 备注
	 */
	@ApiModelProperty("备注")
	private String remarks;

	/**
	 * 相关通知书文件
	 */
	@ApiModelProperty("相关通知书文件")
	private String notifyBookFile;
}
