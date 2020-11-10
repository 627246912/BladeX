package org.springblade.energy.securitymanagement.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DateStatisticsVo {
	/**
	 * 任务数
	 */
	private String taskCount;

	/**
	 * 及时完成率
	 */
	private String completionRate;
}
