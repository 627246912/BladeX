package org.springblade.energy.securitymanagement.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
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

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_patrol")
@ApiModel(value = "Patrol对象", description = "安全管理--安全检查")
public class Patrol extends TenantEntity implements Serializable {

	private static final long serialVersionUID = 1L;


	@ApiModelProperty("检查内容")
	private String checkContent;

	@ApiModelProperty("检查站点")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long stationId;

	@ApiModelProperty("检查位置")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long siteId;

	@ApiModelProperty("检查周期")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long checkCycle;

	@ApiModelProperty("标准工时")
	private Integer normWork;

	@ApiModelProperty("是否启动")
	private Integer isEnable;

	@ApiModelProperty("启动时间")
	private String enableDate;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty("指定责任人")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long responsible;

	@ApiModelProperty("检查周期起始时间")
	private String checkCycleStartDate;

	@ApiModelProperty("任务提前推送时长")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long taskPushAheadDuration;

	@ApiModelProperty("期望检查时长")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long checkDuration;

	@ApiModelProperty("标准详情")
	private String standardDetails;

	@ApiModelProperty("是否拍照")
	private Integer isPhoto;

	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@ApiModelProperty("下次检查时间")
	private Date nextTime;

	@ApiModelProperty("任务名称")
	private String taskName;

	@ApiModelProperty("站点名称")
	private String stationName;

	@ApiModelProperty("位置名称")
	private String siteName;

	@ApiModelProperty("责任人名称")
	private String responsibleName;

	private Integer isAdmin;


}
