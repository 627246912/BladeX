package org.springblade.energy.runningmanagement.station.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author bond
 * @date 2020/3/20 15:58
 * @desc
 */
@Data
public class SiteTree {



	@ApiModelProperty(value = "stationId")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long stationId;
	@ApiModelProperty(value = "站点名称")
	private String sname;

	@ApiModelProperty(value = "id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	/**
	 * 位置名称
	 */
	@ApiModelProperty(value = "位置名称")
	private String siteName;
	/**
	 * 父位置id
	 */
	@ApiModelProperty(value = "父位置id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long parentId;
	@ApiModelProperty(value = "父位名称")
	private String parentNme;

	@ApiModelProperty(value = "部门id")
	private String deptId;

	@ApiModelProperty(value = "部门名称")
	private String deptName;

	@ApiModelProperty(value = "备注")
	private String remark;

	private List<SiteTree> children;
}
