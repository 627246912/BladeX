package org.springblade.energy.statistics.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bond
 * @date 2020/6/15 17:54
 * @desc 用水列表
 */
@Data
public class GasDataResq {
	@ApiModelProperty(value = "时间")
	private String time;
	@ApiModelProperty(value = "位置I")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long siteId;
	@ApiModelProperty(value = "位置")
	private String siteName;
	@ApiModelProperty(value = "总用气量")
	private String val;

	@ApiModelProperty(value = "压力")
	private String pressure;

	@ApiModelProperty(value = "标准流量")
	private String normFlow;
	@ApiModelProperty(value = "工况流量")
	private String workFlow;

	@ApiModelProperty(value = "气温")
	private String gasTemp;

}
