package org.springblade.pms.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class PmsGwInfo implements Serializable {
	private static final long serialVersionUID = 6797089314338525478L;
	private String gwid;					//网关
	private String MN;
	private String ver;
	@ApiModelProperty(value = "站点名称")
	private String stationName;
	/**
	 * 设备名称
	 */
	@ApiModelProperty(value = "设备名称")
	private String deviceName;
	/**
	 * 省份code
	 */
	@ApiModelProperty(value = "省份code")
	private String provinceCode;
	/**
	 * 省份
	 */
	@ApiModelProperty(value = "省份")
	private String provinceName;
	/**
	 * 城市code
	 */
	@ApiModelProperty(value = "城市code")
	private String cityCode;
	/**
	 * 城市
	 */
	@ApiModelProperty(value = "城市")
	private String cityName;
	/**
	 * 区县code
	 */
	@ApiModelProperty(value = "区县code")
	private String countyCode;
	/**
	 * 区县
	 */
	@ApiModelProperty(value = "区县")
	private String countyName;
	/**
	 * 详细地址
	 */
	@ApiModelProperty(value = "详细地址")
	private String address;
	/**
	 * 经纬度
	 */
	@ApiModelProperty(value = "经纬度")
	private String location;



	private List<PmsGwInfoRtu> rtulist;

}
