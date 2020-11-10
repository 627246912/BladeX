package org.springblade.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Auther: wangqiaoqing
 * @Date: 2019/4/16
 * @Description:
 */
@Data
@ApiModel(description = "日期请求参数")
public class DateReq implements Serializable {
    private static final long serialVersionUID = 906036165826492977L;
    @ApiModelProperty(value = "日期类型 0:天 1:星期 2:月 3:年 4:近24小时 5:近7天 6:近30天 7:近12月")
    private Integer dateType;
    @ApiModelProperty(value = "时间")
    private Date time;
	@ApiModelProperty(value = "结束时间")
	private Date etime;
	@ApiModelProperty(value = "是否统计某一天的数据")
	private Boolean dayStat;

}
