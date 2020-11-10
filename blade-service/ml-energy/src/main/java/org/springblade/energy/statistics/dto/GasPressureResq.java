package org.springblade.energy.statistics.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bond
 * @date 2020/7/23 16:20
 * @desc
 */
@Data
public class GasPressureResq {
	@ApiModelProperty(value = "时间")
	private String time;
	@ApiModelProperty(value = "名称")
	private String productName;
	@ApiModelProperty(value = "最大值")
	private String maxval;

	@ApiModelProperty(value = "最小值")
	private String minval;
	@ApiModelProperty(value = "当前值")
	private String val;

}
