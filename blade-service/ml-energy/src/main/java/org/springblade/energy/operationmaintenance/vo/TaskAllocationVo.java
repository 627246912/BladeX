package org.springblade.energy.operationmaintenance.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaskAllocationVo {

	/**
	 * 用户id
	 */
	private String userId;

	/**
	 * 用户名称
	 */
	private String userName;

	/**
	 * 用户头像
	 */
	private String userAvatar;

	/**
	 * 如果忙碌 显示任务名称
	 */
	private String userTaskName;

	/**
	 * 任务状态  0：休息  1：空闲  2：忙碌  3：未上班 4：已下班  默认休
	 */
	private Integer status;

	/**
	 * 班次
	 */
	private String shift;

	/**
	 * 班次编号
	 */
	private Integer shiftNumber;

	/**
	 * 派单优先级 0：高 1：中 2：低
	 */
	private Integer level;

	/**
	 * 当天已执行的任务数
	 */
	private Long taskCount;

	/**
	 * 工作开始时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-Mm-dd HH:mm:ss")
	private Date workStartTime;

	/**
	 * 工作结束时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-Mm-dd HH:mm:ss")
	private Date workEndsTime;

	/**
	 * 工作开始时间戳
	 */
	private Long workStartTimestamp;

	/**
	 * 工作结束时间戳
	 */
	private Long workEndsTimestamp;

}
