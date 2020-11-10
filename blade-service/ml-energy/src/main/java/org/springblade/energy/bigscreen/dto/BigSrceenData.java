package org.springblade.energy.bigscreen.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bond
 * @date 2020/8/3 14:24
 * @desc
 */
@Data
public class BigSrceenData {
	@ApiModelProperty(value = "名称")
	private String name;
	@ApiModelProperty(value = "用量")
	private Float thisVal;
	@ApiModelProperty(value = "同比用量")
	private Float lastVal;

	@ApiModelProperty(value = "单位")
	private String unit;

	@ApiModelProperty(value = "用量费用")
	private Float thisCost;
	@ApiModelProperty(value = "同比用量费用")
	private Float lastCost;

}
