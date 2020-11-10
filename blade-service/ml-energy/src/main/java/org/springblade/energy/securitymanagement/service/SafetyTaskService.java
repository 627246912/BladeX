package org.springblade.energy.securitymanagement.service;

import org.springblade.core.mp.base.BaseService;
import org.springblade.core.tool.api.R;
import org.springblade.energy.securitymanagement.entity.SafetyTask;
import org.springblade.energy.securitymanagement.util.PageQuery;
import org.springblade.energy.securitymanagement.util.PageUtils;

public interface SafetyTaskService extends BaseService<SafetyTask> {

	R updateSafetyInspectionRecord(SafetyTask safetyInspectionRecord);

	R<PageUtils> listSafetyInspectionRecord(PageQuery pageQuery, SafetyTask safetyInspectionRecord);
}
