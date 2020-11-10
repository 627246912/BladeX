package org.springblade.energy.statistics.dto;

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

	@ApiModelProperty(value = "产品属性统一编码")
	private String propertyCode;

	@ApiModelProperty(value = "数据项ID")
	private String itemId;
	@ApiModelProperty(value = "数据项名称")
	private String itemName;
	@ApiModelProperty(value = "单位")
	private String unit;

	@ApiModelProperty(value = "告警上线")
	private Float uplimit;

	@ApiModelProperty(value = "告警下线")
	private Float lowlimit;


	@ApiModelProperty(value = "时间")
	private String time;


	@ApiModelProperty(value = "X轴数据")
	List<Object> xvals;
	@ApiModelProperty(value = "Y轴数据")
	List<Object> yvals;


}
