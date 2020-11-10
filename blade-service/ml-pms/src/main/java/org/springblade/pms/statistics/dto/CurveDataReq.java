package org.springblade.pms.statistics.dto;

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
	@ApiModelProperty(value = "网关id")
	private String gwId;

	@ApiModelProperty(value = "用户ID")
	private String userGroup;

	@ApiModelProperty(value = "rtuidcb")
	private String rtuidcb;

	@ApiModelProperty(value = "sid")
	private String sid;

	@ApiModelProperty(value = "曲线数据类型,2电流，7电能")
	private Integer dataCurveType;

	@ApiModelProperty(value = "统计日期类型 0:天 1:星期 2:月 3:年 4:近24小时 5:近7天 6:近30天 7:近12月 8:季度 9:5分钟")
	private Integer dateType;
	@ApiModelProperty(value = "时间")
	private Date time;

	@ApiModelProperty(value = "结束时间")
	private Date etime;

	@ApiModelProperty(value = "数据日期类型用英文逗号分隔 0:当前查询日期数据 1:同比数据 2:环比数据")
	private String dataMolds;

}
