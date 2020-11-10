package org.springblade.energy.statistics.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bond
 * @date 2020/5/14 13:58
 * @desc
 */
@Data
public class EquipmentResq {

	/**
	 * 产品名称
	 */
	@ApiModelProperty(value = "产品名称")
	private String productName;

	@ApiModelProperty(value = "系统图产品ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long diagramProductId;

}
