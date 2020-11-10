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
 * 维修实体类
 *
 * @author CYL
 * @since 2020-07-23
 */
@Data
@TableName("t_repair")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Repair对象", description = "维修对象")
public class Repair extends TenantEntity {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "位置区域id")
	private String siteId;

	@ApiModelProperty(value = "站点id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long stationId;

	@ApiModelProperty(value = "设备编号")
	private String equipmentNo;

	@ApiModelProperty(value = "报修内容")
	private String repairContent;

	@ApiModelProperty(value = "设备状态 ")
	private Integer eventStatus;

	@ApiModelProperty(value = "报修优先级 1：一般 2：紧急")
	private Integer priority;

	@ApiModelProperty(value = "派单类型 0：自动 1：选择")
	private Integer assignedType;

	@ApiModelProperty(value = "指派责任人id")
	private String assignedPersonId;

	@ApiModelProperty(value = "协助人id")
	private String assistPersonId;

	@ApiModelProperty(value = "报修人id")
	private String releaseRepairPersonId;

	@ApiModelProperty(value = "完成工时")
	private Integer finishWorkTime;

	@ApiModelProperty(value = "故障照片")
	private String faultPictures;

	@ApiModelProperty("修复照片")
	private String repairPicture;

	@ApiModelProperty(value = "维修类型 0:未开始 1:维修中 2:暂停 3:已完成 4:重检")
	private Integer repairType;

	@ApiModelProperty(value = "任务id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long taskId;

	@ApiModelProperty("质量id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long qualityId;

	@ApiModelProperty(value = "任务类型 0: 巡查 1：保养 2：安全检查 3：人工报修")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long taskType;

	@ApiModelProperty(value = "任务状态 0：未修复 1：已修复 默认未修复")
	private String taskStatus;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "报修时间")
	private Date repairTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty("派单时间")
	private Date dispatchTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty("开始时间")
	private Date startTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "修复时间")
	private Date restoreTime;

	@ApiModelProperty("是否指定时间完成 0:未过期 1已过期 默认已过期")
	private Integer isExpired;

	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty("审核人id")
	private Long checkPerson;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty("审核时间")
	private Date checkTime;

	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty("申请人id")
	private Long applicationPerson;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty("申请时间")
	private Date applicationTime;

	@ApiModelProperty("审核状态 0:待审批 1：已审批 默认待审批")
	private Integer checkStatus;

	@ApiModelProperty("验收质量 0:合格 1：不合格 默认合格")
	private Integer checkAcceptQuality;

	@ApiModelProperty("满意度")
	private Integer satisfaction;

	@ApiModelProperty("巡检计划类型 1：巡检 2；巡查")
	private Integer inspectionType;

	@ApiModelProperty("异常描述")
	private String remarks;

	@ApiModelProperty("问题描述")
	private String problemDetail;

	@ApiModelProperty("故障历史")
	private String faultHistory;

	@ApiModelProperty("站点名称")
	private String stationName;

	@ApiModelProperty("位置名称")
	private String siteName;

	@ApiModelProperty("设备名称")
	private String equipmentName;

	@ApiModelProperty("报修人名称")
	private String releaseRepairPersonName;

	@ApiModelProperty("报修人电话")
	private String releaseRepairPersonPhone;

	@ApiModelProperty("责任人名称")
	private String assignedPersonName;

	@ApiModelProperty("协助人名称")
	private String assistPersonName;

	@ApiModelProperty("审核人名称")
	private String checkPersonName;

	@ApiModelProperty("类型 1：报修  2：告警")
	private Integer type;

	@ApiModelProperty("处理优先级 1：一般 2：紧急")
	private Integer handleLevel;

	@ApiModelProperty("告警等级")
	private Integer alertLevel;

	@ApiModelProperty("告警id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long alertId;

	@ApiModelProperty("是否已创建 0:未创建 1：已创建  默认未创建")
	private Integer isCreate;

	@ApiModelProperty("是否处理 0：未处理 1：已处理 默认未处理")
	private Integer isHandle;

	@ApiModelProperty("是否驳回 0：审核中 1:已驳回 2：已派单 默认审核中")
	private Integer isTurnDown;

}
