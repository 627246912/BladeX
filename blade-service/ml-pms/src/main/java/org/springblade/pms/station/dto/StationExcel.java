package org.springblade.pms.station.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author bond
 * @date 2020/9/29 11:03
 * @desc
 */
@Data
public class StationExcel {
	@ExcelProperty(value = "网关id")
	private String gwId;
	/**
	 * 站点名称
	 */
	@ExcelProperty(value = "站点编号")
	private String stationNo;
	@ExcelProperty(value = "站点名称")
	private String stationName;
	/**
	 * 设备名称
	 */
	@ExcelProperty(value = "设备名称")
	private String deviceName;
	/**
	 * 省份code
	 */
	@ExcelProperty(value = "省份code")
	private String provinceCode;
	/**
	 * 省份
	 */
	@ExcelProperty(value = "省份")
	private String provinceName;
	/**
	 * 城市code
	 */
	@ExcelProperty(value = "城市code")
	private String cityCode;
	/**
	 * 城市
	 */
	@ExcelProperty(value = "城市")
	private String cityName;
	/**
	 * 区县code
	 */
	@ExcelProperty(value = "区县code")
	private String countyCode;
	/**
	 * 区县
	 */
	@ExcelProperty(value = "区县")
	private String countyName;
	/**
	 * 详细地址
	 */
	@ExcelProperty(value = "详细地址")
	private String address;
	/**
	 * 经度
	 */
	@ExcelProperty(value = "经度")
	private Double lng;
	/**
	 * 纬度
	 */
	@ExcelProperty(value = "纬度")
	private Double lat;

	@ExcelProperty(value = "产权属性")
	private String property;
	@ExcelProperty(value = "责任人")
	private String dutyer;
	@ExcelProperty(value = "电话")
	private String phone;

}
