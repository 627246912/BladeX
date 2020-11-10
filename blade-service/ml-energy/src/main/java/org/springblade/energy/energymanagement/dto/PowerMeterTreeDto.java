package org.springblade.energy.energymanagement.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author bini
 * @date 2020/9/15 17:42
 * @desc 供电计量管账
 */
@Data
public class PowerMeterTreeDto extends PowerMeterDto {

	@ApiModelProperty(value = "显示节点id")
	private String viewId;

	@ApiModelProperty(value = "子节点")
	private List<PowerMeterTreeDto> nextLevelObjs;

	@ApiModelProperty(value = "节点级别")
	private Integer nodeLevel;


}
