package org.springblade.energy.operationmaintenance.service;

import org.springblade.core.mp.base.BaseService;
import org.springblade.core.tool.api.R;
import org.springblade.energy.operationmaintenance.entity.InspectionTask;
import org.springblade.energy.operationmaintenance.vo.InspectionCountVo;
import org.springblade.energy.operationmaintenance.vo.InspectionRouteStatusVo;
import org.springblade.energy.securitymanagement.util.PageQuery;
import org.springblade.energy.securitymanagement.util.PageUtils;

public interface InspectionTaskService extends BaseService<InspectionTask> {


	R<PageUtils> customizePage(PageQuery pageQuery, InspectionTask inspectionRecord);

	InspectionRouteStatusVo inspectionRouteStatus(Long id, String yearMonthDay);

	R<InspectionCountVo> inspectionCount(String stationId, String siteId, String time, Integer type, Integer taskType);

}
