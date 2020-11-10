package org.springblade.energy.equipmentmanagement.eec.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bond
 * @date 2020/7/10 18:47
 * @desc
 */
@Data
public class AirConditionerData {
	//1耗电量，2耗气量，3耗水量，4单位面积空调能耗，5单位面积空调耗冷量，6空调系统能效比，7制冷系统能效比，8冷水机组运行效率")


	@ApiModelProperty(value = "值")
	private String val;

	@ApiModelProperty(value = "数据类型")
	private Integer type;

	@ApiModelProperty(value = "数据名称")
	private String name;




//	@ApiModelProperty(value = "耗电量")
//	private String wq;
//
//	@ApiModelProperty(value = "耗气量")
//	private String gq;
//
//	@ApiModelProperty(value = "耗水量")
//	private String sq;
//
//	@ApiModelProperty(value = "单位面积空调能耗")
//	private String eca;
//
//	@ApiModelProperty(value = "位面积空调耗冷量")
//	private String cca;
//
//	@ApiModelProperty(value = "空调系统能效比")
//	private String eers;
//
//	@ApiModelProperty(value = "制冷系统能效比")
//	private String eerr;
//
//	@ApiModelProperty(value = "冷水机组运行效率")
//	private String cop;

}
