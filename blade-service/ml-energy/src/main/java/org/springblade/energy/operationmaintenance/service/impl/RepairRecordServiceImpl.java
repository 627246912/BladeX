package org.springblade.energy.operationmaintenance.service.impl;

import lombok.AllArgsConstructor;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.energy.operationmaintenance.entity.RepairRecord;
import org.springblade.energy.operationmaintenance.mapper.RepairRecordMapper;
import org.springblade.energy.operationmaintenance.service.RepairRecordService;
import org.springframework.stereotype.Service;

/**
 * 服务实现类
 *
 * @author bini
 * @since 2020-08-05
 */
@Service
@AllArgsConstructor
public class RepairRecordServiceImpl extends BaseServiceImpl<RepairRecordMapper, RepairRecord> implements RepairRecordService {
}
