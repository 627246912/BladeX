package org.springblade.energy.poweroutagemanagement.entity;

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
@TableName("t_power_job")
@ApiModel(value = "Job对象", description = "工作票")
public class Job extends TenantEntity implements Serializable {

	private static final long SerialVersionUID = 1L;

	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty("站点")
	private Long stationId;

	@ApiModelProperty("站点名称")
	private String stationName;

	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty("位置")
	private Long siteId;

	@ApiModelProperty("位置名称")
	private String siteName;

	@ApiModelProperty("工作负责人")
	private String jobResponsiblePerson;

	@ApiModelProperty("工作许可人")
	private String jobAllowPerson;

	@ApiModelProperty("工作班人员")
	private String jobPerson;

	@ApiModelProperty("班组")
	private String team;

	@ApiModelProperty("工作任务")
	private String jobTask;

	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@ApiModelProperty("计划开始工作时间")
	private Date planStartJobTime;

	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@ApiModelProperty("计划结束工作时间")
	private Date planEndsJobTime;
	//{"0":"不停电","1":停电""}
	@ApiModelProperty("工作条件")

	private Integer jobCondition;

	@ApiModelProperty("注意事项")
	private String noteMatter;

	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@ApiModelProperty("工作开始时间")
	private Date startTime;

	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@ApiModelProperty("工作结束时间")
	private Date endsTime;

	@ApiModelProperty("备注")
	private String remarks;

	@ApiModelProperty("工作风险")
	private String jobRisk;

	//------------------------------------------------------------------------------------------------------------------

	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty("审核人")
	private Long checkPerson;

	@ApiModelProperty("审核人名称")
	private String checkPersonName;

	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@ApiModelProperty("审核时间")
	private Date checkTime;

	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty("申请人")
	private Long applicationPerson;

	@ApiModelProperty("申请人名称")
	private String applicationPersonName;

	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@ApiModelProperty("申请时间")
	private Date applicationTime;

	@ApiModelProperty("审核状态")
	//{"0":"待审批","1":"已审批","2":"已拒绝"}
	private Integer checkStatus;

	@ApiModelProperty("拒绝理由")
	private String refuseReason;

	@ApiModelProperty("验收质量")
	private Integer checkAcceptQuality;

	@ApiModelProperty("满意度")
	private Integer satisfaction;


}
