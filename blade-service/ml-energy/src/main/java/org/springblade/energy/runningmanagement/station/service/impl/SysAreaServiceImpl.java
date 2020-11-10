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
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.energy.runningmanagement.station.entity.SysArea;
import org.springblade.energy.runningmanagement.station.mapper.SysAreaMapper;
import org.springblade.energy.runningmanagement.station.service.ISysAreaService;
import org.springblade.energy.runningmanagement.station.vo.AreaTree;
import org.springblade.energy.runningmanagement.station.vo.SysAreaVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  服务实现类
 *
 * @author bond
 * @since 2020-03-13
 */
@Service
public class SysAreaServiceImpl extends BaseServiceImpl<SysAreaMapper, SysArea> implements ISysAreaService {

	@Override
	public IPage<SysAreaVO> selectSysAreaPage(IPage<SysAreaVO> page, SysAreaVO sysArea) {
		return page.setRecords(baseMapper.selectSysAreaPage(page, sysArea));
	}

	@Override
	public List<AreaTree> getAreaTree(String areaCode) {

		Map<String,Object> map =new HashMap<String,Object>(1);
		List<AreaTree> list=baseMapper.getAreaTree(map);

		List<AreaTree> AreaTreeList =findTree("0",list);

		return AreaTreeList;
	}

	public static List<AreaTree> findTree(String id, List<?> nodeIds) {
		List<AreaTree> trees = new ArrayList<AreaTree>();
		for (int i = 0; i < nodeIds.size(); i++) {
			AreaTree areaTree = (AreaTree) nodeIds.get(i);
			if (id.equals(areaTree.getParentId())) {
				AreaTree tree = new AreaTree();
				BeanUtils.copyProperties(areaTree, tree);
				List<AreaTree> childrens = findTree(String.valueOf(areaTree.getAreaCode()), nodeIds);
				tree.setChildAreaList(childrens);
				trees.add(tree);
			}
		}
		return trees;
	}

	public AreaTree getAreaById(String areaCode){
		return  baseMapper.getAreaById(areaCode);
	}

	public List<AreaTree> getChildAreaList(String areaCode){
		return  baseMapper.getChildAreaList(areaCode);
	}
}
