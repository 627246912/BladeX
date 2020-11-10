package org.springblade.energy.operationmaintenance.service.impl;

import lombok.AllArgsConstructor;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.energy.operationmaintenance.entity.MaintainRecord;
import org.springblade.energy.operationmaintenance.mapper.MaintainRecordMapper;
import org.springblade.energy.operationmaintenance.service.MaintainRecordService;
import org.springframework.stereotype.Service;

/**
 * 服务实现类
 *
 * @author bini
 * @since 2020-08-05
 */
@Service
@AllArgsConstructor
public class MaintainRecordServiceImpl extends BaseServiceImpl<MaintainRecordMapper, MaintainRecord> implements MaintainRecordService {

}
