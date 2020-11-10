package org.springblade.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bond
 * @date 2020/5/14 16:32
 * @desc 产品属性统一code
 */
@Data
@ApiModel(value = "ProductProperty对象", description = "产品属性表")
public class Ptype {
	@ApiModelProperty(value = "1遥测2遥信3遥调4遥控5计算遥测6计算遥信")
	private String stype; //1遥测2遥信3遥调4遥控5计算遥测6计算遥信
	@ApiModelProperty(value = "属性code")
	private String ptype; //属性code
	@ApiModelProperty(value = "名称")
	private String name;
	@ApiModelProperty(value = "简称")
	private String cname;
	@ApiModelProperty(value = "单位")
	private String unit;
}
