package org.springblade.dto;

import lombok.Data;

/**
 * @author bond
 * @date 2020/7/21 19:04
 * @desc
 */
@Data
public class WaterOrGasPrice {
	private String itemid;//数据项
	private int type;//类型 0：水  1：气
	private String lowSeason;//淡季  专用于气  月份格式: 01,02,03,04,05,06,07,08,09,10,11,12
	private float lowSeasonPrice;//淡季价格   专用于气
	private String busySeason;//旺季   专用于气
	private float busySeasonPrice;//旺季价格  专用于气
	private float price;//专用于水

}
