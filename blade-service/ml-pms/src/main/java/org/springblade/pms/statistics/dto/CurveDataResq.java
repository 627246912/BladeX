package org.springblade.pms.statistics.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author bond
 * @date 2020/5/16 13:32
 * @desc
 */
@Data
public class CurveDataResq {

	@ApiModelProperty(value = "数据项ID")
	private String itemId;
	@ApiModelProperty(value = "数据项名称")
	private String itemName;
	@ApiModelProperty(value = "单位")
	private String unit;

	@ApiModelProperty(value = "时间")
	private String time;


	@ApiModelProperty(value = "X轴数据")
	List<Object> xvals;
	@ApiModelProperty(value = "Y轴数据")
	List<Object> yvals;


}
