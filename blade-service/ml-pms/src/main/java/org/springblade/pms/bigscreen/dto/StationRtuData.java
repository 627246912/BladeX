package org.springblade.pms.bigscreen.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bond
 * @date 2020/9/14 18:46
 * @desc
 */
@Data
public class StationRtuData {
	/**
	 * 网关id
	 */
	@ApiModelProperty(value = "网关id")
	private String gwId;
	/**
	 * com口
	 */
	@ApiModelProperty(value = "rtuid")
	private String rtuid;

	@ApiModelProperty(value = "端口")
	private Integer port;
	/**
	 * rtuid
	 */
	@ApiModelProperty(value = "端口组合")
	private String rtuidcb;
	/**
	 * 分配用户
	 */
	@ApiModelProperty(value = "分配用户")
	private String userGroup;
	/**
	 * 额定电流
	 */
	@ApiModelProperty(value = "额定电流")
	private Float ratedI;
	/**
	 * 电流预警百分比
	 */
	@ApiModelProperty(value = "电流预警百分比")
	private Float warnI;
	/**
	 * 电流告警百分比
	 */
	@ApiModelProperty(value = "电流告警百分比")
	private Float alarmI;
	/**
	 * com名称
	 */
	@ApiModelProperty(value = "端口名称")
	private String rtuname;



	@ApiModelProperty(value = "实时数据项列表")
	private List<ItemResq> itemResqList = new ArrayList<>();

	@ApiModelProperty(value = "网关在线状态0在线,1离线")
	private Integer gwStatus;

	@ApiModelProperty(value = "开关状态0断开,1闭合")
	private Integer switchStatus;


	@ApiModelProperty(value = "状态1启用，0未启用")
	private Integer status;

	@ApiModelProperty(value = "启用时间")
	private String activateTime;

	@ApiModelProperty(value = "定时控制0禁止,1启用")
	private Integer timeContorStatus;


}
