package org.springblade.energy.runningmanagement.standingbook.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

import java.time.LocalDateTime;

/**
 * 台账--柜体实体类
 *
 * @author bond
 * @since 2020-04-03
 */
@Data
@TableName("t_equipment_cabinet")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "EquipmentCabinet对象", description = "台账--柜体")
public class EquipmentCabinet extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 设备编号
	*/
		@ApiModelProperty(value = "设备编号")
		private String code;
	/**
	* 设备名称
	*/
		@ApiModelProperty(value = "设备名称")
		private String name;
	/**
	* 设备型号
	*/
		@ApiModelProperty(value = "设备型号")
		private String modelNo;
	/**
	* 所属站点
	*/
		@ApiModelProperty(value = "所属站点")
		@JsonSerialize(
			using = ToStringSerializer.class
		)
		private Long stationId;
	/**
	* 设备位置
	*/
		@ApiModelProperty(value = "设备位置")
		@JsonSerialize(
			using = ToStringSerializer.class
		)
		private Long siteId;
	/**
	* 投入时间
	*/
		@ApiModelProperty(value = "投入时间")
		@JsonFormat(
			pattern = "yyyy-MM-dd HH:mm:ss"
		)
		private LocalDateTime devoteTime;
	/**
	* 厂家
	*/
		@ApiModelProperty(value = "厂家")
		private String factory;
	/**
	* 用电单位
	*/
		@ApiModelProperty(value = "用电单位")
		private String useUnit;
	/**
	* 设备图片
	*/
		@ApiModelProperty(value = "设备图片")
		private String picture;
	/**
	* 设备二维码图片
	*/
		@ApiModelProperty(value = "设备二维码图片")
		private String qrCode;

	@ApiModelProperty(value = "设备类别")
	private String deviceType;

}
