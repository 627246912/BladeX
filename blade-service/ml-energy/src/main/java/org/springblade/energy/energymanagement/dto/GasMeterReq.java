package org.springblade.energy.energymanagement.dto;

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
public class GasMeterReq {


	@ApiModelProperty(value = "站点id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long stationId;

	@ApiModelProperty(value = "位置id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long siteId;



	@ApiModelProperty(value = "供气公司ID")
	@JsonSerialize(
		using = ToStringSerializer.class
	)
	private Long gasCompanyId;

	@ApiModelProperty(value = "淡季月份")
	private String lowSeason;
	@ApiModelProperty(value = "淡季价格")
	private Double lowSeasonPrice;

	@ApiModelProperty(value = "旺季月份")
	private String busySeason;

	@ApiModelProperty(value = "旺季价格")
	private Double busySeasonPrice;

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
