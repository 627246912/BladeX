package org.springblade.energy.energymanagement.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springblade.energy.energymanagement.entity.PowerMeter;

/**
 * @author bond
 * @date 2020/6/16 16:30
 * @desc 供电计量管账
 */
@Data
public class PowerMeterDto extends PowerMeter {


	@ApiModelProperty(value = "站点id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long stationId;
	@ApiModelProperty(value = "站点名称")
	private String stationName;

	@ApiModelProperty(value = "位置id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long siteId;
	@ApiModelProperty(value = "位置名称")
	private String siteName;

	@ApiModelProperty(value = "产品名")
	private String productcname;

	@ApiModelProperty(value = "用电单元ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long deptId;

	@ApiModelProperty(value = "用电单元")
	private String deptName;

	@ApiModelProperty(value = "系统图产品ID")
	private String diagramProductId;

	@ApiModelProperty(value = "用电类型名")
	private String electricTypeName;

	@ApiModelProperty(value = "用电类型key")
	private Integer electricTypekey;


}
