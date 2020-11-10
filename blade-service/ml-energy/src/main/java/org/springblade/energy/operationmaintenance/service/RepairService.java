package org.springblade.energy.operationmaintenance.service;

import org.springblade.core.mp.base.BaseService;
import org.springblade.core.tool.api.R;
import org.springblade.energy.operationmaintenance.entity.Repair;
import org.springblade.energy.operationmaintenance.vo.RepairCountVo;
import org.springblade.energy.securitymanagement.util.PageQuery;
import org.springblade.energy.securitymanagement.util.PageUtils;


/**
 * 维修服务接口
 *
 * @author bini
 * @since 2020-07-23
 */

public interface RepairService extends BaseService<Repair> {

	R<RepairCountVo> repairCount(String stationId, String siteId, String time, Integer type);

	R<PageUtils> customizePage(PageQuery pageQuery, Repair repair);

	R<PageUtils> transferOrderPage(PageQuery pageQuery, String dateTime, Long adminId);
}
