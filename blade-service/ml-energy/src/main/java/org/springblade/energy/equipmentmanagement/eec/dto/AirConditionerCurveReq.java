package org.springblade.energy.equipmentmanagement.eec.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author bond
 * @date 2020/6/29 10:48
 * @desc
 */
@Data
public class AirConditionerCurveReq {
	private static final long serialVersionUID = 1L;

	/**
	 * 站点ID
	 */
	@ApiModelProperty(value = "站点ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long stationId;
	/**
	 * 位置ID
	 */
	@ApiModelProperty(value = "位置ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long siteId;

//	@ApiModelProperty(value = "系统图id")
//	@JsonSerialize(using = ToStringSerializer.class)
//	private Long diagramId;

	@ApiModelProperty(value = "系统图产品id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long diagramProductId;

	@ApiModelProperty(value = "数据类型：1耗电量，2耗气量，3耗水量，4单位面积空调能耗，5单位面积空调耗冷量，6空调系统能效比，7制冷系统能效比，8冷水机组运行效率")
	private Integer dataType;


	@ApiModelProperty(value = "日期类型 0:天 1:星期 2:月 3:年 4:近24小时 5:近7天 6:近30天 7:近12月 8:季度")
	private Integer dateType;

	@ApiModelProperty(value = "时间")
	private Date time;

	@ApiModelProperty(value = "数据类型用英文逗号分隔 0:当前查询日期数据 1:同比数据 2:环比数据")
	private String dataMolds;
}
