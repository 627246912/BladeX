package org.springblade.energy.operationmaintenance.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApiTaskCountVo {

	private static final String ZERO = "0";
	/**
	 * 任务总数
	 */
	private String taskCount = ZERO;

	/**
	 * 完成任务总数
	 */
	private String completeCount = ZERO;

	/**
	 * 及时完成率
	 */
	private String timelyCompleteRate = ZERO;

	/**
	 * 抢修完成数
	 */
	private String repairCompleteCount = ZERO;

	/**
	 * 抢修实际工时
	 */
	private String repairActualWorkHours = ZERO;

	/**
	 * 抢修及时完率
	 */
	private String repairTimelyCompleteRate = ZERO;

	/**
	 * 巡检完成数
	 */
	private String inspectionCompleteCount = ZERO;

	/**
	 * 巡检实际工时
	 */
	private String inspectionActualWorkHours = ZERO;

	/**
	 * 巡检过期数
	 */
	private String inspectionExpiredCount = ZERO;

	/**
	 * 保养完成数
	 */
	private String maintainCompleteCount = ZERO;

	/**
	 * 保养实际工时
	 */
	private String maintainActualWorkHours = ZERO;

	/**
	 * 保养过期数
	 */
	private String maintainExpiredCount = ZERO;

}
