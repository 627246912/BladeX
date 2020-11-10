package org.springblade.energy.websocket.parameterReq;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bond
 * @date 2020/4/17 19:44
 * @desc
 */
@Data
@ApiModel(value = "Diagram对象", description = "系统图展示及数据项数据")
public class ChartDataReq {

	@ApiModelProperty(value = "接口")
	private String method;

	@ApiModelProperty(value = "站点ID")
	@JsonSerialize(
		using = ToStringSerializer.class
	)
	private Long stationId;

	@ApiModelProperty(value = "位置ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long siteId;

	@ApiModelProperty(value = "图形分类")
	private String diagramType;
}
