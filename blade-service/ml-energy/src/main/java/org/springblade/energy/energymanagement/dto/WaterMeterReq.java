package org.springblade.energy.energymanagement.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author bond
 * @date 2020/6/16 16:30
 * @desc 供水计量管账
 */
@Data
public class WaterMeterReq {


	@ApiModelProperty(value = "站点id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long stationId;

	@ApiModelProperty(value = "位置id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long siteId;

	@ApiModelProperty(value = "水表，燃气表等级")
	private int grade;

	@ApiModelProperty(value = "价格")
	@TableField("price")
	private Double price;
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@ApiModelProperty(value = "计量时间")
	private Date meterTime;

	@ApiModelProperty(value = "计量方式")
	private Integer meterType;
}
