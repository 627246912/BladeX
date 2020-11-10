package org.springblade.energy.operationmaintenance.entity;

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
@TableName("t_change")
@ApiModel(value = "Change", description = "Change对象")
public class Change extends TenantEntity implements Serializable {

	private static final Long serialVersionUID = 1L;

	@ApiModelProperty("变更类型 1：转单")
	private Integer changeType;

	@ApiModelProperty("任务id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long taskId;

	@ApiModelProperty("任务类型 0：巡检 1：保养 2：维修 3：安全检查")
	private Integer taskType;

	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@ApiModelProperty("任务开始时间")
	private Date taskTime;

	@ApiModelProperty("任务工时")
	private Integer taskWorkHours;

	@ApiModelProperty("交换人id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long exchangePersonId;

	@ApiModelProperty("交换人名称")
	private String exchangePersonName;

	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@ApiModelProperty("交换人变更提交时间")
	private Date exchangeChangeTime;

	@ApiModelProperty("交换人原因说明")
	private String exchangeSeason;

	@ApiModelProperty("被交换人id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long replyPersonId;

	@ApiModelProperty("被交换人名称")
	private String replyPersonName;

	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@ApiModelProperty("被交换人变更时间")
	private Date replyChangeTime;

	@ApiModelProperty("被交换人拒绝原因说明")
	private String replyRefuseSeason;

	@ApiModelProperty("变更状态 0：待同意 1：同意 2：拒绝 默认待同意")
	private Integer changeStatus;

	@ApiModelProperty("头像")
	private String avatar;

	@ApiModelProperty("部门id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long deptId;

	@ApiModelProperty("部门名称")
	private String deptName;

	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty("位置id")
	private Long siteId;

	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty("站点id")
	private Long stationId;

}
