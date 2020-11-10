package org.springblade.energy.statistics.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author bond
 * @date 2020/5/16 13:32
 * @desc
 */
@Data
public class CurveDataReq {
	@ApiModelProperty(value = "站点")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long stationId;

	@ApiModelProperty(value = "位置")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long siteId;

	@ApiModelProperty(value = "部门")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long deptId;

	@ApiModelProperty(value = "系统图产品id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long diagramProductId;

	@ApiModelProperty(value = "系统图类型")
	private String diagramType;

	@ApiModelProperty(value = "曲线类型")
	private Integer dataCurveType;

	@ApiModelProperty(value = "日期类型 0:天 1:星期 2:月 3:年 4:近24小时 5:近7天 6:近30天 7:近12月 8:季度 9:5分钟")
	private Integer dateType;
	@ApiModelProperty(value = "时间")
	private Date time;

	@ApiModelProperty(value = "结束时间")
	private Date etime;

	@ApiModelProperty(value = "数据类型用英文逗号分隔 0:当前查询日期数据 1:同比数据 2:环比数据")
	private String dataMolds;

}
