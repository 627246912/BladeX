package org.springblade.energy.securitymanagement.entity;

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
@TableName("t_safety_task")
@ApiModel(value = "PatrolRec对象", description = "APP--安全巡视")
public class SafetyTask extends TenantEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty("任务编号")
	private Long taskId;
	/* ************************************    app    ************************************ */
	@ApiModelProperty("任务类型 " + "{\"1\":\"安全检查\",\"2\":\"整改计划\"}  默认1")
	// {"1":"安全检查","2":"整改计划"} 默认1
	private Integer taskType;

	@ApiModelProperty("任务状态 " + " {\"0\":\"未检\",\"1\":\"进行中\",\"2\":\"暂停\",\"3\":\"已检\",\"4\":\"已取消\",\"5\":\"已超时\",\"6\":\"重检\"}  默认0")
	//默认0
	private String taskStatus;

	@ApiModelProperty("检查状态 " + "{\"0\":\"正常\",\"1\":\"异常\"} 默认0")
	//{"0":"正常","1":"异常"} 默认0
	private Integer inspectionStatus;

	@ApiModelProperty("异常内容")
	private String abnormalContent;

	@ApiModelProperty("报修状态 " + "{\"0\":\"不报修\",\"1\":\"报修\"} 默认0")
	//{"0":"不报修","1":"报修"} 默认0
	private Integer repairStatus;

	@ApiModelProperty("工作状态 " + "{\"0\":\"完成工作\",\"1\":\"暂停工作\",\"2\":\"继续工作\"} 默认0")
	//{"0":"完成工作","1":"暂停工作","2":"继续工作"} 默认0
	private Integer jobStatus;

	@ApiModelProperty("是否拍照 " + "{\"0\":\"否\",\"1\":\"是\"}  默认0")
	//{"0":"否","1":"是"} 默认0
	private Integer isPhoto;

	@ApiModelProperty("图片")
	private String image;

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

	@ApiModelProperty("审核状态 " + "{\"0\":\"待审批\",\"1\":\"已审批\",\"2\":\"已拒绝\"}")
	//{"0":"待审批","1":"已审批","2":"已拒绝"}
	private Integer checkStatus;

	@ApiModelProperty("拒绝理由")
	private String refuseReason;

	@ApiModelProperty("验收质量")
	private Integer checkAcceptQuality;

	@ApiModelProperty("满意度")
	private Integer satisfaction;

	@ApiModelProperty("审核人名称")
	private String checkPersonName;

	@ApiModelProperty("申请人名称")
	private String applicationPersonName;

	/* ************************************    app    ************************************ */

	/* ************************************   patrol   ************************************ */
	@ApiModelProperty("站点编号")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long stationId;

	@ApiModelProperty("位置编号")
	@JsonSerialize(using = ToStringSerializer.class)
	private String siteId;

	@ApiModelProperty("站点名称")
	private String stationName;

	@ApiModelProperty("位置名称")
	private String siteName;

	@ApiModelProperty("任务名称")
	private String taskName;

	@ApiModelProperty("检查标准")
	private String checkStandard;

	@ApiModelProperty("检查内容")
	private String checkContent;

	@ApiModelProperty("责任人编号")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long responsiblePersonId;

	@ApiModelProperty("责任人名称")
	private String responsiblePersonName;

	@ApiModelProperty("标准工时")
	private Integer standardTime;

	@ApiModelProperty("期望工时")
	private Integer expectTime;

	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@ApiModelProperty("巡视开始时间")
	private Date startTime;

	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@ApiModelProperty("巡视结束时间")
	private Date endsTime;
	/* ************************************   patrol   ************************************ */

	/* ************************************   record   ************************************ */
	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@ApiModelProperty("任务时间")
	private Date taskTime;

	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	//@ApiModelProperty("提醒时间")
	private Date remindTime;

	//@ApiModelProperty("是否第一次记录")
	//{"0":"否","1":"是"} 默认0
	private Integer isFirstTime;

	//@ApiModelProperty("是否最后一次记录")
	//{"0":"否","1":"是"} 默认0
	private Integer isLastTime;

	@ApiModelProperty("记录过期状态 +{\"0\":\"未过期\",\"1\":\"已过期\"} \t// 默认0")
	private Integer recordStatus;

	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@ApiModelProperty("记录过期时间")
	private Date abnormalTime;

	//@ApiModelProperty("推送状态")
	//{"0":"正常","1":"异常"} 默认0
	private Integer pushStatus;

	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	//@ApiModelProperty("推送异常时间")
	private Date pushAbnormalTime;
	/* ************************************   record   ************************************ */

	/* ************************************   rectify   ************************************ */
	@ApiModelProperty("整改部位")
	private String problemLocation;

	@ApiModelProperty("整改描述")
	private String problemDescription;

	@ApiModelProperty("建议")
	private String recommend;

	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@ApiModelProperty("整改时间")
	private Date rectifyTime;

	@ApiModelProperty("备注")
	private String remarks;

	/* ************************************   rectify   ************************************ */

	private Integer isCount;
}
