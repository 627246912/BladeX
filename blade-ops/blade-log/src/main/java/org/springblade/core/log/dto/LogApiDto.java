package org.springblade.core.log.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author bond
 * @date 2020/9/25 15:58
 * @desc
 */
@Data
public class LogApiDto {

	protected String methodName;
	protected String serviceId;
	protected String createBy;
	private String title;

	protected String rtuidcb;

	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	protected Date startTime;

	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	protected Date endTime;

}
