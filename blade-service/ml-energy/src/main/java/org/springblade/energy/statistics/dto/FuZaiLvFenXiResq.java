package org.springblade.energy.statistics.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bond
 * @date 2020/5/25 16:12
 * @desc 负载率列表
 */
@Data
public class FuZaiLvFenXiResq {
	@ApiModelProperty(value = "监测名称")
	private String mname;

	@ApiModelProperty(value = "额定容量")
	private String capacity;
	@ApiModelProperty(value = "二次侧输出平均功率")
	private String avgOutPowerRate;
	@ApiModelProperty(value = "二次侧输出平均功率因数")
	private String avgOutPowerFactor;
	@ApiModelProperty(value = "平均负载率")
	private String avgLoadRate;
	@ApiModelProperty(value = "二次侧峰值功率")
	private String topPowerRate;
	@ApiModelProperty(value = "二次侧峰值功率因数")
	private String topPowerFactor;
	@ApiModelProperty(value = "峰值负载率")
	private String topLoadRate;
	@ApiModelProperty(value = "最高峰值时间")
	private String topTime;
	@ApiModelProperty(value = "最佳负载率")
	private String destLoadRate;
}
