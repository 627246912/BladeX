package org.springblade.energy.statistics.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bond
 * @date 2020/7/3 16:52
 * @desc 部门能耗对标
 */
@Data
public class EnergyTarReport {
	@ApiModelProperty(value = "类别")
	private String energyType;

	@ApiModelProperty(value = "类别名称")
	private String energyName;

	@ApiModelProperty(value = "上升下降数据")
	private String val;
	@ApiModelProperty(value = "上升下降率")
	private String rate;
	@ApiModelProperty(value = "成本分析")
	private Float cost;

	@ApiModelProperty(value = "单价")
	private Float unitCost;

}
