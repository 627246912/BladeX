package org.springblade.dto;

import lombok.Data;
import org.springblade.bean.YxAlarmConfig;

import java.util.List;

/**
 * @author bond
 * @date 2020/5/9 18:27
 * @desc
 */
@Data
public class YxAlarmConfigReq {
	private List<YxAlarmConfig> yxAlarmConfig;
	private List<String> gwIds;
	private List<String> rtuIds;
}
