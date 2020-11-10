package org.springblade.dto;

import lombok.Data;
import org.springblade.bean.YcAlarmConfig;

import java.util.List;

/**
 * @author bond
 * @date 2020/5/9 18:27
 * @desc
 */
@Data
public class YcAlarmConfigReq {
	private List<YcAlarmConfig> ycAlarmConfig;
	private List<String> gwIds;
	private List<String> rtuIds;
}
