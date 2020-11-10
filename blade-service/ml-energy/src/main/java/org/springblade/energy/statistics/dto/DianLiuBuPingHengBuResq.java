package org.springblade.energy.statistics.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bond
 * @date 2020/5/25 16:12
 * @desc 三相电流不平衡度分析列表
 */
@Data
public class DianLiuBuPingHengBuResq {
	@ApiModelProperty(value = "数据项")
	private String item;
	@ApiModelProperty(value = "监测名称")
	private String mname;
	@ApiModelProperty(value = "最高不平衡度")
	private Object maxval;
	@ApiModelProperty(value = "最低不平衡度")
	private Object minval;
	@ApiModelProperty(value = "平均不平衡度")
	private Object avgval;
}
