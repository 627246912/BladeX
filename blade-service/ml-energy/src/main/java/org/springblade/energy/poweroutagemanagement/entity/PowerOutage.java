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
@TableName("t_power")
@ApiModel(value = "PowerOutage对象", description = "停送电")
public class PowerOutage extends TenantEntity implements Serializable {

	private static final long SerialVersionUID = 1L;

	@ApiModelProperty("站点")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long stationId;

	@ApiModelProperty("站点名称")
	private String stationName;
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty("位置")

	private Long siteId;

	@ApiModelProperty("位置名称")
	private String siteName;

	@ApiModelProperty("申请部门")
	private String applicationDept;

	@ApiModelProperty("申请部门名称")
	private String applicationDeptName;

	@ApiModelProperty("送电线路名称")
	private String powerName;

	@ApiModelProperty("交配电管理人")
	private String powerDistributionManagementPerson;

	@ApiModelProperty("故障设备是否恢复")
	private Integer isMalfunctionDevRestore;

	@ApiModelProperty("工作负责人")
	private String jobResponsiblePerson;

	@ApiModelProperty("停电原因")
	//{"0":"计划停电","1":"临时停电","2":"故障停电"}
	private Integer powerFailureReason;

	@ApiModelProperty("故障原因说明")
	private String malfunctionReasonDescription;

	@ApiModelProperty("详细处理方案")
	private String detailDealWithProgram;

	@ApiModelProperty("申请部门负责人")
	private String applicationDeptResponsiblePerson;

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
