package org.springblade.dto;

import lombok.Data;
import org.springblade.bean.YxCalc;

import java.util.List;

/**
 * @author bond
 * @date 2020/5/9 18:27
 * @desc
 */
@Data
public class YxCalcReq {
	private List<YxCalc> yxCalcs;
	private String deviceCodes;
}
