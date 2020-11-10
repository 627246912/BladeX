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

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.bean.DeviceItemHistoryDiffData;
import org.springblade.constants.XYDdatas;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.deptcost.dto.TurnInvoiceDTO;
import org.springblade.energy.deptcost.entity.TurnInvoice;
import org.springblade.energy.deptcost.entity.TurnUnitData;
import org.springblade.energy.deptcost.mapper.TurnInvoiceMapper;
import org.springblade.energy.deptcost.service.ITurnInvoiceService;
import org.springblade.energy.deptcost.service.ITurnUnitDataService;
import org.springblade.energy.deptcost.vo.TurnInvoiceVO;
import org.springblade.energy.statistics.repository.CurveDataRepository;
import org.springblade.enums.DeviceItemCycle;
import org.springblade.enums.HistoryDataType;
import org.springblade.gw.feign.IDeviceItemHistoryClient;
import org.springblade.util.BigDecimalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 电量转供发票 服务实现类
 *
 * @author bond
 * @since 2020-08-07
 */
@Service
public class TurnInvoiceServiceImpl extends BaseServiceImpl<TurnInvoiceMapper, TurnInvoice> implements ITurnInvoiceService {
	@Autowired
	private IDeviceItemHistoryClient iDeviceItemHistoryClient;
	@Autowired
	private CurveDataRepository curveDataRepository;
	@Autowired
	private ITurnUnitDataService iTurnUnitDataService;
	@Override
	public IPage<TurnInvoiceDTO> selectTurnInvoicePage(IPage<TurnInvoiceDTO> page, TurnInvoiceVO turnInvoice) {
		List<TurnInvoiceDTO> list= baseMapper.selectTurnInvoicePage(page, turnInvoice);
		for(TurnInvoiceDTO dto:list){
			TurnInvoice entity=new TurnInvoice();
			entity.setUnitId(dto.getUnitId());
			entity.setInvoiceTime(dto.getInvoiceTime());
			List<TurnInvoice> invoiceList=baseMapper.selectList(Condition.getQueryWrapper(entity));
			dto.setInvoiceList(invoiceList);
			setTurnInvoiceDTO(dto,turnInvoice.getInvoiceTime());
			Float invoiceCost=0f;
			for(TurnInvoice invoicedata:invoiceList){
				invoiceCost=invoiceCost+ BigDecimalUtil.convertsToFloat(invoicedata.getInvoicePrice());
			}
			dto.setContrast(invoiceCost);
			Float errVal=BigDecimalUtil.subF(dto.getCost()==null?0f:dto.getCost(),invoiceCost);
			Float errRate=BigDecimalUtil.divF(errVal,invoiceCost,4);
			dto.setErrRate(String.valueOf(errRate)+"%");

		}


		return page.setRecords(list);
	}

	public void setTurnInvoiceDTO(TurnInvoiceDTO dto, String invoiceTime){
		if(Func.equals(dto.getReadType(),"1")) {//手动
			TurnUnitData turnUnitData=new TurnUnitData();
			turnUnitData.setUnitId(dto.getUnitId());
			turnUnitData.setMonthTime(dto.getInvoiceTime());
			List<TurnUnitData> list=iTurnUnitDataService.list(Condition.getQueryWrapper(turnUnitData));
			for(TurnUnitData unitData:list){
				dto.setCost(BigDecimalUtil.convertsToFloat(unitData.getCost()==null?0f:unitData.getCost()));
				dto.setVal(BigDecimalUtil.convertsToFloat(unitData.getVal()==null?0f:unitData.getVal()));
			}
		}
		if(Func.equals(dto.getReadType(),"2")){//自动
		String stime= invoiceTime+"-01 00:00:00";
		String etime= invoiceTime+"-01 23:59:59";

		if(Func.isEmpty(dto.getItemId())){
			return;
		}
		List<DeviceItemHistoryDiffData> deviceItemHistoryDiffDatas = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
			dto.getItemId(), stime, etime,DeviceItemCycle.MONTH.id, HistoryDataType.TOTAL.id);
		Map<String, Float> itemsSumValue = curveDataRepository.itemsSumValue(deviceItemHistoryDiffDatas);
		Float YSubVals=itemsSumValue.get(XYDdatas.YSubVals);
		Float YPrices=itemsSumValue.get(XYDdatas.YPrices);

		dto.setCost(YPrices);
		dto.setVal(YSubVals);
	}
	}

}
