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
public class InspectionRouteVo {

	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	private Date startTime;

	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	private Date endsTime;

	/**
	 * 位置名称
	 */
	private String siteName;

	/**
	 * 耗时
	 */
	private String timeConsuming;

	/**
	 * 状态
	 */
	private String inspectionStatus;

	/**
	 * 状态码
	 */
	private String code;


}
