/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package org.springblade.energy.runningmanagement.station.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.common.cache.CacheNames;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.runningmanagement.station.entity.Site;
import org.springblade.energy.runningmanagement.station.service.ISiteService;
import org.springblade.energy.runningmanagement.station.vo.SiteTree;
import org.springblade.energy.runningmanagement.station.vo.SiteVO;
import org.springblade.energy.runningmanagement.station.wrapper.SiteWrapper;
import org.springblade.system.entity.Dept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

/**
 * 位置信息表 控制器
 *
 * @author bond
 * @since 2020-03-16
 */
@RestController
@AllArgsConstructor
@RequestMapping("/site")
@Api(value = "位置信息表", tags = "位置信息表接口")
public class SiteController extends BladeController {

	private ISiteService siteService;
	@Autowired
	private BladeRedisCache redisCache;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入site")
	public R<SiteVO> detail(Site site) {
		Site detail = siteService.getOne(Condition.getQueryWrapper(site));
		return R.data(SiteWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 位置信息表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入site")
	public R<IPage<SiteVO>> list(Site site, Query query) {
		IPage<Site> pages = siteService.page(Condition.getPage(query), Condition.getQueryWrapper(site));
		return R.data(SiteWrapper.build().pageVO(pages));
	}
	/**
	 * 根据站点、系统图类型查询位置
	 */
	@GetMapping("/getlistByDiagramType")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "根据站点、系统图类型查询位置", notes = "")
	public R<List<Site>> getlistByDiagramType(@ApiParam(value = "站点ID", required = true) Long stationId,
											  @ApiParam(value = "系统图类型", required = false)String diagramType) {
		List<String> diagramTypes=new ArrayList<>();
			if(Func.isNotEmpty(diagramType)){
				diagramTypes=Arrays.asList(diagramType.split(","));
			}else{diagramTypes=null;}
		List<Site> list= siteService.getlistByDiagramType(stationId,diagramTypes);
		return R.data(list);
	}

	/**
	 * 查询站点位置树
	 */
	@GetMapping("/tree")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "查询站点位置树", notes = "传入parentId")
	public R<List<SiteTree>> getSiteTree(Long stationId) {
		Map<String,Object> map =new HashMap<String,Object>(1);
		map.put("stationId",stationId);
		List<SiteTree> list = siteService.getSiteTree(map);
		return R.data(list);
	}

	/**
	 * 自定义分页 位置信息表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入site")
	public R<IPage<SiteVO>> page(SiteVO site, Query query) {
		IPage<SiteVO> pages = siteService.selectSitePage(Condition.getPage(query), site);

		return R.data(pages);
	}

	/**
	 * 新增 位置信息表
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入site")
	public R save(@Valid @RequestBody Site site) {
		return R.status(siteService.save(site));
	}

	/**
	 * 修改 位置信息表
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入site")
	public R update(@Valid @RequestBody Site site) {
		Site detail =siteService.getById(site.getId());
		boolean re = siteService.updateById(site);
		if (!re) {
			return R.status(re);
		}
		//判断是否修改所属站点
		if(!detail.getStationId().equals(site.getStationId())){



			List<SiteTree>  list=siteService.querySiteByPid(site.getId());
			for(SiteTree siteTree:list){
				detail =siteService.getById(site.getId());
				detail.setStationId(site.getStationId());
				re = siteService.updateById(site);
			}
		}
		return R.status(true);
	}

	@GetMapping("/querySiteList")
	public R querySiteList(long pid) {
		List<SiteTree>  list=siteService.querySiteByPid(pid);
		return R.data(list)	;
	}
	/**
	 * 新增或修改 位置信息表
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入site")
	public R submit(@Valid @RequestBody Site site) {
		return R.status(siteService.saveOrUpdate(site));
	}


	/**
	 * 删除 位置信息表
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(siteService.deleteLogic(Func.toLongList(ids)));
	}



	/**
	 * 查询位置部门
	 */
	@GetMapping("/toDeptlist")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "位置下的所以部门", notes = "传入siteId")
	public R<List<Dept>> toDeptlist(@ApiParam(value = "位置id", required = true) @RequestParam Long siteId) {

		Map<String,Object> map =new HashMap<String,Object>(1);
		map.put("id",siteId);
		List<Site> listSite = siteService.queryChildSite(map);

		List<String> deptIds=new ArrayList<>();
		for(Site site :listSite) {
			if(Func.isNotEmpty(site.getDeptId())){
			List<String> deptId = Arrays.asList(site.getDeptId().split(","));
				deptIds.addAll(deptId);
			}
		}
		List<Dept> list = new ArrayList<>();

		for (String id : deptIds) {
				Dept dept = redisCache.hGet(CacheNames.DEPT_KEY, id);
				list.add(dept);
		}

		return R.data(list);
	}

}
