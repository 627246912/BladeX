package org.springblade.energy.securitymanagement.service;

import org.springblade.core.mp.base.BaseService;
import org.springblade.core.tool.api.R;
import org.springblade.energy.securitymanagement.entity.CompanyInfo;

import java.util.List;


public interface CompanyInfoService extends BaseService<CompanyInfo> {

	R<CompanyInfo> batchDeleted(List<CompanyInfo> params);
}
