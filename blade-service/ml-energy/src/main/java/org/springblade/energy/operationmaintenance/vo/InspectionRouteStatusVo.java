package org.springblade.energy.operationmaintenance.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InspectionRouteStatusVo {

	private String status;

	private List<InspectionRouteVo> inspectionRouteVos;
}
