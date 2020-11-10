package org.springblade.system.user.entity;

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

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_shift")
@ApiModel(value = "Shift", description = "Shift对象")
public class Shift extends TenantEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty("申请人id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long applicant;

	@ApiModelProperty("申请人名称")
	private String applicantName;

	@ApiModelProperty("被换班人id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long shiftedPerson;

	@ApiModelProperty("被换班人名称")
	private String shiftedPersonName;

	@ApiModelProperty("换班日期")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date shiftDate;

	@ApiModelProperty("被换班日期")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date shiftedDate;

	@ApiModelProperty("原因")
	private String reason;

	@ApiModelProperty("换班状态 0：待同意 1：已同意 2：已拒绝 默认待同意")
	private Integer shiftStatus;

	@ApiModelProperty("申请人头像")
	private String applicantAvatar;

	@ApiModelProperty("被换班人头像")
	private String shiftedPersonAvatar;

	@ApiModelProperty("申请人部门id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long applicantDeptId;

	@ApiModelProperty("申请人部门名称")
	private String applicantDeptName;

	@ApiModelProperty("位置id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long siteId;

	@ApiModelProperty("站点id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long stationId;

}
