package org.springblade.energy.operationmaintenance.service.impl;

import lombok.AllArgsConstructor;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.energy.operationmaintenance.entity.CheckItemRecord;
import org.springblade.energy.operationmaintenance.mapper.CheckItemRecordMapper;
import org.springblade.energy.operationmaintenance.service.CheckItemRecordService;
import org.springframework.stereotype.Service;

/**
 * 服务实现类
 *
 * @author bini
 * @since 2020-07-08
 */
@Service
@AllArgsConstructor
public class CheckItemRecordServiceImpl extends BaseServiceImpl<CheckItemRecordMapper, CheckItemRecord> implements CheckItemRecordService {
}
