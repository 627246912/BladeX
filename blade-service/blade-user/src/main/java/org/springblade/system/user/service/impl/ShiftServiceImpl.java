package org.springblade.system.user.service.impl;

import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.system.user.entity.Shift;
import org.springblade.system.user.mapper.ShiftMapper;
import org.springblade.system.user.service.ShiftService;
import org.springframework.stereotype.Service;

@Service
public class ShiftServiceImpl extends BaseServiceImpl<ShiftMapper, Shift> implements ShiftService {
}
