package org.springblade.energy.statistics.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bond
 * @date 2020/5/25 16:12
 * @desc 效率率分析列表
 */
@Data
public class XiaoLvResq {
	@ApiModelProperty(value = "时间")
	private String time;
	@ApiModelProperty(value = "一次侧输入电量")
	private Float oneEval ;
	@ApiModelProperty(value = "二次侧输出电量")
	private Float twoEval;
	@ApiModelProperty(value = "实际使用效率") //实际使用效率：低压总进线电量除以中压馈线输入总电量x100
	private String auseRate;
	@ApiModelProperty(value = "理论最佳使用效率") //实际使用效率 除以 理论运行值 x100
	private String luseRate;
}
