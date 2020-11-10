package org.springblade.pms.statistics.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.google.common.collect.Sets;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.bean.DeviceItem;
import org.springblade.bean.DeviceItemHistoryDiffData;
import org.springblade.bean.DeviceSub;
import org.springblade.constants.CoreCommonConstant;
import org.springblade.constants.GwsubscribeConstant;
import org.springblade.constants.XYDdatas;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.enums.CurveType;
import org.springblade.enums.DeviceItemCycle;
import org.springblade.enums.HistoryDataType;
import org.springblade.enums.ProductSid;
import org.springblade.pms.enums.UserGroup;
import org.springblade.pms.gw.feign.IDeviceClient;
import org.springblade.pms.gw.feign.IDeviceItemHistoryClient;
import org.springblade.pms.station.entity.RtuSet;
import org.springblade.pms.station.service.IRtuSetService;
import org.springblade.pms.statistics.dto.*;
import org.springblade.pms.statistics.repository.CurveDataRepository;
import org.springblade.pms.statistics.repository.ReportDataRepository;
import org.springblade.util.BigDecimalUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author bond
 * @date 2020/9/16 17:09
 * @desc
 */
@RestController
@AllArgsConstructor
@RequestMapping("/sta")
@Api(value = "报表统计", tags = "报表统计")
public class StatisticsController {

	private CurveDataRepository curveDataRepository;
	private IRtuSetService iRtuSetService;
	private IDeviceItemHistoryClient iDeviceItemHistoryClient;
	private IDeviceClient iDeviceClient;

