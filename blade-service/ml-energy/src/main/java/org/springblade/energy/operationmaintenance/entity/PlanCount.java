package org.springblade.energy.operationmaintenance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springblade.core.tenant.mp.TenantEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_plan_count")
@ApiModel(value = "PlanCount", description = "PlanCount对象")
public class PlanCount extends TenantEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty("位置id")
	private Long siteId;

	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty("站点id")
	private Long stationId;

	@ApiModelProperty("计划id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long planId;

	@ApiModelProperty("任务类型 0:巡检 1:保养 2:维修 3:安全巡查")
	private Integer taskType;

	@ApiModelProperty("任务时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date taskTime;

	@ApiModelProperty("任务负责人")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long taskResponsible;

	@ApiModelProperty("是否admin")
	private Integer isAdmin;
}
