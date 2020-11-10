package org.springblade.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class Device implements Serializable {
	private static final long serialVersionUID = 6797089314338525478L;
	private String code;					//设备编号
	private String name;					//名称
	private String desc;					//网关描述
	private String appid;					//网关所属公司
	private String stationid;					// 中车站点ID

}
