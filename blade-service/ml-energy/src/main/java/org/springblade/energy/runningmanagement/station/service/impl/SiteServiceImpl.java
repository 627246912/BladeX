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
package org.springblade.energy.runningmanagement.station.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.common.cache.CacheNames;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.runningmanagement.station.entity.Site;
import org.springblade.energy.runningmanagement.station.mapper.SiteMapper;
import org.springblade.energy.runningmanagement.station.service.ISiteService;
import org.springblade.energy.runningmanagement.station.vo.SiteTree;
import org.springblade.energy.runningmanagement.station.vo.SiteVO;
import org.springblade.system.entity.Dept;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 位置信息表 服务实现类
 *
 * @author bond
 * @since 2020-03-16
 */
@Service
public class SiteServiceImpl extends BaseServiceImpl<SiteMapper, Site> implements ISiteService {
	@Autowired
	private BladeRedisCache redisCache;

	@Override
	public IPage<SiteVO> selectSitePage(IPage<SiteVO> page, SiteVO site) {
		return page.setRecords(baseMapper.selectSitePage(page, site));
	}
	/**
	 * 查询站点位置树
	 */
	public List<SiteTree> getSiteTree(Map<String,Object> map){
		List<SiteTree> list=baseMapper.querySite(map);
		StringBuffer deptName= new StringBuffer();
		for(SiteTree siteTree:list){
			if(Func.isNotEmpty(siteTree.getDeptId())){
				List<String> deptIds= Arrays.asList(siteTree.getDeptId().split(","));
				for(String deptId:deptIds){
					Dept dept=redisCache.hGet(CacheNames.DEPT_KEY, Func.toLong(deptId));
					if(Func.isNotEmpty(dept)){
						deptName.append(",").append(dept.getDeptName());
					}
				}
				if(Func.isNotEmpty(deptName)){
					siteTree.setDeptName(Func.toStr(deptName.substring(2)));

				}
			}
		}
		List<SiteTree> treeList =findTree(0l,list);

		return treeList;
	}

	public static List<SiteTree> findTree(Long id, List<?> nodeIds) {
		List<SiteTree> trees = new ArrayList<SiteTree>();
		for (int i = 0; i < nodeIds.size(); i++) {
			SiteTree siteTree = (SiteTree) nodeIds.get(i);
			if (id.equals(siteTree.getParentId())) {
				SiteTree tree = new SiteTree();
				BeanUtils.copyProperties(siteTree, tree);
				List<SiteTree> childrens = findTree(siteTree.getId(), nodeIds);
				tree.setChildren(childrens);
				trees.add(tree);
			}
		}
		return trees;
	}
	/**
	 * 查询站点位置树
	 */
	public List<SiteTree> querySiteByPid(Long pid){
		List<SiteTree> list=baseMapper.querySiteByPid(pid);
		return list;
	}

	/**
	 * 查询站点位置树
	 */
	public List<Site> queryChildSite(Map<String,Object> map){
		List<Site> list=baseMapper.queryChildSite(map);
		return list;
	}

	/**
	 * 根据站点、系统图类型查询位置
	 */
	public List<Site> getlistByDiagramType(Long stationId,List<String> diagramTypes){
		List<Site> list=baseMapper.getlistByDiagramType(stationId,diagramTypes);
		return list;
	}
}
