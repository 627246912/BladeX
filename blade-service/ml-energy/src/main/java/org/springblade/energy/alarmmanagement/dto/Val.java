package org.springblade.energy.alarmmanagement.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bond
 * @date 2020/5/6 18:42
 * @desc
 */
@Data
public class Val {



	@ApiModelProperty(value = "Id")
	private Integer id;

	@ApiModelProperty(value = "名称")
	private String name;

	@ApiModelProperty(value = "数量")
	private Integer val;


}
