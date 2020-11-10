package org.springblade.energy.operationmaintenance.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaskTotalVo {

	/**
	 * 巡检总数
	 */
	private Long inspectionCount;

	/**
	 * 保养总数
	 */
	private Long maintainCount;

	/**
	 * 维修总数
	 */
	private Long repairCount;
}
