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
public class WaterDataResq {
	@ApiModelProperty(value = "时间")
	private String time;
	@ApiModelProperty(value = "位置I")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long siteId;
	@ApiModelProperty(value = "位置")
	private String siteName;
	@ApiModelProperty(value = "总用水量")
	private String val;

	@ApiModelProperty(value = "水温")
	private String waterTemp;

	@ApiModelProperty(value = "瞬时流量")
	private String instFlow;
}
