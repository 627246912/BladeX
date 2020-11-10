package org.springblade.energy.operationmaintenance.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RepairSiteCountVo {

	/**
	 * 任务总数
	 */
	private String taskCount;

	/**
	 * 完成总数
	 */
	private String completeCount;

	/**
	 * 未完成总数
	 */
	private String undoneCount;

	/**
	 * 完成率
	 */
	private String completeCountRate;

	/**
	 * 未完成率
	 */
	private String undoneCountRate;

	/**
	 *  及时完成率
	 */
	private String timelyCompleteRate;

	/**
	 * 标准工时
	 */
	private String standardWorkHours;

	/**
	 * 实际工时
	 */
	private String actualWorkHours;

	/**
	 * 位置名称
	 */
	private String siteName;

	/**
	 *  验收质量 合格总数
	 */
	private String passCount;

	/**
	 * 验收质量 不合格总数
	 */
	private String failCount;

	/**
	 * 满意度 差总数
	 */
	private String differCount;

	/**
	 * 满意度 一般总数
	 */
	private String generalCount;

	/**
	 * 满意度 满意总数
	 */
	private String satisfactionCount;

	/**
	 * 满意度 非常满意总数
	 */
	private String verySatisfactionCount;


}
