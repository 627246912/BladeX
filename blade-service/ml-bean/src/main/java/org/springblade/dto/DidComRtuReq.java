package org.springblade.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springblade.bean.DeviceSub;

import java.util.List;
import java.util.Map;

/**
 * @author bond
 * @date 2020/5/30 15:43
 * @desc
 */
@Data
public class DidComRtuReq {
	@ApiModelProperty(value = "站点id")
	private String gwid;           //网关编号，即gwid,全局唯一标识一个网关
	@ApiModelProperty(value = "com口及对应rtu设备信息")
	private Map<Integer,List<DeviceSub>> comRtus;
}
