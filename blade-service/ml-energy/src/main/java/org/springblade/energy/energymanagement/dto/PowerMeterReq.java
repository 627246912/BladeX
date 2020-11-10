package org.springblade.energy.energymanagement.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bond
 * @date 2020/6/16 16:30
 * @desc 供电计量管账
 */
@Data
public class PowerMeterReq{


	@ApiModelProperty(value = "站点id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long stationId;

	@ApiModelProperty(value = "位置id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long siteId;

	@ApiModelProperty(value = "电类型0中压，1低压 2直流")
	private String diagramType;
	/**
	 * 变比
	 */
	@ApiModelProperty(value = "变比")
	private Float ctratio;

	/**
	 * 尖价格
	 */
	@ApiModelProperty(value = "尖价格")
	@TableField("topPrice")
	private Double topPrice;
	/**
	 * 峰价格
	 */
	@ApiModelProperty(value = "峰价格")
	@TableField("peakPrice")
	private Double peakPrice;
	/**
	 * 平价格
	 */
	@ApiModelProperty(value = "平价格")
	@TableField("flatPrice")
	private Double flatPrice;
	/**
	 * 谷价格
	 */
	@ApiModelProperty(value = "谷价格")
	@TableField("valleyPrice")
	private Double valleyPrice;

}
