package org.springblade.pms.bigscreen.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bond
 * @date 2020/7/18 10:48
 * @desc
 */
@Data
public class StationAlarmDataDto {


	@ApiModelProperty(value = "告警状态 0,一般1,故障2,事故： 在线状态3,离线4,在线")
	private int status;
	/**
	 * 站点id
	 */
	@ApiModelProperty(value = "站点id")
	@JsonSerialize(
		using = ToStringSerializer.class
	)
	private String stationId;

	@ApiModelProperty(value = "站点名称")
	private String stationName;
	/**
	 * 地址
	 */
	@ApiModelProperty(value = "地址")
	private String address;
	/**
	 * 告警内容
	 */
	@ApiModelProperty(value = "告警内容")
	private String alarmContent;
	/**
	 * 告警时间
	 */
	@ApiModelProperty(value = "告警时间")
	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	private String alarmTime;

	@ApiModelProperty(value = "设备状态")
	private String equipmentStatusName;
}
