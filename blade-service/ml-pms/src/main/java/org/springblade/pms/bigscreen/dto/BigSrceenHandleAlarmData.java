package org.springblade.pms.bigscreen.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bond
 * @date 2020/8/3 14:24
 * @desc
 */
@Data
public class BigSrceenHandleAlarmData {
	@ApiModelProperty(value = "名称")
	private String name;
	@ApiModelProperty(value = "数量")
	private Integer thisVal;
	@ApiModelProperty(value = "同比数量")
	private Integer lastVal;

}
