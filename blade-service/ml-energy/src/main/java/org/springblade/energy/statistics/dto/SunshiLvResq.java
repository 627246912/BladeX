package org.springblade.energy.statistics.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bond
 * @date 2020/5/25 16:12
 * @desc 效率率分析列表
 */
@Data
public class SunshiLvResq {
	@ApiModelProperty(value = "时间")
	private String time;
	@ApiModelProperty(value = "一次侧输入功率")
	private Float oneEval ;
	@ApiModelProperty(value = "二次侧输出功率")
	private Float twoEval;
	@ApiModelProperty(value = "实际损失率") //实际损失率：低压总进线有用总功率除以中压馈线输入有用总功率x100
	private String auseRate;
	@ApiModelProperty(value = "理论最佳损失率") //实际损失率 除以 理论运行值 x100
	private String luseRate;
}
