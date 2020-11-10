package org.springblade.energy.statistics.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bond
 * @date 2020/5/25 16:12
 * @desc 电压波动列表
 */
@Data
public class DianYaBoDongResq {
	@ApiModelProperty(value = "监测名称")
	private String mname;

	@ApiModelProperty(value = "A相正波动次数")
	private Integer uaPtimes;
	@ApiModelProperty(value = "B相正波动次数")
	private Integer ubPtimes;
	@ApiModelProperty(value = "C相正波动次数")
	private Integer ucPtimes;

	@ApiModelProperty(value = "A相正波动最大幅度")
	private String maxUaPval;
	@ApiModelProperty(value = "B相正波动最大幅度")
	private String maxUbPval;
	@ApiModelProperty(value = "C相正波动最大幅度")
	private String maxUcPval;


	@ApiModelProperty(value = "A相负波动次数")
	private Integer uaNtimes;

	@ApiModelProperty(value = "B相负波动次数")
	private Integer ubNtimes;
	@ApiModelProperty(value = "C相负波动次数")
	private Integer ucNtimes;

	@ApiModelProperty(value = "A相负波动最大幅度")
	private String maxUaNval;
	@ApiModelProperty(value = "B相负波动最大幅度")
	private String maxUbNval;
	@ApiModelProperty(value = "C相负波动最大幅度")
	private String maxUcNval;
}
