package org.springblade.energy.statistics.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bond
 * @date 2020/5/25 16:12
 * @desc 负载率列表
 */
@Data
public class YunXingReportResq {
	@ApiModelProperty(value = "时间")
	private String time;
	@ApiModelProperty(value = "输出平均功率")
	private String avgPower;
	@ApiModelProperty(value = "平均负载率")
	private String avgLoadRate;
	@ApiModelProperty(value = "峰值功率")
	private String topPowerRate;
	@ApiModelProperty(value = "功率因数")
	private String powerFactor;
	@ApiModelProperty(value = "峰值负载率")
	private String topLoadRate;
	@ApiModelProperty(value = "最佳负载率")
	private String destLoadRate;
	@ApiModelProperty(value = "用电量")
	private String useVal;
}
