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
@TableName("t_power_off")
@ApiModel(value = "OffOperating对象", description = "倒闸操作票")
public class OffOperating extends TenantEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty("站点")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long stationId;

	@ApiModelProperty("位置")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long siteId;

	@ApiModelProperty("发令人")
	private String irritating;

	@ApiModelProperty("受令人")
	private String suffer;

	@ApiModelProperty("发令时间")
	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	private Date orderTime;

	@ApiModelProperty("操作开始时间")
	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	private Date operatingStartTime;

	@ApiModelProperty("操作结束时间")
	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	private Date operatingEndsTime;

	@ApiModelProperty("操作任务")
	private String operatingTask;

	@ApiModelProperty("操作项目")
	private String operatingItem;

	@ApiModelProperty("操作状态")
	//{"0":"监护下操作","1":"单人操作","2":"检查人员操作"}
	private Integer operatingStatus;

	@ApiModelProperty("备注")
	private String remarks;

	@ApiModelProperty("操作人")
	private String operatingPerson;

	@ApiModelProperty("监护人")
	private String guardianshipPerson;

	@ApiModelProperty("值班负责人")
	private String dutyPerson;

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
	//{"0":"待审批","1":"已审批","2":"已拒绝"}
	private Integer checkStatus;

	@ApiModelProperty("拒绝理由")
	private String refuseReason;

	@ApiModelProperty("验收质量")
	private Integer checkAcceptQuality;

	@ApiModelProperty("满意度")
	private Integer satisfaction;

	@ApiModelProperty("站点名称")
	private String stationName;

	@ApiModelProperty("位置名称")
	private String siteName;

	@ApiModelProperty("审核人名称")
	private String checkPersonName;

	@ApiModelProperty("申请人名称")
	private String applicationPersonName;

}
