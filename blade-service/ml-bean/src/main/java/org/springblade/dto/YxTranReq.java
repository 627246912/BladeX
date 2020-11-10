package org.springblade.dto;

import lombok.Data;
import org.springblade.bean.YxTran;

import java.util.List;

/**
 * @author bond
 * @date 2020/5/9 18:27
 * @desc
 */
@Data
public class YxTranReq {
	private List<YxTran> yxTranInfos;
	private String rtuIds;
}
