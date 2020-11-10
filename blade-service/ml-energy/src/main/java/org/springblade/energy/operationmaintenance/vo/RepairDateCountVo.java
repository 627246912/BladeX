package org.springblade.energy.operationmaintenance.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RepairDateCountVo {

	/**
	 * 任务总数
	 */
	private String taskCount;

	/**
	 * 已完成总数
	 */
	private String completeCount;

	/**
	 * 未完成总数
	 */
	private String undoneCount;

}
