package org.springblade.energy.energymanagement.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bond
 * @date 2020/4/29 9:09
 * @desc
 */
@Data
public class ConsumeItemResq {

	@JsonSerialize(
		using = ToStringSerializer.class
	)
	@ApiModelProperty("主键id")
	@TableId(
		value = "id",
		type = IdType.ASSIGN_ID
	)
	private Long id;

	@ApiModelProperty(value = "数据项id")
	private String itemId;

	@ApiModelProperty(value = "数据项名称")
	private String propertyName;

	@ApiModelProperty(value = "数据项别名")
	private String propertyAlias;

	@ApiModelProperty(value = "消耗类型")
	private Integer consumeType;

	@ApiModelProperty(value = "消耗类型名称")
	private String consumeName;

	@ApiModelProperty(value = "能源类型1供电2供水3供气")
	private Integer energyType;



}
