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
package org.springblade.energy.deptcost.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.bean.DeviceItemHistoryDiffData;
import org.springblade.common.cache.CacheNames;
import org.springblade.constants.CoreCommonConstant;
import org.springblade.constants.XYDdatas;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.deptcost.dto.DeptInvoiceDTO;
import org.springblade.energy.deptcost.entity.Invoice;
import org.springblade.energy.deptcost.mapper.InvoiceMapper;
import org.springblade.energy.deptcost.service.IInvoiceService;
import org.springblade.energy.deptcost.vo.InvoiceVO;
import org.springblade.energy.diagram.entity.DiagramItem;
import org.springblade.energy.diagram.service.IDiagramItemService;
import org.springblade.energy.statistics.repository.CurveDataRepository;
import org.springblade.enums.*;
import org.springblade.gw.feign.IDeviceItemHistoryClient;
import org.springblade.system.entity.Dept;
import org.springblade.util.BigDecimalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  服务实现类
 *
 * @author bond
 * @since 2020-07-24
 */
@Service
public class InvoiceServiceImpl extends BaseServiceImpl<InvoiceMapper, Invoice> implements IInvoiceService {
	@Autowired
	private BladeRedisCache redisCache;
	@Autowired
	private IDiagramItemService iDiagramItemService;
	@Autowired
	private IDeviceItemHistoryClient iDeviceItemHistoryClient;
	@Autowired
	private CurveDataRepository curveDataRepository;

	@Override
	public IPage<InvoiceVO> selectInvoicePage(IPage<InvoiceVO> page, Invoice invoice) {
		return page.setRecords(baseMapper.selectInvoicePage(page, invoice));
	}

	public IPage<DeptInvoiceDTO> selectDeptInvoicePage(IPage<DeptInvoiceDTO> page, Invoice invoice){
		List<DeptInvoiceDTO> list= baseMapper.selectDeptInvoicePage(page, invoice);
		for(DeptInvoiceDTO deptInvoiceDTO:list){
			Dept dept = redisCache.hGet(CacheNames.DEPT_KEY, deptInvoiceDTO.getDeptId());
			if(Func.isNotEmpty(dept)){
				deptInvoiceDTO.setDeptName(dept.getDeptName());
			}
			Invoice queryinvoice=new Invoice();
			queryinvoice.setDeptId(deptInvoiceDTO.getDeptId());
			queryinvoice.setInvoiceTime(invoice.getInvoiceTime());
			queryinvoice.setInvoiceType(invoice.getInvoiceType());
			List<Invoice> invoiceList=baseMapper.selectInvoicePage(queryinvoice);
			deptInvoiceDTO.setInvoiceList(invoiceList);
			setDeptInvoiceDTO(deptInvoiceDTO,invoice.getInvoiceTime(),invoice.getInvoiceType());
			Float invoiceCost=0f;
			for(Invoice invoicedata:invoiceList){
				invoiceCost=invoiceCost+ BigDecimalUtil.convertsToFloat(invoicedata.getInvoicePrice());
			}
			deptInvoiceDTO.setContrast(invoiceCost);
			Float errVal=BigDecimalUtil.subF(deptInvoiceDTO.getCost(),invoiceCost);
			Float errRate=BigDecimalUtil.divF(errVal,invoiceCost,4);
			deptInvoiceDTO.setErrRate(String.valueOf(errRate*100)+"%");
		}

		return page.setRecords(list);
	}

	public void setDeptInvoiceDTO(DeptInvoiceDTO deptInvoiceDTO,String invoiceTime,Integer energyType){


		String stime= invoiceTime+"-01 00:00:00";
		String etime= invoiceTime+"-01 23:59:59";

		//查询具体数据项
		Map<String,Object> queryMap=new HashMap<>();
		if(Func.equals(energyType, EnergyType.POWER.id)){
			queryMap.put("propertyCode", ProductSid.SID31.id);
			//queryMap.put("btype",ItemBtype.ELECTRICITY.id);
		}
		if(Func.equals(energyType, EnergyType.WATER.id)){
			queryMap.put("propertyCode", ProductSid.SID159.id);
			//queryMap.put("btype",ItemBtype.WATERVOLUME.id);
		}
		if(Func.equals(energyType, EnergyType.GAS.id)){
			queryMap.put("propertyCode", ProductSid.SID161.id);
			//queryMap.put("btype",ItemBtype.AIRVOLUME.id);
		}
		queryMap.put("deptId",deptInvoiceDTO.getDeptId());

		List<DiagramItem> diagramItemList=iDiagramItemService.selectDiagramItemByMap(queryMap);

		List<String> items =new ArrayList<>();
		for(DiagramItem diagramItem:diagramItemList) {
			items.add(diagramItem.getItemId());
		}

		List<DeviceItemHistoryDiffData> deviceItemHistoryDiffDatas = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
			CollectionUtil.join(items, CoreCommonConstant.SPLIT_COMMA_KEY), stime, etime,
			DeviceItemCycle.MONTH.id, HistoryDataType.TOTAL.id);

		Map<String, Float> itemsSumValue = curveDataRepository.itemsSumValue(deviceItemHistoryDiffDatas);
		Float YSubVals=itemsSumValue.get(XYDdatas.YSubVals);
		Float YPrices=itemsSumValue.get(XYDdatas.YPrices);

		deptInvoiceDTO.setCost(YPrices);
		deptInvoiceDTO.setVal(YSubVals);
	}

}
