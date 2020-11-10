package org.springblade.dto;

import lombok.Data;
import org.springblade.bean.YcTran;

import java.util.List;

/**
 * @author bond
 * @date 2020/5/9 18:27
 * @desc
 */
@Data
public class YcTranReq {
	private List<YcTran> ycTranInfos;
	private String rtuIds;
}
