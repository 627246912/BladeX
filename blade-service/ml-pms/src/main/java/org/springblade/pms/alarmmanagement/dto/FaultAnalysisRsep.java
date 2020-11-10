package org.springblade.pms.alarmmanagement.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bond
 * @date 2020/5/6 18:42
 * @desc
 */
@Data
public class FaultAnalysisRsep {

	@ApiModelProperty(value = "站点id")
	private Long stationId;
	@ApiModelProperty(value = "站点名称")
	private String stationName;

	@ApiModelProperty(value = "位置id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long siteId;

	@ApiModelProperty(value = "位置名称")
	private String siteName;

	@ApiModelProperty(value = "设备id")
	private Long equipmentId;
	@ApiModelProperty(value = "设备名称")
	private String equipmentName;

	@ApiModelProperty(value = "告警等级Id")
	private Integer alarmLevel;
	@ApiModelProperty(value = "告警等级名称")
	private String alarmLevelName;

	@ApiModelProperty(value = "告警类型Id")
	private Integer alarmType;
	@ApiModelProperty(value = "告警类型名称")
	private String alarmTypeName;

	@ApiModelProperty(value = "设备总数")
	private Integer equipmentNum;
	@ApiModelProperty(value = "告警数量")
	private Integer alarmNum;

//	@ApiModelProperty(value = "故障率")
//	private String failureRate;

	@ApiModelProperty(value = "停机次数")
	private Integer haltNum;

	@ApiModelProperty(value = "停机时间")
	private Double haltTime;

	@ApiModelProperty(value = "闭环率") //1-当天停机时间除以24小时
	private String closedLoop;

}
