package org.springblade.system.user.vo;

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
public class UserShiftVo {


	/**
	 * 用户
	 */
	private Long id;

	/**
	 * 用户名称
	 */
	private String name;


	/**
	 * 日
	 */
	private Integer day;


	/**
	 * 星期
	 */
	private String week;


	/**
	 * 班次
	 */
/*	{
		"0": "休息",
		"1": "早班",
		"2": "中班",
		"3": "晚班",
		"4": "全天"
	}*/
	private Integer shift;


	/**
	 * 职责范围
	 */

	/*	{
	    "0": "无",
		"1": "抢修单",
		"2": "保险单",
		"3": "巡查单"
	}*/
	private Integer resRange;


}
