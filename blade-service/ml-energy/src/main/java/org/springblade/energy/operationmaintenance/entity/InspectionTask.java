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

import java.util.Date;

/**
 * 巡检记录实体类
 *
 * @author CYL
 * @since 2020-08-05
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_inspection_task")
@ApiModel(value = "巡检记录实体类", description = "巡检记录对象")
public class InspectionTask extends TenantEntity {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "任务编号")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long taskId;

	@ApiModelProperty(value = "任务状态  0:未检 1:进行中 2:暂停 3:已检 4:已取消")
	private String taskStatus;

	@ApiModelProperty(value = "实际开始时间")
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	private Date actualStartTime;

	@ApiModelProperty(value = "实际完成时间")
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	private Date actualEndTime;

	@ApiModelProperty("过期时间")
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	private Date expiredTime;

	@ApiModelProperty("过期状态 0:未过期,1:已过期 默认1")
	private Integer recordStatus;

	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	//@ApiModelProperty("记录过期时间")
	private Date abnormalTime;

	@ApiModelProperty("设备名称")
	private String equipmentName;

	@ApiModelProperty("责任人名称")
	private String assignedPersonName;

	@ApiModelProperty("位置名称")
	private String siteName;

	@ApiModelProperty("本次检查时间")
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm"
	)
	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm"
	)
	private Date taskTime;

	@ApiModelProperty("任务类型 1：巡检 2：保养")
	private Integer taskType;

	@ApiModelProperty("资源类型 1:电 2:水 3:气 4:重点能耗 5:安全巡视")
	private Integer sourceType;

	@ApiModelProperty("任务最终完成情况 0：已完成 1：未完成 默认1")
	private Integer accomplish;

	@ApiModelProperty("负责人id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long assignedPersonId;

	@ApiModelProperty("检查周期")
	private Long inspectionCycle;

	@ApiModelProperty("检查次数")
	@JsonSerialize(using = ToStringSerializer.class)
	private Integer inspectionNumber;

	@ApiModelProperty("设备id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long equipmentNo;

	@ApiModelProperty("站点id")
	private String stationId;

	@ApiModelProperty("站点名称")
	private String stationName;

	@ApiModelProperty("位置id")
	private String siteId;

	@ApiModelProperty("责任人电话")
	private String assignedPhone;

	@ApiModelProperty("标准工时")
	private Integer workHours;

	@ApiModelProperty("期望工时")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long expectHours;

	@ApiModelProperty("实际总工时")
	private String actualWorkHour;

	@ApiModelProperty("异常数")
	private Integer abnormalNumber;

	@ApiModelProperty("报修数")
	private Integer repairNumber;

	@ApiModelProperty("巡检计划类型 1:巡检 2:巡查")
	private Integer inspectionType;

	@ApiModelProperty("质量体系巡检项")
	private String inspectionItem;

	@ApiModelProperty("巡检项总数")
	private Integer inspectionItemCount;

	@ApiModelProperty("已检项")
	private Integer checkItem;

	@ApiModelProperty("未检查项")
	private Integer UncheckItem;

	@ApiModelProperty("时间段")
	private String timeQuantum;

	@ApiModelProperty("保养周期")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long maintainCycle;

	@ApiModelProperty("历史状态")
	private String historyStatus;

	@ApiModelProperty("上级领导id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long superiorsId;

	@ApiModelProperty("进度跟踪")
	private String progressTrack;

	@ApiModelProperty("是否异常 0:正常 1：异常 默认正常")
	private Integer isAbnormal;

	private Integer isCount;

}
