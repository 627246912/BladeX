package org.springblade.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bond
 * @date 2020/7/17 19:14
 * @desc
 */
@Data
public class DiagramTypeDto {
	@ApiModelProperty(value = "名称")
	private String name;
	@ApiModelProperty(value = "值")
	private String diagramType;
}
