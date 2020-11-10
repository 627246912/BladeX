package org.springblade.energy.operationmaintenance.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProgressTrackVo {

	/**
	 * 进程
	 */
	private Integer process;

	/**
	 * 时间
	 */
	private String time;

	/**
	 * 状态   0:未响应  1：已相应
	 */
	private Integer status;
}
