package org.springblade.energy.poweroutagemanagement.service.Impl;

import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.energy.poweroutagemanagement.entity.PowerOutage;
import org.springblade.energy.poweroutagemanagement.mapper.PowerOutageMapper;
import org.springblade.energy.poweroutagemanagement.service.PowerOutageService;
import org.springframework.stereotype.Service;

@Service
public class PowerOutageServiceImpl extends BaseServiceImpl<PowerOutageMapper, PowerOutage> implements PowerOutageService {
}
