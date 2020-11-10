package org.springblade.energy.energymanagement.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bond
 * @date 2020/6/16 16:30
 * @desc 计量管账-能耗统计
 */
@Data
public class UsePowerDto {


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
	@ApiModelProperty(value = "时间")
	private String time;

	@ApiModelProperty(value = "用量")
	private Float totalVal=0f;

	@ApiModelProperty(value = "费用")
	private double totalCost=0;

	@ApiModelProperty(value = "尖用量")
	private Float toplVal=0f;
	@ApiModelProperty(value = "尖费用")
	private double topCost=0;

	@ApiModelProperty(value = "峰用量")
	private Float peakVal=0f;
	@ApiModelProperty(value = "峰费用")
	private double peakCost=0;

	@ApiModelProperty(value = "平用量")
	private Float flatVal=0f;
	@ApiModelProperty(value = "平费用")
	private double flatCost=0;

	@ApiModelProperty(value = "谷用量")
	private Float valleyVal=0f;
	@ApiModelProperty(value = "谷费用")
	private double valleyCost=0;

}
