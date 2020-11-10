package org.springblade.energy.bigscreen.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bond
 * @date 2020/8/3 14:24
 * @desc
 */
@Data
@ApiModel(value = "位置能耗", description = "位置能耗")
public class Nenghaogongshi {
	@ApiModelProperty(value = "位置名称")
	private String siteName;

	@ApiModelProperty(value = "本月用电")
	private String thisMothVal;

	@ApiModelProperty(value = "上月用电")
	private String lastMothVal;

	@ApiModelProperty(value = "单位")
	private String unit;

}
