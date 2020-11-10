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
public class RepairCountVo {

	/**
	 * 任务总数
	 */
	private String taskCount;

	/**
	 * 告警总数
	 */
	private String alertCount;

	/**
	 * 告警率
	 */
	private String alertCountRate;

	/**
	 * 报修总数
	 */
	private String repairCount;

	/**
	 * 报修率
	 */
	private String repairCountRate;

	/**
	 * 完成总数
	 */
	private String completeCount;

	/**
	 * 完成率
	 */
	private String completeCountRate;

	/**
	 * 处理优先级一般总数
	 */
	private String generalCount;

	/**
	 * 处理优先级一般 未处理总数
	 */
	private String generalUntreatedCount;

	/**
	 * 处理优先级一般 已处理总数
	 */
	private String generalProcessedCount;

	/**
	 * 处理优先级紧急总数
	 */
	private String urgentCount;

	/**
	 * 处理优先级紧急 未处理总数
	 */
	private String urgentUntreatedCount;

	/**
	 * 处理优先级紧急 已处理总数
	 */
	private String urgentProcessedCount;

	/**
	 * 范围统计
	 */
	private Map<String, RepairDateCountVo> range;

	/**
	 * 位置统计
	 */
	private List<RepairSiteCountVo> site;
}
