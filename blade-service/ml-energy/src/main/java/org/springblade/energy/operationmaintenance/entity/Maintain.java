package org.springblade.energy.operationmaintenance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 保养实体类
 *
 * @author CYL
 * @since 2020-07-23
 */
@Data
@TableName("t_maintain")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Maintain对象", description = "保养对象")
public class Maintain extends TenantEntity {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "资源类型")
	private Integer sourceType;

	@ApiModelProperty(value = "位置区域id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long siteId;

	@ApiModelProperty(value = "站点id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long stationId;

	@ApiModelProperty(value = "设备id")
	private String equipmentNo;

	@ApiModelProperty(value = "指派责任人id")
	private String assignedPersonId;

	@ApiModelProperty("位置名称")
	private String siteName;

	@ApiModelProperty("站点名称")
	private String stationName;

	@ApiModelProperty("设备名称")
	private String equipmentName;

	@ApiModelProperty("责任人名称")
	private String assignedPersonName;

	@ApiModelProperty(value = "保养周期")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long maintainCycle;

	@ApiModelProperty(value = "是否启用 0：未启用 1：启用")
	private Integer isStart;

	@ApiModelProperty(value = "启动日期")
	@DateTimeFormat(
		pattern = "yyyy-MM-dd"
	)
	@JsonFormat(
		pattern = "yyyy-MM-dd"
	)
	private Date startDate;

	@ApiModelProperty(value = "工时")
	private Integer workHours;

	@ApiModelProperty(value = "派单类型 0：自动 1：选择")
	private Integer assignedType;

	@ApiModelProperty(value = "是否外包")
	private Integer isOutsourcing;

	@ApiModelProperty(value = "周期开始时间")
	@DateTimeFormat(
		pattern = "HH:mm:ss"
	)
	@JsonFormat(
		pattern = "HH:mm:ss"
	)
	private Date cycleStart;

	@ApiModelProperty(value = "保养任务提前派放的时间")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long maintainBefore;

	@ApiModelProperty(value = "保养任务过期的时间")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long maintainAfter;

	@ApiModelProperty(value = "下一次检查时间")
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm"
	)
	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm"
	)
	private Date nextTime;

	@ApiModelProperty(value = "下一次提醒时间")
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm"
	)
	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm"
	)
	private Date remindTime;

	private Integer isAdmin;
}
