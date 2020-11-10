package org.springblade.energy.operationmaintenance.service.impl;

import lombok.AllArgsConstructor;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.energy.operationmaintenance.entity.Change;
import org.springblade.energy.operationmaintenance.mapper.ChangeMapper;
import org.springblade.energy.operationmaintenance.service.ChangeService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ChangeServiceImpl extends BaseServiceImpl<ChangeMapper, Change> implements ChangeService {
}
