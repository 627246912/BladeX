package org.springblade.energy.securitymanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import lombok.AllArgsConstructor;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.tool.api.R;
import org.springblade.energy.securitymanagement.entity.CompanyInfo;
import org.springblade.energy.securitymanagement.entity.ConstructionNotifyCard;
import org.springblade.energy.securitymanagement.entity.HiddenTroubleNotifyCard;
import org.springblade.energy.securitymanagement.enums.Status;
import org.springblade.energy.securitymanagement.mapper.CompanyInfoMapper;
import org.springblade.energy.securitymanagement.mapper.ConstructionNotifyCardMapper;
import org.springblade.energy.securitymanagement.mapper.HiddenTroubleNotifyCardMapper;
import org.springblade.energy.securitymanagement.service.CompanyInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class CompanyInfoServiceImpl extends BaseServiceImpl<CompanyInfoMapper, CompanyInfo> implements CompanyInfoService {

	ConstructionNotifyCardMapper constructionNotifyCardMapper;

	HiddenTroubleNotifyCardMapper hiddenTroubleNotifyCardMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R<CompanyInfo> batchDeleted(List<CompanyInfo> params) {
		params.forEach((c) -> {
			CompanyInfo companyInfo = baseMapper.selectOne(new QueryWrapper<CompanyInfo>().eq("id", c.getId()));
			String name = companyInfo.getCompanyName();
			List<ConstructionNotifyCard> constructionNotifyCardList = constructionNotifyCardMapper.selectList(new QueryWrapper<ConstructionNotifyCard>().eq("construction_org", name));
			List<HiddenTroubleNotifyCard> hiddenTroubleNotifyCardList = hiddenTroubleNotifyCardMapper.selectList(new QueryWrapper<HiddenTroubleNotifyCard>().eq("item_name", name));
			if (constructionNotifyCardList.size() > 0 || hiddenTroubleNotifyCardList.size() > 0) {
				throw new ServiceException("请先删除该公司相关安全告知卡或隐患告知卡");
			}
			baseMapper.delete(new QueryWrapper<CompanyInfo>().eq("id", c.getId()));
		});
		return R.success(Status.DELETE_SUCCESS.getVal());
	}
}
