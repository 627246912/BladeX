package org.springblade.energy.statistics.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bond
 * @date 2020/5/16 13:32
 * @desc
 */
@Data
public class CurveDataTyReq extends CurveDataReq{

	@ApiModelProperty(value = "理论值")
	private Double theoryval;
}
