package org.springblade.pms.alarmmanagement.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bond
 * @date 2020/5/7 16:28
 * @desc
 */
@Data
public class EquipmentAlarmRsep {

	@JsonSerialize(
		using = ToStringSerializer.class
	)
	@ApiModelProperty("主键id")
	private String id;


	@ApiModelProperty(value = "网关")
	private String gwId;

	@ApiModelProperty(value = "告警id")
	@TableField("alarmId")
	private Integer alarmId;
	/**
	 * 告警数据项id
	 */
	@ApiModelProperty(value = "告警数据项id")
	@TableField("alarmItemId")
	private String alarmItemId;
	/**
	 * 设备id
	 */
	@ApiModelProperty(value = "设备id")
	private String equipmentId;
	/**
	 * 设备名称
	 */
	@ApiModelProperty(value = "设备名称")
	private String equipmentName;
	/**
	 * 设备类型id
	 */
	@ApiModelProperty(value = "设备类型id")
	private Long equipmentTypeId;

	@ApiModelProperty(value = "设备类型名称")
	private String equipmentTypeName;

	/**
	 * 站点id
	 */
	@ApiModelProperty(value = "站点id")
	@JsonSerialize(
		using = ToStringSerializer.class
	)
	private String stationId;

	@ApiModelProperty(value = "站点名称")
	private String stationName;
	/**
	 * 地址
	 */
	@ApiModelProperty(value = "地址")
	private String address;
	/**
	 * 告警内容
	 */
	@ApiModelProperty(value = "告警内容")
	private String alarmContent;
	/**
	 * 告警等级
	 */
	@ApiModelProperty(value = "告警等级")
	private Integer level;
	@ApiModelProperty(value = "告警等级名称")
	private String levelName;
	/**
	 * 告警状态
	 */
	@ApiModelProperty(value = "告警状态")
	private Integer alarmStatus;
	/**
	 * 告警时间
	 */
	@ApiModelProperty(value = "告警时间")
	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	private String alarmTime;
	/**
	 * 告警结束时间
	 */
	@ApiModelProperty(value = "告警结束时间")
	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	private String alarmEndTime;
	/**
	 * 处理状态
	 */
	@ApiModelProperty(value = "处理状态")
	private Integer handleStatus;

	@ApiModelProperty(value = "告警类型")
	private Integer alarmType;
	@ApiModelProperty(value = "告警类型名称")
	private String alarmTypeName;

	/**
	 * 经度
	 */
	@ApiModelProperty(value = "经度")
	private Double lng;
	/**
	 * 纬度
	 */
	@ApiModelProperty(value = "纬度")
	private Double lat;



	@ApiModelProperty(value = "设备状态")
	private Integer equipmentStatus;
	@ApiModelProperty(value = "设备状态")
	private String equipmentStatusName;

	@ApiModelProperty(value = "影响用户名称")
	private String userGroupName;
	@ApiModelProperty(value = "影响用户名称")
	private String userGroup;

}
