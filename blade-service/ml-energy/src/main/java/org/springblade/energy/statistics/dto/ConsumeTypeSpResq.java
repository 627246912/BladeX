package org.springblade.energy.statistics.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bond
 * @date 2020/7/6 11:29
 * @desc
 */
@Data
public class ConsumeTypeSpResq {

	@ApiModelProperty(value = "消耗类型")
	private Integer consumeType;

	@ApiModelProperty(value = "消耗类名称")
	private String consumeName;

	@ApiModelProperty(value = "能源类型1供电2供水3供气")
	private Integer energyType;

	@ApiModelProperty(value = "数值")
	private Float val;
}
