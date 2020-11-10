package org.springblade.energy.securitymanagement.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SecurityCountVo {

	/**
	 * 任务数
	 */
	private String taskCount;

	/**
	 * 隐患数
	 */
	private String hiddenCount;

	/**
	 * 完成任务数
	 */
	private String accomplishCount;

	/**
	 * 过期任务数
	 */
	private String expiredCount;

	/**
	 * 任务完成率
	 */
	private String accomplishRate;

	/**
	 * 任务过期率
	 */
	private String expirationRate;

	/**
	 * 及时完成率
	 */
	private String completionRate;

	/**
	 * 合格率
	 */
	private String passRate;

	/**
	 * 平均分
	 */
	private String score;

	/**
	 * 范围统计
	 */
	private Map<String, DateStatisticsVo> range;

	/**
	 * 位置统计
	 */
	private List<LocationStatisticsVo> site;
}
