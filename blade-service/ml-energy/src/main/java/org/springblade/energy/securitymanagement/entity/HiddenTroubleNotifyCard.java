package org.springblade.energy.securitymanagement.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springblade.core.tenant.mp.TenantEntity;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_hidden")
@ApiModel(value = "HiddenTroubleNotifyCard对象", description = "安全管理--安全隐患告知卡")
public final class HiddenTroubleNotifyCard extends TenantEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 项目名称
	 */
	@ApiModelProperty("项目名称")
	@NotBlank(message = "项目名称不能为空")
	private String itemName;

	/**
	 * 相关方
	 */
	@ApiModelProperty("相关方")
	@NotBlank(message = "相关方不能为空")
	private String related;

	/**
	 * 隐患级别
	 */
	@ApiModelProperty("隐患级别")
	@NotNull(message = "隐患级别不能为空")
	private String hiddenTroubleLevel;

	/**
	 * 隐患描述
	 */
	@ApiModelProperty("隐患描述")
	@NotBlank(message = "隐患描述不能为空")
	private String hiddenTroubleDepict;

	/**
	 * 隐患依据
	 */
	@ApiModelProperty("隐患依据")
	@NotBlank(message = "隐患依据不能为空")
	private String hiddenTroubleAccordance;

	/**
	 * 主要危害
	 */
	@ApiModelProperty("主要危害")
	@NotBlank(message = "主要危害不能为空")
	private String mainHarm;

	/**
	 * 整改措施
	 */
	@ApiModelProperty("整改措施")
	@NotBlank(message = "整改措施不能为空")
	private String rectificationMeasures;

	/**
	 * 隐患图片
	 */
	@ApiModelProperty("隐患图片")
	@NotBlank(message = "隐患图片不能为空")
	private String hiddenTroubleImage;

	/**
	 * 被告知方
	 */
	@ApiModelProperty("被告知方")
	@NotBlank(message = "被告知方不能为空")
	private String informed;

	// -----------------------------------------------------------------------------------------------------------------

	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty("审核人")
	private Long checkPerson;

	@ApiModelProperty("审核时间")
	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	private Date checkTime;

	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty("申请人")
	private Long applicationPerson;

	@ApiModelProperty("申请时间")
	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	private Date applicationTime;

	@ApiModelProperty("审核状态")
	private Integer checkStatus;

	@ApiModelProperty("拒绝理由")
	private String refuseReason;

	@ApiModelProperty("审核人名称")
	private String checkPersonName;

	@ApiModelProperty("申请人名称")
	private String applicationPersonName;
}
