package org.springblade.energy.statistics.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author bond
 * @date 2020/6/29 10:48
 * @desc
 */
@Data
public class EnergyTarCurveReq{
	private static final long serialVersionUID = 1L;

	/**
	 * 站点ID
	 */
	@ApiModelProperty(value = "站点ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long stationId;
	/**
	 * 位置ID
	 */
	@ApiModelProperty(value = "位置ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long siteId;

	@ApiModelProperty(value = "部门")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long deptId;

	@ApiModelProperty(value = "能源类型 1供电，2供水，3供气")
	private Integer energyType;
//
//	@ApiModelProperty(value = "指标")
//	private Double tar;

	@ApiModelProperty(value = "日期类型 0:天 1:星期 2:月 3:年 4:近24小时 5:近7天 6:近30天 7:近12月 8:季度")
	private Integer dateType;

	@ApiModelProperty(value = "时间")
	private Date time;

	@ApiModelProperty(value = "数据类型用英文逗号分隔 0:当前查询日期数据 1:同比数据 2:环比数据")
	private String dataMolds;
}
