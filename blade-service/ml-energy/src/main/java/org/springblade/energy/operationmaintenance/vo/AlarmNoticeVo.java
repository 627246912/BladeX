package org.springblade.energy.operationmaintenance.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
public class AlarmNoticeVo {

	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;

	/**
	 * 标题
	 */
	private String title = "物联告警";

	/**
	 * 告警日期
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date alarmTime;

	/**
	 * 内容
	 */
	private String alarmContent;

	/**
	 * 告警等级
	 */
	private Integer level;

}
