package org.springblade.energy.operationmaintenance.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RepairHistoryFaultVo {

	//设备名称
	private String devName;

	//报修时间
	private String repairTime;

	//报修原因
	private String repairReason;
}
