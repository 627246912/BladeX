package org.springblade.energy.operationmaintenance.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InspectionQualityStandardVo {

	// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	/**
	 * 项目id
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private int itemId;

	/**
	 * 任务id
	 */
	private Long taskId;

	/**
	 * 任务类型
	 */
	private Integer taskType;

	/**
	 * 资源类型
	 */
	private Integer sourceType;

	/**
	 * 巡检项目
	 */
	private String item;

	/**
	 * 标准范围  巡检 供水、供气项字段
	 */
	private String standardRange;

	/**
	 * 检查类型 巡检 重点能耗项字段
	 */
	private String checkType;

	/**
	 * 机组工况 供水、供气项字段
	 */
	private String unitStatus;

	/**
	 * 机组工况结果
	 */
	private String unitStatusResult;

	/**
	 * 检查方法
	 */
	private Integer checkMethod;

	/**
	 * 设备
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long dev;

	private String devName;

	/**
	 * 巡查内容
	 */
	private String inspectionContent;

	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	//类别
	private Integer type;
	//部位
	private String location;
	//保养项目
	private String activityItem;
	//注意事项
	private String precautions;
	//工具与方法
	private String utilAndSkill;
	//判定基准
	private String judgementStandard;

	// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	/**
	 * 正常
	 */
	private String normal;

	/**
	 * 异常
	 */
	private String abnormal;

	/**
	 * 异常结果  无  有
	 */
	private String normalResult;

	/**
	 * 检查结果
	 */
	private String checkResult;

	/**
	 * 是否拍照 0：否 1：是
	 */
	private Integer isAppPhoto;

	/**
	 * 是否报修 0：否 1：是
	 */
	private Integer isRepair;

	/**
	 * 报修内容
	 */
	private String repairContent;

	/**
	 * 项是否已检 0：未检 1：已检
	 */
	private Integer isInspection;

	/**
	 * 图片
	 */
	private String images;

	/**
	 * 室外温度
	 */
	private String temperature;

	/**
	 * 时间段时间
	 */
	private String timeQuantumTime;

}