	@PostMapping("/backstage/getCurveDate")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "配置端曲线图数据", notes = "")
	public R<Map<String, List<CurveDataResq>>> getCurveDateBack(@RequestBody CurveDataReq curveDataReq) {
		R r = curveDataRepository.checkParam(curveDataReq);
		if (!r.isSuccess()) {
			return r;
		}

		String gwId= curveDataReq.getGwId();
		List<String> gwIds=new ArrayList<>();
		gwIds.add(gwId);
		String userId= curveDataReq.getUserGroup();
		String rtridcb= curveDataReq.getRtuidcb();
		Integer curveType=curveDataReq.getDataCurveType();

		if(Func.isEmpty(rtridcb)) {
			//跟网关查找redis里面的com口
			List<DeviceSub> gwSubList = iDeviceClient.getDeviceSubsByGwid(gwId);
			for (DeviceSub deviceSub : gwSubList) {
				//rtu1为总路 获取电压和电流
				if (Func.equals("1", deviceSub.getRtuid())) {
					rtridcb = deviceSub.getRtuidcb();
				}
			}
		}

		Map<String, DeviceItem> DeviceItemMap =new HashMap<>();
		if(Func.isNotEmpty(rtridcb)){
			List<DeviceItem> gwItemList = iDeviceClient.getDeviceItemInfosByRtuidcb(rtridcb);
			DeviceItemMap=CurveDataFactory.buildItemSidMap(gwItemList);
		}

		List<DeviceItem> itemList=new ArrayList<>();
		//用户1
		if (Func.equals(userId, UserGroup.YIDONG.id)) {
			//用户1总电流 307
			if (Func.equals(curveType, CurveType.ELECTRICCURRENT.getId())) {
				DeviceItem deviceItem = DeviceItemMap.get(ProductSid.SID307.id);
				if (Func.isNotEmpty(deviceItem)) {
					itemList.add(deviceItem);
					curveDataReq.setSid(ProductSid.SID307.id);
				}
			}
			//用户1电能 309
			if (Func.equals(curveType,CurveType.ELECTRICITY.getId())) {
				DeviceItem deviceItem = DeviceItemMap.get(ProductSid.SID309.id);
				if (Func.isNotEmpty(deviceItem)) {
					itemList.add(deviceItem);
					curveDataReq.setSid(ProductSid.SID309.id);
				}
			}

		}
		//用户2
		if (Func.equals(userId, UserGroup.LIANTONG.id)) {
			//总电流 310
			if (Func.equals(curveType, CurveType.ELECTRICCURRENT.getId())) {
				DeviceItem deviceItem = DeviceItemMap.get(ProductSid.SID310.id);
				if (Func.isNotEmpty(deviceItem)) {
					itemList.add(deviceItem);
					curveDataReq.setSid(ProductSid.SID310.id);
				}
			}
			//用户2电能 312
			if (Func.equals(curveType,CurveType.ELECTRICITY.getId())) {
				DeviceItem deviceItem = DeviceItemMap.get(ProductSid.SID312.id);
				if (Func.isNotEmpty(deviceItem)) {
					itemList.add(deviceItem);
					curveDataReq.setSid(ProductSid.SID312.id);
				}
			}
		}

		//用户3
		if (Func.equals(userId, UserGroup.DIANXIN.id)) {
			//总电流 313
			if (Func.equals(curveType, CurveType.ELECTRICCURRENT.getId())) {
				DeviceItem deviceItem = DeviceItemMap.get(ProductSid.SID313.id);
				if (Func.isNotEmpty(deviceItem)) {
					itemList.add(deviceItem);
					curveDataReq.setSid(ProductSid.SID313.id);
				}
			}
			//用户3电能 315
			if (Func.equals(curveType,CurveType.ELECTRICITY.getId())) {
				DeviceItem deviceItem = DeviceItemMap.get(ProductSid.SID315.id);
				if (Func.isNotEmpty(deviceItem)) {
					itemList.add(deviceItem);
					curveDataReq.setSid(ProductSid.SID315.id);
				}
			}
		}
		//用户4
		if (Func.equals(userId, UserGroup.TIETA.id)) {
			//总电流 316
			if (Func.equals(curveType, CurveType.ELECTRICCURRENT.getId())) {
				DeviceItem deviceItem = DeviceItemMap.get(ProductSid.SID316.id);
				if (Func.isNotEmpty(deviceItem)) {
					itemList.add(deviceItem);
					curveDataReq.setSid(ProductSid.SID316.id);
				}
			}
			//用户4电能 309
			if (Func.equals(curveType,CurveType.ELECTRICITY.getId())) {
				DeviceItem deviceItem = DeviceItemMap.get(ProductSid.SID317.id);
				if (Func.isNotEmpty(deviceItem)) {
					itemList.add(deviceItem);
					curveDataReq.setSid(ProductSid.SID317.id);
				}
			}
		}
		//用户5
		if (Func.equals(userId, UserGroup.GUANGDIAN.id)) {
			//总电流 319
			if (Func.equals(curveType, CurveType.ELECTRICCURRENT.getId())) {
				DeviceItem deviceItem = DeviceItemMap.get(ProductSid.SID319.id);
				if (Func.isNotEmpty(deviceItem)) {
					itemList.add(deviceItem);
					curveDataReq.setSid(ProductSid.SID319.id);
				}
			}
			//用户5 当月能耗319
			if (Func.equals(curveType,CurveType.ELECTRICITY.getId())) {
				DeviceItem deviceItem = DeviceItemMap.get(ProductSid.SID321.id);
				if (Func.isNotEmpty(deviceItem)) {
					itemList.add(deviceItem);
					curveDataReq.setSid(ProductSid.SID321.id);
				}
			}
		}

		Map<String, List<CurveDataResq>> res = curveDataRepository.getCurveData(curveDataReq,itemList, HistoryDataType.TOTAL.id);

		return R.data(res);
	}

	@PostMapping("/getCurveDate")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "大屏端曲线图数据", notes = "")
	public R<Map<String, List<CurveDataResq>>> getCurveDate(@RequestBody CurveDataReq curveDataReq) {
		R r = curveDataRepository.checkParam(curveDataReq);
		if (!r.isSuccess()) {
			return r;
		}

		String gwId= curveDataReq.getGwId();
		List<String> gwIds=new ArrayList<>();
		gwIds.add(gwId);
		String userId= curveDataReq.getUserGroup();
		String rtridcb= curveDataReq.getRtuidcb();
		Integer curveType=curveDataReq.getDataCurveType();

		if(Func.isEmpty(rtridcb)) {
			//跟网关查找redis里面的com口
			List<DeviceSub> gwSubList = iDeviceClient.getDeviceSubsByGwid(gwId);
			for (DeviceSub deviceSub : gwSubList) {
				//rtu1为总路 获取电压和电流
				if (Func.equals("1", deviceSub.getRtuid())) {
					rtridcb = deviceSub.getRtuidcb();
				}
			}
		}

		Map<String, DeviceItem> DeviceItemMap =new HashMap<>();
		if(Func.isNotEmpty(rtridcb)){
			List<DeviceItem> gwItemList = iDeviceClient.getDeviceItemInfosByRtuidcb(rtridcb);
			DeviceItemMap=CurveDataFactory.buildItemSidMap(gwItemList);
		}

		List<DeviceItem> itemList=new ArrayList<>();

		//电流 304
		if (Func.equals(curveType, CurveType.ELECTRICCURRENT.getId())) {
			DeviceItem deviceItem = DeviceItemMap.get(ProductSid.SID304.id);
			if (Func.isNotEmpty(deviceItem)) {
				itemList.add(deviceItem);
				curveDataReq.setSid(ProductSid.SID304.id);
			}
		}
		//电能 306
		if (Func.equals(curveType,CurveType.ELECTRICITY.getId())) {
			DeviceItem deviceItem = DeviceItemMap.get(ProductSid.SID306.id);
			if (Func.isNotEmpty(deviceItem)) {
				itemList.add(deviceItem);
				curveDataReq.setSid(ProductSid.SID306.id);
			}
		}
		Map<String, List<CurveDataResq>> res = curveDataRepository.getCurveData(curveDataReq,itemList, HistoryDataType.TOTAL.id);

		return R.data(res);
	}


