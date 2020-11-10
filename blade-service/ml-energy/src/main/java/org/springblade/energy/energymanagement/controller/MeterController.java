package org.springblade.energy.energymanagement.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.dto.JfpgPriceNew;
import org.springblade.dto.WaterOrGasPrice;
import org.springblade.energy.diagram.controller.DiagramController;
import org.springblade.energy.diagram.dto.DiagramItemReq;
import org.springblade.energy.diagram.entity.DiagramItem;
import org.springblade.energy.diagram.entity.DiagramProduct;
import org.springblade.energy.diagram.service.IDiagramItemService;
import org.springblade.energy.diagram.service.IDiagramProductService;
import org.springblade.energy.energymanagement.dto.*;
import org.springblade.energy.energymanagement.entity.EnergyConsumeType;
import org.springblade.energy.energymanagement.entity.GasMeter;
import org.springblade.energy.energymanagement.entity.PowerMeter;
import org.springblade.energy.energymanagement.entity.WaterMeter;
import org.springblade.energy.energymanagement.service.IEnergyConsumeTypeService;
import org.springblade.energy.energymanagement.service.IGasMeterService;
import org.springblade.energy.energymanagement.service.IPowerMeterService;
import org.springblade.energy.energymanagement.service.IWaterMeterService;
import org.springblade.enums.DiagramType;
import org.springblade.enums.WaterGasPriceType;
import org.springblade.gw.feign.IDataItemClient;
import org.springblade.system.feign.IDictBizClient;
import org.springblade.util.BigDecimalUtil;
import org.springblade.util.ListUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author bond
 * @date 2020/6/10 13:53
 * @desc
 */
@RestController
@AllArgsConstructor
@RequestMapping("/meter")
@Api(value = "能源管理-计量管账", tags = "能源管理-计量管账")
public class MeterController {
	@Autowired
	private MeterRepository meterRepository;
	@Autowired
	private IDiagramItemService iDiagramItenService;
	@Autowired
	private DiagramController diagramController;
	@Autowired
	private IPowerMeterService iPowerMeterService;
	@Autowired
	private IWaterMeterService iWaterMeterService;
	@Autowired
	private IGasMeterService iGasMeterService;

	@Autowired
	private IDataItemClient iDataItemClient;

	@Autowired
	private IDictBizClient dictBizClient;
	@Autowired
	private IDiagramProductService diagramProductService;
	@Autowired
	private IEnergyConsumeTypeService energyConsumeTypeService;



