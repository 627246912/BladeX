package org.springblade.energy.energymanagement.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bond
 * @date 2020/6/16 16:30
 * @desc 供水计量管账
 */
@Data
public class UseWaterDto{


//	@ApiModelProperty(value = "站点id")
//	@JsonSerialize(using = ToStringSerializer.class)
//	private Long stationId;
//	@ApiModelProperty(value = "站点名称")
//	private String stationName;
//
//	@ApiModelProperty(value = "位置id")
//	@JsonSerialize(using = ToStringSerializer.class)
//	private Long siteId;
//	@ApiModelProperty(value = "位置名称")
//	private String siteName;

	@ApiModelProperty(value = "产品名")
	private String productcname;

	@ApiModelProperty(value = "用电单元")
	private String deptName;
	@ApiModelProperty(value = "用电单元ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long deptId;
//	@ApiModelProperty(value = "水表，燃气表等级")
//	private int grade;

	@ApiModelProperty(value = "时间")
	private String time;

	@ApiModelProperty(value = "用量")
	private Float val=0f;

	@ApiModelProperty(value = "费用")
	private double cost=0;

}
