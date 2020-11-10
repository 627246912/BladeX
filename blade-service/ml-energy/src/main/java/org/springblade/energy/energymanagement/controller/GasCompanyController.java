package org.springblade.energy.energymanagement.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.energy.energymanagement.entity.GasCompany;
import org.springblade.energy.energymanagement.service.IGasCompanyService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author bond
 * @date 2020/6/10 13:53
 * @desc
 */
@RestController
@AllArgsConstructor
@RequestMapping("/meter")
@Api(value = "能源管理-计量管账", tags = "能源管理-供气公司")
public class GasCompanyController {

	private IGasCompanyService iGasCompanyServiceImpl;
	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入gasCompany")
	public R<GasCompany> detail(GasCompany gasCompany) {
		GasCompany detail = iGasCompanyServiceImpl.getOne(Condition.getQueryWrapper(gasCompany));
		return R.data(detail);
	}

	/**
	 * 自定义分页 位置信息表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入gasCompany")
	public R<IPage<GasCompany>> page(GasCompany gasCompany, Query query) {
		IPage<GasCompany> pages = iGasCompanyServiceImpl.selectGasCompanyPage(Condition.getPage(query), gasCompany);
		return R.data(pages);
	}

	/**
	 * 新增或修改 位置信息表
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入gasCompany")
	public R submit(@Valid @RequestBody GasCompany site) {
		return R.status(iGasCompanyServiceImpl.saveOrUpdate(site));
	}

}
