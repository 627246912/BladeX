package org.springblade.pms.bigscreen.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bond
 * @date 2020/4/29 9:09
 * @desc
 */
@Data
public class ItemResq {


	@ApiModelProperty(value = "数据项id")
	private String itemId;
	@ApiModelProperty(value = "sid")
	private Integer sid;
//	@ApiModelProperty(value = "显示状态（0显示 1隐藏）")
//	private Boolean visible;
	@ApiModelProperty(value = "简称")
	private String shortname;
	@ApiModelProperty(value = "名称")
	private String name;
	@ApiModelProperty(value = "实时数据")
	private String realTimeValue;
	@ApiModelProperty(value = "单位")
	private String unit;
	@ApiModelProperty(value = "展示分类")
	private String showType;

	@ApiModelProperty(value = "数据项属性1传输遥测2传输遥信3传输遥调4传输遥控5计算遥测6计算遥信")
	private Integer stype;           //1传输遥测2传输遥信3传输遥调4传输遥控5计算遥测6计算遥信

}
