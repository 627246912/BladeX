package org.springblade.energy.operationmaintenance.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InspectionDateCountVo {

	/**
	 * 任务总条数
	 */
	private String taskCount;

	/**
	 * 任务完成率
	 */
	private String completeCountRate;
}
