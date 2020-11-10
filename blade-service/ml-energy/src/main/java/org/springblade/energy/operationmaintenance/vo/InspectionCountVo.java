package org.springblade.energy.operationmaintenance.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InspectionCountVo {

	/**
	 * 任务总数
	 */
	private String taskCount;

	/**
	 * 过期总数
	 */
	private String expiredCount;

	/**
	 * 过期总数率
	 */
	private String expiredCountRate;

	/**
	 * 完成总数
	 */
	private String completeCount;

	/**
	 * 完成总数率
	 */
	private String completeCountRate;

	/**
	 * 过期完成总数
	 */
	private String expiredCompleteCount;

	/**
	 * 巡检项总数
	 */
	private String inspectionItemCount;

	/**
	 * 报修总数
	 */
	private String repairItemCount;

	/**
	 * 任务过期率
	 */
	private String taskExpiredRate;

	/**
	 * 范围统计
	 */
	private Map<String, InspectionDateCountVo> range;

	/**
	 * 位置统计
	 */
	private List<InspectionSiteCountVo> site;


}
