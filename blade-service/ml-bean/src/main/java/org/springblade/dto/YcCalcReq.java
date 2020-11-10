package org.springblade.dto;

import lombok.Data;
import org.springblade.bean.YcCalc;

import java.util.List;

/**
 * @author bond
 * @date 2020/5/9 18:27
 * @desc
 */
@Data
public class YcCalcReq {
	private List<YcCalc> ycCalcs;
	private String deviceCodes;
}
