package org.springblade.energy.energymanagement.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.energy.energymanagement.entity.GasCompany;
import org.springblade.energy.energymanagement.mapper.GasCompanyMapper;
import org.springblade.energy.energymanagement.service.IGasCompanyService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author bond
 * @date 2020/7/22 14:19
 * @desc
 */
@Service
public class IGasCompanyServiceImpl extends BaseServiceImpl<GasCompanyMapper, GasCompany> implements IGasCompanyService {
	@Override
	public IPage<GasCompany> selectGasCompanyPage(IPage<GasCompany> page, GasCompany dto) {
		List<GasCompany> list= baseMapper.selectGasCompanyPage(page, dto);
		return page.setRecords(list);
	}

}
