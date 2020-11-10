package org.springblade.energy.energymanagement.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author bond
 * @date 2020/4/29 9:09
 * @desc
 */
@Data
public class ConsumeItemReq {

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

	@ApiModelProperty(value = "消耗类型")
	private Integer consumeType;

	@ApiModelProperty(value = "能源类型1供电2供水3供气")
	private Integer energyType;

	@ApiModelProperty(value = "数据项")
	private List<ConsumeItemResq> consumeItemResqList;

}
