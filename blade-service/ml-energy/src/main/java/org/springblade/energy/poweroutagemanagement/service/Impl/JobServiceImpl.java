package org.springblade.energy.poweroutagemanagement.service.Impl;

import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.energy.poweroutagemanagement.entity.Job;
import org.springblade.energy.poweroutagemanagement.mapper.JobMapper;
import org.springblade.energy.poweroutagemanagement.service.JobService;
import org.springframework.stereotype.Service;

@Service
public class JobServiceImpl extends BaseServiceImpl<JobMapper, Job> implements JobService {
}
