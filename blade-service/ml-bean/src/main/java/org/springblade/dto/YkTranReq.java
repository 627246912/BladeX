package org.springblade.dto;

import lombok.Data;
import org.springblade.bean.YkTran;

import java.util.List;

/**
 * @author bond
 * @date 2020/5/9 18:27
 * @desc
 */
@Data
public class YkTranReq {
	private List<YkTran> ykTranInfos;
	private String rtuIds;
}