//	public R<Map<String,Object>> testData() {
//
//		JSONArray heads = new JSONArray();//表头
//		JSONArray datas = new JSONArray();//数据
//		JSONArray foots = new JSONArray();//表尾
//		JSONArray children = new JSONArray();
//
//		JSONObject head = new JSONObject();
//		head.put("label", "用户");
//		JSONObject head_time = new JSONObject();
//		head_time.put("label", "时间");
//		head_time.put("prop", "time");
//		children.add(head_time);
//		head.put("children",children);
//		heads.add(head);
//
//		head = new JSONObject();
//		head.put("label", "移动");
//		children = new JSONArray();
//
//		JSONObject head_port = new JSONObject();
//		head_port.put("label", "端口1");
//		head_port.put("prop", "port1");
//		children.add(head_port);
//
//		head_port = new JSONObject();
//		head_port.put("label", "端口3");
//		head_port.put("prop", "port3");
//		children.add(head_port);
//
//		head.put("children", children);
//		heads.add(head);
//
//
//		head = new JSONObject();
//		head.put("label", "电信");
//		children = new JSONArray();
//
//		head_port = new JSONObject();
//		head_port.put("label", "端口2");
//		head_port.put("prop", "port2");
//		children.add(head_port);
//
//		head_port = new JSONObject();
//		head_port.put("label", "端口4");
//		head_port.put("prop", "port4");
//		children.add(head_port);
//
//		head_port = new JSONObject();
//		head_port.put("label", "端口5");
//		head_port.put("prop", "port5");
//		children.add(head_port);
//
//		head.put("children", children);
//		heads.add(head);
//
//
//
//
//		JSONObject data = new JSONObject();
//		data.put("time","01:00");
//		data.put("port1","5A");
//		data.put("port2","5A");
//		data.put("port3","5A");
//		data.put("port4","5A");
//		data.put("port5","4.5A");
//		datas.add(data);
//		data = new JSONObject();
//		data.put("time","02:00");
//		data.put("port1","5A");
//		data.put("port2","5A");
//		data.put("port3","5A");
//		data.put("port4","5A");
//		data.put("port5","4.5A");
//		datas.add(data);
//
//		data = new JSONObject();
//		data.put("time","合计");
//		data.put("port1","10A");
//		data.put("port2","10A");
//		data.put("port3","10A");
//		data.put("port4","10A");
//		data.put("port5","9A");
//		datas.add(data);
//
//		JSONObject foot = new JSONObject();
//		foot.put("label","合计");
//		foot.put("colspan","1");
//		foots.add(foot);
//
//		foot = new JSONObject();
//		foot.put("label","移动");
//		foot.put("colspan","2");
//		foot.put("value","20A");
//		foots.add(foot);
//
//		foot = new JSONObject();
//		foot.put("label","电信");
//		foot.put("colspan","3");
//		foot.put("value","29A");
//		foots.add(foot);
//
//		System.out.println("heads:"+heads);
//		System.out.println("datas:"+datas);
//		System.out.println("foots:"+foots);
//
//		Map<String,Object> resMap =new HashMap<>();
//		resMap.put("head",heads);
//		resMap.put("body",datas);
//		resMap.put("foot",foots);
//
//		return R.data(resMap);
//	}


	public void getHeadsAndPortUsers(JSONArray heads,LinkedHashSet<PortUser> portUsers,List<RtuSet> rtuSetList){

		JSONArray children = new JSONArray();

		JSONObject head = new JSONObject();
		head.put("label", "用户");
		JSONObject head_time = new JSONObject();
		head_time.put("label", "时间");
		head_time.put("prop", "time");
		children.add(head_time);
		head.put("children",children);
		heads.add(head);
		Set<PortUser> portUsers1=new HashSet<>();
		for(RtuSet rtuSet:rtuSetList){
			if(Func.isNotEmpty(UserGroup.getValue(rtuSet.getUserGroup()))) {
				PortUser portUser =new PortUser();
				portUser.setId(rtuSet.getUserGroup());
				portUser.setValue(UserGroup.getValue(rtuSet.getUserGroup()));
				portUsers1.add(portUser);
			}
		}
		portUsers1.stream().sorted(Comparator.comparing(PortUser::getId).reversed()).forEachOrdered(portUsers::add);
		for (PortUser portUser : portUsers) {
			head = new JSONObject();
			head.put("label", portUser.getValue());
			children = new JSONArray();
			for(RtuSet rtuSet:rtuSetList){
				if(Func.equals(portUser.getId(),rtuSet.getUserGroup())){
					JSONObject head_port = new JSONObject();
					head_port.put("label", "端口"+rtuSet.getPort());
					head_port.put("prop", "port"+rtuSet.getPort());
					children.add(head_port);
				}
			}
			head.put("children", children);
			heads.add(head);

		}
	}

	public void getFoots(JSONArray foots,Set<PortUser> portUsers,List<RtuSetDeviceItem> rtuSetDeviceItemList){
		JSONObject foot = new JSONObject();
		foot.put("label","合计");
		foot.put("colspan","1");
		foots.add(foot);

		for (PortUser portUser : portUsers) {
			foot = new JSONObject();
			foot.put("label", portUser.getValue());

			Float value=0f;
			int colspan=0;
			for (RtuSetDeviceItem entity : rtuSetDeviceItemList) {
				if(Func.equals(portUser.getId(),entity.getUserGroup())){
					value=value+(entity.getSumVal()==null?0f:entity.getSumVal());
					colspan++;
				}

			}
			foot.put("value", BigDecimalUtil.keepTwoDecimals(value,2));
			foot.put("colspan", colspan);
			foots.add(foot);
		}
	}

	@GetMapping("/getPortReport")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "配置端(电流电能)报表", notes = "")
	public R<Map<String,Object>> getPortReport(@ApiParam(value = "网关id", required = true) @RequestParam String gwId,
													  @ApiParam(value = "统计时间类型0:小时报表，2:日报表 ,3:月报表", required = true) @RequestParam Integer dateType,
													  @ApiParam(value = "时间", required = true) @RequestParam Date time,
													  @ApiParam(value = "报表类型2电流，7电能", required = true) @RequestParam int type
	) {

		CurveDataReq curveDataReq = new CurveDataReq();
		curveDataReq.setTime(time);
		curveDataReq.setDateType(dateType);
		CurveDataInfo curveDataInfo= CurveDataFactory.getCurveDataInfo(curveDataReq,false);

		String stime = curveDataInfo.getStime();
		String etime = curveDataInfo.getEtime();
		DeviceItemCycle itemCycle = curveDataInfo.getItemCycle();
		//查询用户端口
		Map<String, Object> querymap = new HashMap<>();
		querymap.put("gwId", gwId);
		List<RtuSet> rtuSetList = iRtuSetService.selectGwcomSetList(querymap);
		if (Func.isEmpty(rtuSetList)) {
			return R.fail("没有端口数据");
		}

		JSONArray heads =new JSONArray();//表头
		LinkedHashSet<PortUser> portUsers=Sets.newLinkedHashSet();
		//Set<PortUser> portUsers =new HashSet<>();//端口用户组
		getHeadsAndPortUsers(heads,portUsers,rtuSetList);

		JSONArray datas = new JSONArray();//数据
		JSONArray foots = new JSONArray();//表尾

		List<String> gwIds = new ArrayList<>();
		gwIds.add(gwId);

		//获取数据项
		Map<String, DeviceItem> deviceItemMap = new HashMap<>();
		List<DeviceItem> gwItemList = iDeviceClient.getDeviceItemInfosByGwids(gwIds);
		if (Func.isEmpty(rtuSetList)) {
			return R.fail("没有数据项");
		}
		//电流
		if(Func.equals(type, CurveType.ELECTRICCURRENT.getId())){
			gwItemList = ReportDataRepository.getElectricityItem(gwItemList);
		}
		//电能
		if(Func.equals(type, CurveType.ELECTRICITY.getId())){
			gwItemList = ReportDataRepository.getPowerItem(gwItemList);
		}
		deviceItemMap = CurveDataFactory.buildItemSidMap(gwItemList);
		//组装用户数据项 key是用户id
		List<RtuSetDeviceItem>  rtuSetDeviceItemList= getRtuSetDeviceItem(rtuSetList, type, deviceItemMap);

		List<Object> timeRows = new ArrayList<>();
		for (RtuSetDeviceItem entity : rtuSetDeviceItemList) {
			DeviceItem deviceItem=entity.getDeviceItem();
			if(Func.isEmpty(deviceItem)){
				continue;
			}
			List<String> items = new ArrayList<>();//查询数据的数据项
			items.add(deviceItem.getId());
			//查询具体数据项数据
			List<DeviceItemHistoryDiffData> deviceItemHistoryDiffDatas = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
				CollectionUtil.join(items, CoreCommonConstant.SPLIT_COMMA_KEY)
				, stime, etime, itemCycle.id, HistoryDataType.TOTAL.id);
			if(Func.isNotEmpty(deviceItemHistoryDiffDatas)) {
				Map<String, List<Object>> valMap = curveDataRepository.groupValueByXY(null,deviceItemHistoryDiffDatas, itemCycle, curveDataInfo.getShowRows());

				//电流
				if(Func.equals(type, CurveType.ELECTRICCURRENT.getId())){
					List<Object> YSubVals = valMap.get(XYDdatas.YVals);
					timeRows = valMap.get(XYDdatas.XVals);
					entity.setXVals(timeRows);
					entity.setYSubVals(YSubVals);
				}
				//电能
				if(Func.equals(type, CurveType.ELECTRICITY.getId())){
					List<Object> YSubVals = valMap.get(XYDdatas.YSubVals);
					Float sumVal=0f;
					for(Object val: YSubVals){
						sumVal=(Func.equals(val, GwsubscribeConstant.ITEM_NULL_VALUE) ? 0f : Float.valueOf(val.toString()))+sumVal;

					}
					timeRows = valMap.get(XYDdatas.XVals);
					entity.setSumVal(BigDecimalUtil.keepTwoDecimals(sumVal,2));
					entity.setXVals(timeRows);
					entity.setYSubVals(YSubVals);
				}



			}
		}
		for(int i=0;i<timeRows.size();i++){
			JSONObject data = new JSONObject();
			data.put("time",timeRows.get(i));
			for (RtuSetDeviceItem entity : rtuSetDeviceItemList) {
				List<Object> YSubVals=entity.getYSubVals();
				if(Func.isNotEmpty(YSubVals)) {
					data.put("port" + entity.getPort(), YSubVals.get(i));
				}
			}

			datas.add(data);
		}

		Map<String,Object> resMap =new HashMap<>();

		//电能
		if(Func.equals(type, CurveType.ELECTRICITY.getId())) {
			JSONObject data = new JSONObject();
			data.put("time", "合计");
			for (RtuSetDeviceItem entity : rtuSetDeviceItemList) {
				data.put("port" + entity.getPort(), entity.getSumVal());
			}
			datas.add(data);
			getFoots(foots,portUsers,rtuSetDeviceItemList);
			resMap.put("foot",foots);
		}

		resMap.put("head",heads);
		resMap.put("body",datas);
		return R.data(resMap);

	}

	/**
	 * 获得端口用户item数据 key itemId
	 * @param rtuSetList
	 * @param type
	 * @param DeviceItemMap
	 * @return
	 */
	public List<RtuSetDeviceItem> getRtuSetDeviceItem(List<RtuSet> rtuSetList,Integer type,Map<String, DeviceItem> DeviceItemMap){
		List<RtuSetDeviceItem> rtuSetDeviceItemList=new ArrayList<>();
		for(RtuSet rtuSet:rtuSetList) {
			RtuSetDeviceItem rtuSetDeviceItem=new RtuSetDeviceItem();
			BeanUtils.copyProperties(rtuSet,rtuSetDeviceItem);
			if (Func.isEmpty(rtuSet.getUserGroup())) {
				continue;
			}
			String userId = rtuSet.getUserGroup();
			//用户1
			if (Func.equals(userId, UserGroup.YIDONG.id)) {
				//用户1总电流 307
				if (Func.equals(type, CurveType.ELECTRICCURRENT.getId())) {
					DeviceItem deviceItem = DeviceItemMap.get(ProductSid.SID307.id);
					if (Func.isNotEmpty(deviceItem)) {
						rtuSetDeviceItem.setDeviceItem(deviceItem);
					}
				}
				//用户1电能 309
				if (Func.equals(type, CurveType.ELECTRICITY.getId())) {
					DeviceItem deviceItem = DeviceItemMap.get(ProductSid.SID309.id);
					if (Func.isNotEmpty(deviceItem)) {
						rtuSetDeviceItem.setDeviceItem(deviceItem);
					}
				}

			}
			//用户2
			if (Func.equals(userId, UserGroup.LIANTONG.id)) {
				//总电流 310
				if (Func.equals(type, CurveType.ELECTRICCURRENT.getId())) {
					DeviceItem deviceItem = DeviceItemMap.get(ProductSid.SID310.id);
					if (Func.isNotEmpty(deviceItem)) {
						rtuSetDeviceItem.setDeviceItem(deviceItem);
					}
				}
				//用户2电能 312
				if (Func.equals(type, CurveType.ELECTRICITY.getId())) {
					DeviceItem deviceItem = DeviceItemMap.get(ProductSid.SID312.id);
					if (Func.isNotEmpty(deviceItem)) {
						rtuSetDeviceItem.setDeviceItem(deviceItem);
					}
				}
			}

			//用户3
			if (Func.equals(userId, UserGroup.DIANXIN.id)) {
				//总电流 313
				if (Func.equals(type, CurveType.ELECTRICCURRENT.getId())) {
					DeviceItem deviceItem = DeviceItemMap.get(ProductSid.SID313.id);
					if (Func.isNotEmpty(deviceItem)) {
						rtuSetDeviceItem.setDeviceItem(deviceItem);
					}
				}
				//用户3电能 315
				if (Func.equals(type, CurveType.ELECTRICITY.getId())) {
					DeviceItem deviceItem = DeviceItemMap.get(ProductSid.SID315.id);
					if (Func.isNotEmpty(deviceItem)) {
						rtuSetDeviceItem.setDeviceItem(deviceItem);
					}
				}
			}
			//用户4
			if (Func.equals(userId, UserGroup.TIETA.id)) {
				//总电流 316
				if (Func.equals(type, CurveType.ELECTRICCURRENT.getId())) {
					DeviceItem deviceItem = DeviceItemMap.get(ProductSid.SID316.id);
					if (Func.isNotEmpty(deviceItem)) {
						rtuSetDeviceItem.setDeviceItem(deviceItem);
					}
				}
				//用户4电能 309
				if (Func.equals(type, CurveType.ELECTRICITY.getId())) {
					DeviceItem deviceItem = DeviceItemMap.get(ProductSid.SID317.id);
					if (Func.isNotEmpty(deviceItem)) {
						rtuSetDeviceItem.setDeviceItem(deviceItem);
					}
				}
			}
			//用户5
			if (Func.equals(userId, UserGroup.GUANGDIAN.id)) {
				//总电流 319
				if (Func.equals(type, CurveType.ELECTRICCURRENT.getId())) {
					DeviceItem deviceItem = DeviceItemMap.get(ProductSid.SID319.id);
					if (Func.isNotEmpty(deviceItem)) {
						rtuSetDeviceItem.setDeviceItem(deviceItem);
					}
				}
				//用户5 当月能耗319
				if (Func.equals(type, CurveType.ELECTRICITY.getId())) {
					DeviceItem deviceItem = DeviceItemMap.get(ProductSid.SID321.id);
					if (Func.isNotEmpty(deviceItem)) {
						rtuSetDeviceItem.setDeviceItem(deviceItem);
					}
				}
			}
			rtuSetDeviceItemList.add(rtuSetDeviceItem);
		}
		return rtuSetDeviceItemList;
	}

}
