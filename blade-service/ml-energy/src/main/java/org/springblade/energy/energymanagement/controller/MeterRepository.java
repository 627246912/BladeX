package org.springblade.energy.energymanagement.controller;

import org.springblade.dto.JfpgPriceNew;
import org.springblade.dto.WaterOrGasPrice;
import org.springblade.energy.diagram.entity.DiagramItem;
import org.springblade.energy.diagram.service.IDiagramItemService;
import org.springblade.energy.energymanagement.entity.GasMeter;
import org.springblade.energy.energymanagement.entity.PowerMeter;
import org.springblade.energy.energymanagement.entity.WaterMeter;
import org.springblade.energy.energymanagement.service.IGasMeterService;
import org.springblade.energy.energymanagement.service.IPowerMeterService;
import org.springblade.energy.energymanagement.service.IWaterMeterService;
import org.springblade.energy.runningmanagement.station.entity.Station;
import org.springblade.energy.runningmanagement.station.service.IStationService;
import org.springblade.enums.WaterGasPriceType;
import org.springblade.gw.feign.IDataItemClient;
import org.springblade.util.BigDecimalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bond
 * @date 2020/7/21 16:33
 * @desc
 */
@Component
public class MeterRepository {
	@Autowired
	private IStationService iStationService;
	@Autowired
	private IDiagramItemService iDiagramItenService;
	@Autowired
	private IPowerMeterService iPowerMeterService;
	@Autowired
	private IWaterMeterService iWaterMeterService;
	@Autowired
	private IGasMeterService iGasMeterService;

	@Autowired
	private IDataItemClient iDataItemClient;

	public void insertPowerMeter(Long stationId ,Long siteId) {
		//处理未加入计量表的数据
		Map<String, Object> querymap = new HashMap<>();
		querymap.put("stationId", stationId);
		querymap.put("siteId", siteId);
		List<DiagramItem> listItme = iDiagramItenService.getNotInPowerMeterItem(querymap);

		List<PowerMeter> powerMeterList = new ArrayList<>();

		List<JfpgPriceNew> jfpgPriceNewList = new ArrayList<>();

		for (DiagramItem diagramItem : listItme) {
			Station station = iStationService.getById(diagramItem.getStationId());
			PowerMeter powerMeter = new PowerMeter();
			powerMeter.setItemId(diagramItem.getItemId());
			powerMeter.setCtratio(diagramItem.getCtratio());
			powerMeter.setTop(station.getTop());
			powerMeter.setPeak(station.getPeak());
			powerMeter.setFlat(station.getFlat());
			powerMeter.setValley(station.getValley());
			powerMeter.setTopPrice(station.getTopPrice());
			powerMeter.setPeakPrice(station.getPeakPrice());
			powerMeter.setFlatPrice(station.getFlatPrice());
			powerMeter.setValleyPrice(station.getValleyPrice());
			powerMeterList.add(powerMeter);

			JfpgPriceNew jfpgPriceNew =new JfpgPriceNew();
			jfpgPriceNew.setItemid(diagramItem.getItemId());
			jfpgPriceNew.setFlat(station.getFlat());


			jfpgPriceNew.setFlatPrice(BigDecimalUtil.convertsToFloat(station.getFlatPrice()));
			jfpgPriceNew.setPeak(station.getPeak());
			jfpgPriceNew.setPeakPrice(BigDecimalUtil.convertsToFloat(station.getPeakPrice()));
			jfpgPriceNew.setTop(station.getTop());
			jfpgPriceNew.setTopPrice(BigDecimalUtil.convertsToFloat(station.getTopPrice()));
			jfpgPriceNew.setValley(station.getValley());
			jfpgPriceNew.setValleyPrice(BigDecimalUtil.convertsToFloat(station.getValleyPrice()));
			jfpgPriceNewList.add(jfpgPriceNew);
		}
		Boolean r= iPowerMeterService.saveBatch(powerMeterList);
		if(r){
			iDataItemClient.setJfpgPriceNew(jfpgPriceNewList);
		}
	}

	public void insertWaterMeter(Long stationId ,Long siteId) {
		//处理未加入计量表的数据
		Map<String,Object> querymap=new HashMap<>();
		querymap.put("stationId",stationId);
		querymap.put("siteId",siteId);
		List<DiagramItem> listItme=iDiagramItenService.getNotInWaterMeterItem(querymap);

		List<WaterMeter> meterList=new ArrayList<>();
		List<WaterOrGasPrice> waterPriceList=new ArrayList<>();
		for(DiagramItem diagramItem:listItme){
			Station station=iStationService.getById(diagramItem.getStationId());
			WaterMeter meter=new WaterMeter();
			meter.setItemId(diagramItem.getItemId());
			meter.setPrice(station.getWaterPrice());
			meterList.add(meter);

			WaterOrGasPrice waterPrice=new WaterOrGasPrice();
			waterPrice.setItemid(diagramItem.getItemId());
			waterPrice.setType(WaterGasPriceType.WATER.id);
			waterPrice.setPrice(BigDecimalUtil.convertsToFloat(station.getWaterPrice()));
			waterPriceList.add(waterPrice);
		}
		Boolean r= iWaterMeterService.saveBatch(meterList);
		if(r){
			iDataItemClient.setWaterOrGasPrice(waterPriceList);
		}
	}

	public void insertGasMeter(Long stationId ,Long siteId) {
		//处理未加入计量表的数据
		Map<String,Object> querymap=new HashMap<>();
		querymap.put("stationId",stationId);
		querymap.put("siteId",siteId);
		List<DiagramItem> listItme=iDiagramItenService.getNotInGasMeterItem(querymap);

		List<GasMeter> meterList=new ArrayList<>();
		List<WaterOrGasPrice> gasPriceList=new ArrayList<>();

		for(DiagramItem diagramItem:listItme){
			Station station=iStationService.getById(diagramItem.getStationId());
			GasMeter meter=new GasMeter();
			meter.setItemId(diagramItem.getItemId());
			meter.setLowSeasonPrice(station.getWaterPrice());
			meter.setBusySeasonPrice(station.getWaterPrice());
			meter.setGasCompanyId(null);
			meterList.add(meter);
			WaterOrGasPrice gasPrice=new WaterOrGasPrice();
			gasPrice.setItemid(diagramItem.getItemId());
			gasPrice.setType(WaterGasPriceType.GAS_PRICE_TYPE.id);
			gasPrice.setLowSeasonPrice(BigDecimalUtil.convertsToFloat(station.getGasPrice()));
			gasPrice.setLowSeason("01,02,03,04,05,06,07,08,09,10,11,12");
			gasPriceList.add(gasPrice);
		}
		boolean r= iGasMeterService.saveBatch(meterList);

		if(r){
			iDataItemClient.setWaterOrGasPrice(gasPriceList);
		}
	}

}
