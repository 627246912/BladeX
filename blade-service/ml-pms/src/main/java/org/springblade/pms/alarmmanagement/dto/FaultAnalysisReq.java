package org.springblade.pms.alarmmanagement.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author bond
 * @date 2020/5/6 18:42
 * @desc
 */
@Data
public class FaultAnalysisReq {

	@JsonSerialize(
		using = ToStringSerializer.class
	)
	@ApiModelProperty("主键id")
	private String id;
	@ApiModelProperty(value = "站点id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long stationId;

	@ApiModelProperty(value = "网关")
	private String gwId;

	@ApiModelProperty(value = "告警类型Id")
	private Integer alarmType;

	@ApiModelProperty(value = "告警等级")
	private Integer level;

	@ApiModelProperty(value = "统计开始时间")
	private Date startTime;
	@ApiModelProperty(value = "统计结束时间")
	private Date endTime;

	@ApiModelProperty(value = "处理状态")
	private Integer handle_status;
	@ApiModelProperty(value = "告警状态0未结束 1已结束")
	private Integer alarmStatus;


}
