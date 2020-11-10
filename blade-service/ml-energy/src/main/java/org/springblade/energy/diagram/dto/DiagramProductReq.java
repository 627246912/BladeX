package org.springblade.energy.diagram.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bond
 * @date 2020/4/1 11:14
 * @desc
 */
@Data
public class DiagramProductReq {
	@JsonSerialize(
		using = ToStringSerializer.class
	)
	private Long id;
	@ApiModelProperty(value = "产品Id")
	@JsonSerialize(
		using = ToStringSerializer.class
	)
	public Long productId;
	@ApiModelProperty(value = "产品父节点Id")
	public String parentId;

	@ApiModelProperty(value = "排序")
	private String pindex;

	@ApiModelProperty(value = "网关ID")
	private String did;

	@ApiModelProperty(value = "rtuidcb")
	private String rtuidcb;

	@ApiModelProperty(value = "产品别名")
	private String productcname;

	@ApiModelProperty(value = "页面布局")
	private String layout;

	@ApiModelProperty(value = "变压器中低压关联")
	private String mesolow;
	@ApiModelProperty(value = "用电单元")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long deptId;

	@ApiModelProperty(value = "水表，燃气表等级")
	private int grade;

	@ApiModelProperty(value = "用电类型key")
	private Integer electricTypekey;

}
