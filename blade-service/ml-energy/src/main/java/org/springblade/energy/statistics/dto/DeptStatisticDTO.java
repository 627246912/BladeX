package org.springblade.energy.statistics.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bini
 * @date 2020/10/09 17:49
 * @desc 部门能耗
 */
@Data
public class DeptStatisticDTO {

	@ApiModelProperty(value = "部门名")
	private String deptName;

	@ApiModelProperty(value = "部门id")
	private Long deptId;

	@ApiModelProperty(value = "能耗值")
	private Float powerVal;

}
