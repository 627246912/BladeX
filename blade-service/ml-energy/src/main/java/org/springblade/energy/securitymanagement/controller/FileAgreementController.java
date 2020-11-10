package org.springblade.energy.securitymanagement.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springblade.energy.securitymanagement.entity.FileAgreement;
import org.springblade.energy.securitymanagement.enums.Status;
import org.springblade.energy.securitymanagement.service.FileAgreementService;
import org.springblade.energy.securitymanagement.util.PageQuery;
import org.springblade.energy.securitymanagement.util.PageUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/security/agreement")
@Api(value = "安全管理--安全协议", tags = "安全管理-安全协议接口")
@AllArgsConstructor
public class FileAgreementController {


	FileAgreementService fileAgreementService;

	@PostMapping("/save")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "上传资料")
	public R<FileAgreement> uploadInfo(@RequestBody @Valid FileAgreement fileAgreement) {

		String filename = fileAgreement.getName();
		assert filename != null;
		if (filename.trim().equals("")) {
			return R.fail("文件名称不能为空");
		}
		fileAgreement.setName(fileAgreement.getName().trim());
		if (fileAgreementService.count(new QueryWrapper<FileAgreement>().eq("name", filename)) > 0) {
			return R.fail("文件名称不能重复");
		}
		boolean save = fileAgreementService.save(fileAgreement);
		if (!save) {
			return R.fail(Status.ADD_ERROR.getVal());
		}
		return R.success(Status.ADD_SUCCESS.getVal());
	}


	@GetMapping("/page")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "列表")
	public R<PageUtils> pageList(PageQuery pageQuery, FileAgreement fileAgreement) {
		Page<FileAgreement> page = fileAgreementService.page(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), new QueryWrapper<>(fileAgreement).orderByDesc("create_time"));
		return R.data(new PageUtils(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
	}


	@PutMapping("/update")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "编辑")
	public R<FileAgreement> compile(@RequestBody FileAgreement fileAgreement) {
		if (!
			fileAgreementService.update(new FileAgreement(fileAgreement.getName(),
				fileAgreement.getFile()), new QueryWrapper<FileAgreement>()
				.eq("id", fileAgreement.getId())
				.eq("is_deleted", 0)))
			return R.fail(Status.UPDATE_ERROR.getVal());
		return R.success(Status.UPDATE_SUCCESS.getVal());
	}


	@PostMapping("/remove")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "批量删除")
	public R<FileAgreement> batchDeleted(@RequestBody List<FileAgreement> params) {
		if (params.size() <= 0)
			return R.fail(Status.DELETE_ERROR.getVal());
		params.forEach((f) -> fileAgreementService.remove(new QueryWrapper<FileAgreement>().eq("id", f.getId())));
		return R.success(Status.DELETE_SUCCESS.getVal());
	}
}
