package org.springblade.dto;

import lombok.Data;

/**
 * @author bond
 * @date 2020/7/21 19:11
 * @desc
 */
@Data
public class JfpgPriceNew {
	private String  itemid;//数据项
	private String  top;//尖
	private float  topPrice;//尖价格
	private String  peak;//峰
	private float  peakPrice;//峰价格
	private String  flat;//平
	private float  flatPrice;//平价格
	private String  valley;//谷
	private float  valleyPrice;//谷价格
}
