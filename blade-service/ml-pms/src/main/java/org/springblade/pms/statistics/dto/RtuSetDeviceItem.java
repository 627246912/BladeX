package org.springblade.pms.statistics.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springblade.bean.DeviceItem;

import java.util.List;

/**
 * @author bond
 * @date 2020/9/28 18:07
 * @desc
 */
@Data
public class RtuSetDeviceItem {

	/**
	 * 网关id
	 */
	@ApiModelProperty(value = "网关id")
	private String gwId;

	/**
	 * com口
	 */
	@ApiModelProperty(value = "端口")
	private Integer port;
	/**
	 * com口
	 */
	@ApiModelProperty(value = "rtuid")
	private String rtuid;
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

	@ApiModelProperty(value = "数据项")
	private DeviceItem deviceItem;


	@ApiModelProperty(value = "时间列表")
	private List<Object> XVals;
	@ApiModelProperty(value = "数据列表")
	private List<Object> YSubVals;

	@ApiModelProperty(value = "数据和")
	private Float sumVal;

}
