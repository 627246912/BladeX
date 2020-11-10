package org.springblade.pms.bigscreen.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bond
 * @date 2020/9/17 10:42
 * @desc 站点大屏数据统计
 */
@Data
public class DataStaResq {
	@ApiModelProperty(value = "用户id")
	private String userId;
	@ApiModelProperty(value = "用户名称")
	private String userName;

	@ApiModelProperty(value = "端口")
	private String ports;
	@ApiModelProperty(value = "rtuids")
	private String rtuids;

//	@ApiModelProperty(value = "电流单位")
//	private String iunit;

	@ApiModelProperty(value = "总电流")
	private Double ival;

	@ApiModelProperty(value = "用户总电流")
	private Double userIval;

//	@ApiModelProperty(value = "能耗单位")
//	private String eunit;
	@ApiModelProperty(value = "本月能耗")
	private Float eval;
	@ApiModelProperty(value = "用户本月能耗")
	private Float userEval;


}
