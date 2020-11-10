package org.springblade.energy.operationmaintenance.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InspectionSiteCountVo {

	/**
	 * 任务总数
	 */
	private String taskCount;

	/**
	 * 过期总数
	 */
	private String expiredCount;

	/**
	 * 完成总数
	 */
	private String completeCount;

	/**
	 * 过期总数率
	 */
	private String expiredCountRate;

	/**
	 * 及时完成率
	 */
	private String timelyCompleteRate;

	/**
	 * 完成总数率
	 */
	private String completeCountRate;

	/**
	 * 位置名称
	 */
	private String siteName;

	/**
	 * 故障率
	 */
	private String abnormalRate;

	/**
	 * 标准工时
	 */
	private String standardWorkHours;

	/**
	 * 实际工时
	 */
	private String actualWorkHours;
}
