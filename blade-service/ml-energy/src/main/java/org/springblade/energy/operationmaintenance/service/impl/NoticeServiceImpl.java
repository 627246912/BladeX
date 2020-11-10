package org.springblade.energy.operationmaintenance.service.impl;

import lombok.AllArgsConstructor;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.energy.operationmaintenance.entity.Notice;
import org.springblade.energy.operationmaintenance.mapper.NoticeMapper;
import org.springblade.energy.operationmaintenance.service.NoticeService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NoticeServiceImpl extends BaseServiceImpl<NoticeMapper, Notice> implements NoticeService {
}
