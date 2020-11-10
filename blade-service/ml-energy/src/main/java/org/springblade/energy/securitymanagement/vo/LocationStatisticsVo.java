package org.springblade.energy.securitymanagement.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocationStatisticsVo {

	/**
	 * 任务条数
	 */
	private String taskCount;

	/**
	 * 完成条数
	 */
	private String accomplishCount;

	/**
	 * 过期条数
	 */
	private String expiredCount;

	/**
	 * 过期率
	 */
	private String expirationRate;

	/**
	 * 位置名称
	 */
	private String siteName;
}
