package org.springblade.energy.diagram.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bond
 * @date 2020/4/29 9:09
 * @desc
 */
@Data
public class DiagramItemResq {

	@ApiModelProperty(value = "id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;

	@ApiModelProperty(value = "排序")
	private String pindex;

	@ApiModelProperty(value = "数据项id")
	private String itemId;
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
	@ApiModelProperty(value = "数据项属性分类")
	private String showType;

	@ApiModelProperty(value = "产品属性code")
	private String ptype;

	@ApiModelProperty(value = "网关状态（0在线，1不在线）")
	private Integer status;

}