	@GetMapping("/getPowerMeterList")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "供电计量-计量点配置列表", notes = "")
	public R<IPage<PowerMeterDto>> getPowerMeterList(Query query,
													 @ApiParam(value = "站点id") @RequestParam(value = "stationId" ,required=false) Long stationId,
													 @ApiParam(value = "位置id") @RequestParam(value = "siteId",required=false) Long siteId,
													 @ApiParam(value = "等级（1，2，3）") @RequestParam(value = "level") Integer level)
	{
		//处理未加入计量表的数据
		meterRepository.insertPowerMeter(stationId,siteId);

		String diagramType =null;
		//一级为高压，查询高压所有馈线开关
		if(Func.equals(1,level)){
			diagramType = DiagramType.GAOYA.id;
		}
	//二级为中压，查询中压所有馈线开关
		if(Func.equals(2,level)){
			diagramType = DiagramType.ZHONGYA.id;
		}
		//三级为低压
		if(Func.equals(3,level)){
			diagramType= DiagramType.DIYA.id;
		}

		//总用电量 变比
		PowerMeterDto dto =new PowerMeterDto();
		dto.setStationId(stationId);
		dto.setSiteId(siteId);
		dto.setDiagramType(diagramType);
		IPage<PowerMeterDto> list=iPowerMeterService.selectPowerMeterPage(Condition.getPage(query), dto);
		return R.data(list);
	}

	@ApiLog("修改 供电计量-计量点配置")
	@PostMapping("/updatePowerMeter")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "供电计量-计量点配置", notes = "")
	//@Transactional(rollbackFor = Exception.class)
	public R updatePowerMeter(@RequestBody List<PowerMeterDto> powerMeterDTOs){
		//1.修改系统图的数据项
		List<DiagramItemReq> diagramItemReqs =new ArrayList<>();
		List<JfpgPriceNew> jfpgPriceNewList = new ArrayList<>();
		List<PowerMeter> powerMeters = new ArrayList<>();
		for(PowerMeterDto powerMeterDto :powerMeterDTOs) {
			PowerMeter power = new PowerMeter();
			BeanUtils.copyProperties(powerMeterDto, power);
			powerMeters.add(power);

			Map<String,Object> map =new HashMap<>();
			map.put("itemId",powerMeterDto.getItemId());
			List<DiagramItem> diagramItems = iDiagramItenService.selectDiagramItemByMap(map);

			//修改产品名
			if(Func.isNotEmpty(powerMeterDto.getDiagramProductId())){
				DiagramProduct product = diagramProductService.getById(powerMeterDto.getDiagramProductId());
				if(Func.isNotEmpty(product)){
					product.setProductcname(powerMeterDto.getProductcname());
					product.setDeptId(powerMeterDto.getDeptId());
					product.setElectricTypekey(powerMeterDto.getElectricTypekey());
					diagramProductService.saveOrUpdate(product);
				}
			}
			for(DiagramItem diagramItem:diagramItems) {
				DiagramItemReq diagramItemReq = new DiagramItemReq();
				BeanUtils.copyProperties(diagramItem, diagramItemReq);
				diagramItemReq.setCtratio(powerMeterDto.getCtratio());

				diagramItemReqs.add(diagramItemReq);
			}

			JfpgPriceNew jfpgPriceNew =new JfpgPriceNew();
			jfpgPriceNew.setItemid(powerMeterDto.getItemId());
			jfpgPriceNew.setFlat(powerMeterDto.getFlat());


			jfpgPriceNew.setFlatPrice(BigDecimalUtil.convertsToFloat(powerMeterDto.getFlatPrice()));
			jfpgPriceNew.setPeak(powerMeterDto.getPeak());
			jfpgPriceNew.setPeakPrice(BigDecimalUtil.convertsToFloat(powerMeterDto.getPeakPrice()));
			jfpgPriceNew.setTop(powerMeterDto.getTop());
			jfpgPriceNew.setTopPrice(BigDecimalUtil.convertsToFloat(powerMeterDto.getTopPrice()));
			jfpgPriceNew.setValley(powerMeterDto.getValley());
			jfpgPriceNew.setValleyPrice(BigDecimalUtil.convertsToFloat(powerMeterDto.getValleyPrice()));
			jfpgPriceNewList.add(jfpgPriceNew);
		}
		R r=diagramController.updateDiagramItem(diagramItemReqs);
		if(r.isSuccess()){
			iPowerMeterService.updateBatchById(powerMeters);
		}
		if(r.isSuccess()){
			iDataItemClient.setJfpgPriceNew(jfpgPriceNewList);
		}
		return r;

	}

	@ApiLog("批量修改 供电计量-计量点配置")
	@PostMapping("/batchUpdatePowerMeter")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "供电计量-批量修改计量点配置", notes = "")
	//@Transactional(rollbackFor = Exception.class)
	public R batchUpdatePowerMeter(@RequestBody PowerMeterReq powerMeterReq){
		if(Func.isEmpty(powerMeterReq.getStationId()) || Func.isEmpty(powerMeterReq.getSiteId())){
			return R.fail("站点位置不能为空");
		}
		if(Func.isEmpty(powerMeterReq.getDiagramType())){
			return R.fail("等级不能为空");
		}
		Map<String,Object> map=new HashMap<>();
		map.put("stationId",powerMeterReq.getStationId());
		map.put("siteId",powerMeterReq.getSiteId());
		map.put("diagramType",powerMeterReq.getDiagramType());
		List<PowerMeterDto> list=iPowerMeterService.getPowerMeterItem(map);
		List<PowerMeter> meterList=new ArrayList<>();

		List<JfpgPriceNew> jfpgPriceNewList = new ArrayList<>();

		for (PowerMeterDto dto:list){
			PowerMeter meter=new PowerMeter();
			BeanUtils.copyProperties(dto,meter);
			if(Func.isNotEmpty(powerMeterReq.getCtratio())){
				meter.setCtratio(powerMeterReq.getCtratio());

			}
			if(Func.isNotEmpty(powerMeterReq.getValleyPrice())){
				meter.setValleyPrice(powerMeterReq.getValleyPrice());

			}
			if(Func.isNotEmpty(powerMeterReq.getFlatPrice())){
				meter.setFlatPrice(powerMeterReq.getFlatPrice());

			}
			if(Func.isNotEmpty(powerMeterReq.getPeakPrice())){
				meter.setPeakPrice(powerMeterReq.getPeakPrice());

			}
			if(Func.isNotEmpty(powerMeterReq.getTopPrice())){
				meter.setTopPrice(powerMeterReq.getTopPrice());

			}
			meterList.add(meter);



			JfpgPriceNew jfpgPriceNew =new JfpgPriceNew();
			jfpgPriceNew.setItemid(meter.getItemId());
			jfpgPriceNew.setFlat(meter.getFlat());


			jfpgPriceNew.setFlatPrice(BigDecimalUtil.convertsToFloat(meter.getFlatPrice()));
			jfpgPriceNew.setPeak(meter.getPeak());
			jfpgPriceNew.setPeakPrice(BigDecimalUtil.convertsToFloat(meter.getPeakPrice()));
			jfpgPriceNew.setTop(meter.getTop());
			jfpgPriceNew.setTopPrice(BigDecimalUtil.convertsToFloat(meter.getTopPrice()));
			jfpgPriceNew.setValley(meter.getValley());
			jfpgPriceNew.setValleyPrice(BigDecimalUtil.convertsToFloat(meter.getValleyPrice()));
			jfpgPriceNewList.add(jfpgPriceNew);


		}
		Boolean r= iPowerMeterService.updateBatchById(meterList);
		if(r){
			iDataItemClient.setJfpgPriceNew(jfpgPriceNewList);
		}
		return R.status(r);

	}

	@GetMapping("/getWaterMeterList")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "供水计量-计量点配置列表", notes = "")
	public R<IPage<WaterMeterDto>> getWaterMeterList(Query query,
													 @ApiParam(value = "站点id") @RequestParam(value = "stationId" ,required=false) Long stationId,
													 @ApiParam(value = "位置id") @RequestParam(value = "siteId",required=false) Long siteId,
													 @ApiParam(value = "等级（1，2）") @RequestParam(value = "level") Integer level)
	{
		//处理未加入计量表的数据
		meterRepository.insertWaterMeter(stationId,siteId);

		WaterMeterDto dto =new WaterMeterDto();
		dto.setStationId(stationId);
		dto.setSiteId(siteId);
		dto.setGrade(level);
		IPage<WaterMeterDto> list=iWaterMeterService.selectWaterMeterPage(Condition.getPage(query), dto);
		return R.data(list);
	}
	@ApiLog("修改 供水计量-计量点配置")
	@PostMapping("/updateWaterMeter")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "供水计量-计量点配置", notes = "")
	//@Transactional(rollbackFor = Exception.class)
	public R updateWaterMeter(@RequestBody List<WaterMeter> waterMeters){
		boolean r= iWaterMeterService.updateBatchById(waterMeters);
		if(r) {
			List<WaterOrGasPrice> waterPriceList = new ArrayList<>();

			for(WaterMeter waterMeter:waterMeters) {
				WaterOrGasPrice waterPrice = new WaterOrGasPrice();
				waterPrice.setItemid(waterMeter.getItemId());
				waterPrice.setType(WaterGasPriceType.WATER.id);
				waterPrice.setPrice(BigDecimalUtil.convertsToFloat(waterMeter.getPrice()));
				waterPriceList.add(waterPrice);
			}
			iDataItemClient.setWaterOrGasPrice(waterPriceList);
		}
		return R.status(r);
	}

	@ApiLog("批量修改 供水计量-计量点配置")
	@PostMapping("/batchUpdateWaterMeter")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "供水计量-批量修改计量点配置", notes = "")
	//@Transactional(rollbackFor = Exception.class)
	public R batchUpdateWaterMeter(@RequestBody WaterMeterReq waterMeterReq){
		if(Func.isEmpty(waterMeterReq.getStationId()) || Func.isEmpty(waterMeterReq.getSiteId())){
			return R.fail("站点位置不能为空");
		}
		if(Func.isEmpty(waterMeterReq.getGrade())){
			return R.fail("等级不能为空");
		}
		Map<String,Object> map=new HashMap<>();
		map.put("stationId",waterMeterReq.getStationId());
		map.put("siteId",waterMeterReq.getSiteId());
		map.put("grade",waterMeterReq.getGrade());
		List<WaterMeterDto> list=iWaterMeterService.getWaterMeterItem(map);
		List<WaterMeter> meterList=new ArrayList<>();
		List<WaterOrGasPrice> waterPriceList = new ArrayList<>();
		for (WaterMeterDto dto:list){
			WaterMeter meter=new WaterMeter();
			BeanUtils.copyProperties(dto,meter);
			if(Func.isNotEmpty(waterMeterReq.getPrice())) {
				meter.setPrice(waterMeterReq.getPrice());
			}
			if(Func.isNotEmpty(waterMeterReq.getMeterType())) {
				meter.setMeterType(waterMeterReq.getMeterType());
			}
			if(Func.isNotEmpty(waterMeterReq.getMeterTime())) {
				meter.setMeterTime(waterMeterReq.getMeterTime());
			}
			meterList.add(meter);

			WaterOrGasPrice waterPrice = new WaterOrGasPrice();
			waterPrice.setItemid(meter.getItemId());
			waterPrice.setType(WaterGasPriceType.WATER.id);
			waterPrice.setPrice(BigDecimalUtil.convertsToFloat(meter.getPrice()));
			waterPriceList.add(waterPrice);

		}
		Boolean r= iWaterMeterService.updateBatchById(meterList);
		if(r) {
			iDataItemClient.setWaterOrGasPrice(waterPriceList);
		}
		return R.status(r);

	}



	@GetMapping("/getGasMeterList")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "供气计量-计量点配置列表", notes = "")
	public R<IPage<GasMeterDto>> getGasMeterList(Query query,
													 @ApiParam(value = "站点id") @RequestParam(value = "stationId" ,required=false) Long stationId,
													 @ApiParam(value = "位置id") @RequestParam(value = "siteId",required=false) Long siteId)
	{

		//处理未加入计量表的数据
		meterRepository.insertGasMeter(stationId,siteId);

		GasMeterDto dto =new GasMeterDto();
		dto.setStationId(stationId);
		dto.setSiteId(siteId);
		//dto.setGrade(level);
		IPage<GasMeterDto> list=iGasMeterService.selectGasMeterPage(Condition.getPage(query), dto);
		return R.data(list);
	}

	private static String months="01,02,03,04,05,06,07,08,09,10,11,12";
	//@ApiLog("修改 供气计量-计量点配置")
	@PostMapping("/updateGasMeter")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "供气计量-计量点配置", notes = "")
	//@Transactional(rollbackFor = Exception.class)
	public R updateGasMeter(@RequestBody List<GasMeter> gasMeters){
		Boolean r=iGasMeterService.updateBatchById(gasMeters);
		List<WaterOrGasPrice> gasPriceList=new ArrayList<>();
		for(GasMeter gasMeter:gasMeters){
			WaterOrGasPrice gasPrice = new WaterOrGasPrice();
			gasPrice.setItemid(gasMeter.getItemId());
			gasPrice.setType(WaterGasPriceType.GAS_PRICE_TYPE.id);
			String busyMonths=gasMeter.getBusySeason();
			gasPrice.setBusySeason(busyMonths);
			List<String>  lows= ListUtils.getDiffrent(Arrays.asList(months.split(",")),Arrays.asList(busyMonths.split(",")));
			String lowMonths=ListUtils.ListToStringCommaSymbol(lows);
			gasPrice.setLowSeason(lowMonths);

			gasPrice.setLowSeasonPrice(BigDecimalUtil.convertsToFloat(gasMeter.getLowSeasonPrice()));
			gasPrice.setBusySeasonPrice(BigDecimalUtil.convertsToFloat(gasMeter.getBusySeasonPrice()));
			gasPriceList.add(gasPrice);
		}
		if(r){
			iDataItemClient.setWaterOrGasPrice(gasPriceList);
		}
		return R.status(r);
	}

	//@ApiLog("批量修改 供器计量-计量点配置")
	@PostMapping("/batchUpdateGasMeter")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "供气计量-批量修改计量点配置", notes = "")
	//@Transactional(rollbackFor = Exception.class)
	public R batchUpdateGasMeter(@RequestBody GasMeterReq gasMeterReq){
		if(Func.isEmpty(gasMeterReq.getStationId()) || Func.isEmpty(gasMeterReq.getSiteId())){
			return R.fail("站点位置不能为空");
		}
		Map<String,Object> map=new HashMap<>();
		map.put("stationId",gasMeterReq.getStationId());
		map.put("siteId",gasMeterReq.getSiteId());
		List<GasMeterDto> list=iGasMeterService.getGasMeterItem(map);
		List<GasMeter> meterList=new ArrayList<>();
		List<WaterOrGasPrice> gasPriceList=new ArrayList<>();

		for (GasMeterDto dto:list){
			GasMeter meter=new GasMeter();
			BeanUtils.copyProperties(dto,meter);
			if(Func.isNotEmpty(gasMeterReq.getLowSeasonPrice())) {
				meter.setLowSeasonPrice(gasMeterReq.getLowSeasonPrice());

			}if(Func.isNotEmpty(gasMeterReq.getBusySeasonPrice())) {
				meter.setBusySeasonPrice(gasMeterReq.getBusySeasonPrice());
			}
			if(Func.isNotEmpty(gasMeterReq.getMeterType())) {
				meter.setMeterType(gasMeterReq.getMeterType());
			}
			if(Func.isNotEmpty(gasMeterReq.getMeterTime())) {
				meter.setMeterTime(gasMeterReq.getMeterTime());
			}
			meterList.add(meter);



			WaterOrGasPrice gasPrice = new WaterOrGasPrice();
			gasPrice.setItemid(meter.getItemId());
			gasPrice.setType(WaterGasPriceType.GAS_PRICE_TYPE.id);
			gasPrice.setLowSeason(meter.getLowSeason());
			gasPrice.setBusySeason(meter.getBusySeason());
			gasPrice.setLowSeasonPrice(BigDecimalUtil.convertsToFloat(meter.getLowSeasonPrice()));
			gasPrice.setBusySeasonPrice(BigDecimalUtil.convertsToFloat(meter.getBusySeasonPrice()));
			gasPriceList.add(gasPrice);

		}
		Boolean r= iGasMeterService.updateBatchById(meterList);
		if(r){
			iDataItemClient.setWaterOrGasPrice(gasPriceList);
		}
		return R.status(r);

	}


	@GetMapping("/getPowerMeterTree")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "供电计量-计量点树形列表", notes = "")
	public R<List<PowerMeterTreeDto>> getPowerMeterTree(@ApiParam(value = "是否显示系统图产品id") @RequestParam(value = "showDiagramProductId" ,required=false) Integer showDiagramProductId)
	{
		Map map = new HashMap();
		map.put("show",showDiagramProductId);
		List<PowerMeterDto> list=iPowerMeterService.getPowerMeterItem(map);
		List<PowerMeterTreeDto> backList = new ArrayList<PowerMeterTreeDto>();
		List<PowerMeterTreeDto> sencodList = new ArrayList<PowerMeterTreeDto>();
		List<PowerMeterTreeDto> thirdList = new ArrayList<PowerMeterTreeDto>();

		List<PowerMeterTreeDto> allList = new ArrayList<PowerMeterTreeDto>();

		//从t_energy_consume_type表中取用电类型
		EnergyConsumeType energyConsumeType=new EnergyConsumeType();
		energyConsumeType.setEnergyType(1);//电
		energyConsumeType.setIsDeleted(0);
		List<EnergyConsumeType> consumeTypeslist =energyConsumeTypeService.list(Condition.getQueryWrapper(energyConsumeType));

		for (PowerMeterDto powerMeterDto:list){
			PowerMeterTreeDto treeDto = new PowerMeterTreeDto();
			BeanUtil.copy(powerMeterDto,treeDto);
			if(powerMeterDto.getProductcname().contains("_")){
				String[] viewIdArr = powerMeterDto.getProductcname().split("_");
				String viewId = viewIdArr[0];
				String[] ids = viewId.split("\\.");
				//从字典取用电类型
//				if(Func.isNotEmpty(powerMeterDto.getElectricTypekey())){
//					R<String> name = dictBizClient.getValue("electric_type",powerMeterDto.getElectricTypekey());
//					treeDto.setElectricTypeName(name.getData());
//				}
				if(Func.isNotEmpty(powerMeterDto.getElectricTypekey())){
					if(Func.isNotEmpty(consumeTypeslist)){
						for(EnergyConsumeType type: consumeTypeslist){
							if(powerMeterDto.getElectricTypekey()==type.getConsumeType()){
								treeDto.setElectricTypeName(type.getConsumeName());
								break;
							}
						}
					}
				}
				if(ids.length==1){//一级
					treeDto.setViewId(ids[0]);
					treeDto.setNodeLevel(1);
					backList.add(treeDto);
				}else if (ids.length==2){//二级
					treeDto.setViewId(ids[0]+"."+ids[1]);
					treeDto.setNodeLevel(2);
					sencodList.add(treeDto);
				}else if(ids.length==3){//三级
					treeDto.setViewId(viewId);
					treeDto.setNodeLevel(3);
					thirdList.add(treeDto);
				}
			}
		}
		for(PowerMeterTreeDto secondDto :sencodList){
			List<PowerMeterTreeDto> sonList = new ArrayList<PowerMeterTreeDto>();
			for(PowerMeterTreeDto thirdDto:thirdList){
				String [] a = thirdDto.getViewId().split("\\.");
				if((a[0]+"."+a[1]).equals(secondDto.getViewId())){//
					sonList.add(thirdDto);
				}
			}
			//第三级
			Collections.sort(sonList,new comparObj());
			secondDto.setNextLevelObjs(sonList);
		}
		for(PowerMeterTreeDto dto :backList){
			List<PowerMeterTreeDto> sonList = new ArrayList<PowerMeterTreeDto>();
			for(PowerMeterTreeDto secondDto:sencodList){
				String [] a = secondDto.getViewId().split("\\.");
				if(a[0].equals(dto.getViewId())){//
					sonList.add(secondDto);
				}
			}
			//第二级
			Collections.sort(sonList,new comparObj());
			dto.setNextLevelObjs(sonList);
			allList.add(dto);
		}
		//第一级
		Collections.sort(allList,new comparObj());
		return R.data(allList);
	}

	class comparObj implements Comparator{
		@Override
		public int compare(Object o1, Object o2) {
			if(o1 instanceof PowerMeterTreeDto && o2 instanceof PowerMeterTreeDto){
				PowerMeterTreeDto e1 = (PowerMeterTreeDto) o1;
				PowerMeterTreeDto e2 = (PowerMeterTreeDto) o2;
				String[] ids1 = e1.getViewId().split("\\.");
				String[] ids2 = e2.getViewId().split("\\.");
				if(ids1.length==1 && ids2.length==1){
					return Integer.parseInt(ids1[0]) - Integer.parseInt(ids2[0]);
				}else if (ids1.length==2 && ids2.length==2){
					return Integer.parseInt(ids1[1]) - Integer.parseInt(ids2[1]);
				}else if (ids1.length==3 && ids2.length==3){
					return Integer.parseInt(ids1[2]) - Integer.parseInt(ids2[2]);
				}
			}
			return 0;
		}
	}


}
