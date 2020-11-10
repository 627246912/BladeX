package org.springblade.energy.statistics.repository;

import org.springblade.bean.DeviceItemHistoryDiffData;
import org.springblade.core.mp.support.Condition;
import org.springblade.energy.diagram.entity.DiagramItem;
import org.springblade.energy.diagram.service.IDiagramItemService;
import org.springblade.energy.diagram.service.IDiagramProductService;
import org.springblade.energy.runningmanagement.station.service.IStationService;
import org.springblade.gw.feign.IDeviceItemHistoryClient;
import org.springblade.util.BigDecimalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bond
 * @date 2020/5/25 14:06
 * @desc
 */
@Component
public class ReportDataRepository {
	@Autowired
	private IStationService stationService;
	@Autowired
	private IDiagramProductService iDiagramProductService;
	@Autowired
	private IDiagramItemService iDiagramItemService;
	@Autowired
	private IDeviceItemHistoryClient iDeviceItemHistoryClient;

	/**
	 * 查询具体数据项
	 * @return
	 */
	public DiagramItem queryDiagramItem(Long diagramProductId,String propertyCode,Integer btype){
		DiagramItem  diagramItem=new DiagramItem();
		diagramItem.setPropertyCode(propertyCode);
		diagramItem.setDiagramProductId(diagramProductId);
		diagramItem.setBtype(btype);
		DiagramItem data=iDiagramItemService.getOne(Condition.getQueryWrapper(diagramItem));
		return data;
	}

	/**
	 * 数据项数据最值和平均值
	 * @return
	 */
	public Map<String,Double> queryItemZuizhi(List<DeviceItemHistoryDiffData> deviceItemHistoryDiffDatas){
		double maxval=0;
		double minval=0;
		double vagval=0;
		double countval=0;
		for(DeviceItemHistoryDiffData data:deviceItemHistoryDiffDatas){
			maxval=BigDecimalUtil.returnMax(maxval,data.getVal());
			minval=BigDecimalUtil.returnMin(minval,data.getVal());
			countval=BigDecimalUtil.add(countval,data.getAvg());
		}
		vagval= BigDecimalUtil.div(countval,deviceItemHistoryDiffDatas.size(),4);
		Map<String,Double> map =new HashMap<>();
		map.put("maxval",maxval);
		map.put("minval",minval);
		map.put("vagval",vagval);
		return map;
	}


}
