package org.springblade.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class DeviceSub implements Serializable {
	private static final long serialVersionUID = -3763251340630230208L;
	private String id;
	private String gwid;           //网关编号，即gwid,全局唯一标识一个网关
	private String gwname;         //网关名称，"机房网关"
	private Integer comid;         //通讯端口：例如com1,com2
	private String rtuid;          //RTU设备ID，非全局唯一
	private String rtuname;        //RTU设备名称
	private Integer upload;        //上传间隔,单位为秒
	private String appid;         //网关所属公司
	private String desc;		   //RTU设备描述信息
	private String rtuidcb;        //RTU组合id

	private String stationId; //站点ID


}
