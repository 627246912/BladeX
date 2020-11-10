package org.springblade.energy.statistics.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bond
 * @date 2020/5/25 16:12
 * @desc 线损率分析列表
 */
@Data
public class XianSunResq {
	@ApiModelProperty(value = "时间")
	private String time;
	@ApiModelProperty(value = "监测名称")
	private String mname;
	@ApiModelProperty(value = "线损量")
	private String lossval;
	@ApiModelProperty(value = "线损率")
	private String lossrate;
}
