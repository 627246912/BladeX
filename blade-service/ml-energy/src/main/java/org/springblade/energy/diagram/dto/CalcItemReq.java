package org.springblade.energy.diagram.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bond
 * @date 2020/5/15 15:44
 * @desc
 */
@Data
public class CalcItemReq {
	/**
	 * 网关id
	 */
	@ApiModelProperty(value = "网关id")
	private String did;
	/**
	 * 数据项
	 */
	@ApiModelProperty(value = "数据项")
	private String itemId;
	/**
	 * 功能属性(四遥)
	 */
	@ApiModelProperty(value = "功能属性(四遥)")
	private String ftype;
	@ApiModelProperty(value = "计算值规则，即JS脚本")
	private String formula;           //计算值规则，即JS脚本，数据项对应类别stype等于5或6
	@ApiModelProperty(value = "计算规则，主计算元素，单个数据项id")
	private String relid;            //计算规则，主计算元素，单个数据项id

}
