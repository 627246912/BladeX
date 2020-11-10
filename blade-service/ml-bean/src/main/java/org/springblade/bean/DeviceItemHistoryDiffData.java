package org.springblade.bean;

import lombok.Data;

/**
 * @Auther: bond
 * @Date: 2020、03、31
 * @Description:
 */
@Data
public class DeviceItemHistoryDiffData extends DeviceItemHistoryData{
    private static final long serialVersionUID = 7245662875695290932L;
    /**
     * 初值
     */
    private Float val0;
    /**
     * 终值
     */
    private Float valx;

	/**
	 * 该时间段的平均值
	 */
	private Float avg;

	private Float min; //最小值
	private	Float max;//最大值
	private	Float price;//水电气价钱


}
