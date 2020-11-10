package org.springblade.pms.station.controller;

import org.springblade.bean.DeviceItem;
import org.springblade.bean.SetYt;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.enums.ProductSid;
import org.springblade.pms.enums.TimeContorlEnu;
import org.springblade.pms.gw.feign.IDataItemClient;
import org.springblade.pms.gw.feign.IDeviceClient;
import org.springblade.pms.station.entity.RtuSet;
import org.springblade.pms.station.entity.TimeControl;
import org.springblade.pms.station.service.IRtuSetService;
import org.springblade.pms.station.service.ITimeControlService;
import org.springblade.util.BigDecimalUtil;
import org.springblade.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.*;

/**
 * @author bond
 * @date 2020/6/9 11:33
 * @desc
 */
@Component
public class YaoKControllerFactory {
	@Autowired
	private IDataItemClient iDataItemClient;
	@Autowired
	private IRtuSetService iGwcomSetService;
	@Autowired
	private IDeviceClient iDeviceClient;
	@Autowired
	private ITimeControlService iTimeControlService;

	//端口定时开关启用关闭接口
	public R updateTimeContorStatus(String id,int timeContorStatus) {
		RtuSet rtu=iGwcomSetService.getById(id);
		if(Func.isEmpty(rtu)){
			return R.fail("id不存在");
		}
		rtu.setTimeContorStatus(timeContorStatus);
		boolean r=iGwcomSetService.updateById(rtu);

		List<DeviceItem> gwItemList = iDeviceClient.getDeviceItemInfosByRtuidcb(rtu.getRtuidcb());
		for (DeviceItem ditem : gwItemList) {
			//定时控制使能//摇调
			if (Func.equals(String.valueOf(ditem.getSid()), ProductSid.SID1528.id)) {
				iDataItemClient.setYk(ditem.getId(), timeContorStatus);
			}
		}
		return R.status(r);
	}


	public boolean updateYKYXYCYT(List<RtuSet> rtuSets){

		List<SetYt> setYtList=new ArrayList<>();
		SetYt setYt=null;
		for(RtuSet rtu:rtuSets){
			//根据rtuidcb查找数据项
			List<DeviceItem> gwItemList = iDeviceClient.getDeviceItemInfosByRtuidcb(rtu.getRtuidcb());
			for (DeviceItem ditem : gwItemList) {

				//分配用户//遥调
				if (Func.equals(String.valueOf(ditem.getSid()), ProductSid.SID1521.id)) {
					//iDataItemClient.setYk(ditem.getId(),Integer.valueOf(rtu.getUserGroup()));
					setYt=new SetYt();
					setYt.setItemid(ditem.getId());
					setYt.setVal(Float.valueOf(rtu.getUserGroup()));
					setYtList.add(setYt);
				}
				//判断开关//摇控
				if (Func.equals(String.valueOf(ditem.getSid()), ProductSid.SID2003.id)) {
					iDataItemClient.setYk(ditem.getId(), rtu.getStatus());
				}
				//判断电流预警//遥调
				if (Func.equals(String.valueOf(ditem.getSid()), ProductSid.SID1524.id)) {
					if(Func.isNotEmpty(rtu.getRatedI()) && Func.isNotEmpty(rtu.getWarnI()) ) {
						Float val = BigDecimalUtil.divF(rtu.getRatedI()*rtu.getWarnI(),100f);
						//iDataItemClient.setYk(ditem.getId(),val.intValue());
						setYt=new SetYt();
						setYt.setItemid(ditem.getId());
						setYt.setVal(val);
						setYtList.add(setYt);
					}
				}
				//判断电流过高//摇调
				if (Func.equals(String.valueOf(ditem.getSid()), ProductSid.SID1522.id)) {
					if(Func.isNotEmpty(rtu.getRatedI()) && Func.isNotEmpty(rtu.getAlarmI()) ) {
						Float val = BigDecimalUtil.divF(rtu.getRatedI()*rtu.getAlarmI(),100f);
						//iDataItemClient.setYk(ditem.getId(),val.intValue());
						setYt=new SetYt();
						setYt.setItemid(ditem.getId());
						setYt.setVal(val);
						setYtList.add(setYt);
					}
				}
				//下电时长//摇调
				if (Func.equals(String.valueOf(ditem.getSid()), ProductSid.SID1526.id)) {
						//iDataItemClient.setYk(ditem.getId(), rtu.getDischargeTimes());
					setYt=new SetYt();
					setYt.setItemid(ditem.getId());
					setYt.setVal(rtu.getDischargeTimes());
					setYtList.add(setYt);
				}
				//下电电压//摇调
				if (Func.equals(String.valueOf(ditem.getSid()), ProductSid.SID1527.id)) {
					//iDataItemClient.setYk(ditem.getId(), rtu.getDischargeVoltage());
					setYt=new SetYt();
					setYt.setItemid(ditem.getId());
					setYt.setVal(rtu.getDischargeVoltage());
					setYtList.add(setYt);
				}
			}
		}
		iDataItemClient.setYt(setYtList);

		return true;
	}


	//@ApiOperation(value = "提交端口定时控制", notes = "")
	public R submitTimeControl(@Valid @RequestBody List<TimeControl> timeControls) {
		if(Func.isEmpty(timeControls)){
			return R.fail("传输数据不能为空");
		}
		for(TimeControl timeControl:timeControls){
			if(Func.isEmpty(timeControl)){
				return R.fail("传输数据不能为空");
			}
			if(Func.isEmpty(timeControl.getRtuSetId())){
				return R.fail("传输数据rtuSetId不能为空");
			}
		}
		boolean r =iTimeControlService.saveOrUpdateBatch(timeControls);
		if(r){
			updateDingshi(timeControls);
		}

		return R.status(r);
	}

	/**
	 * 发送定时开关
	 * @param timeControls
	 */
	@Async
	public void updateDingshi(List<TimeControl> timeControls){
		List<SetYt> setYtList=new ArrayList<>();
		SetYt setYt=new SetYt();
		for(TimeControl timeControl:timeControls){
			RtuSet rtu=iGwcomSetService.getById(timeControl.getRtuSetId());
			//根据rtuidcb查找数据项
			List<DeviceItem> gwItemList = iDeviceClient.getDeviceItemInfosByRtuidcb(rtu.getRtuidcb());
			Map<Integer, DeviceItem> DeviceItemMap=buildMap(gwItemList);

			DeviceItem ONOFFItem=null;
			DeviceItem TIMEItem=null;
			if (Func.equals(timeControl.getGrade(), TimeContorlEnu.ONOFF1.id)) {
				 ONOFFItem=DeviceItemMap.get(Integer.valueOf(TimeContorlEnu.ONOFF1.statusSid));
				 TIMEItem=DeviceItemMap.get(Integer.valueOf(TimeContorlEnu.ONOFF1.timeSid));
			}
			if (Func.equals(timeControl.getGrade(), TimeContorlEnu.ONOFF2.id)) {
				 ONOFFItem=DeviceItemMap.get(Integer.valueOf(TimeContorlEnu.ONOFF2.statusSid));
				 TIMEItem=DeviceItemMap.get(Integer.valueOf(TimeContorlEnu.ONOFF2.timeSid));
			}
			if (Func.equals(timeControl.getGrade(), TimeContorlEnu.ONOFF3.id)) {
				ONOFFItem=DeviceItemMap.get(Integer.valueOf(TimeContorlEnu.ONOFF3.statusSid));
				TIMEItem=DeviceItemMap.get(Integer.valueOf(TimeContorlEnu.ONOFF3.timeSid));
			}

			if (Func.equals(timeControl.getGrade(), TimeContorlEnu.ONOFF4.id)) {
				ONOFFItem=DeviceItemMap.get(Integer.valueOf(TimeContorlEnu.ONOFF4.statusSid));
				TIMEItem=DeviceItemMap.get(Integer.valueOf(TimeContorlEnu.ONOFF4.timeSid));
			}
			if (Func.equals(timeControl.getGrade(), TimeContorlEnu.ONOFF5.id)) {
				ONOFFItem=DeviceItemMap.get(Integer.valueOf(TimeContorlEnu.ONOFF5.statusSid));
				TIMEItem=DeviceItemMap.get(Integer.valueOf(TimeContorlEnu.ONOFF5.timeSid));
			}
			if (Func.equals(timeControl.getGrade(), TimeContorlEnu.ONOFF6.id)) {
				ONOFFItem=DeviceItemMap.get(Integer.valueOf(TimeContorlEnu.ONOFF6.statusSid));
				TIMEItem=DeviceItemMap.get(Integer.valueOf(TimeContorlEnu.ONOFF6.timeSid));
			}
			String item="";
			Float val=null;
			if(Func.isNotEmpty(ONOFFItem)){
				if(Func.equals(timeControl.getStatus(),0)){
					//iDataItemClient.setYk(ONOFFItem.getId(), 2);
					item=ONOFFItem.getId();
					val=2f;
					setYt.setItemid(item);
					setYt.setVal(val);
					setYtList.add(setYt);
				}else {
					//iDataItemClient.setYk(ONOFFItem.getId(), Integer.valueOf(timeControl.getOnOff()));
					item=ONOFFItem.getId();
					val=Float.valueOf(timeControl.getOnOff());
					setYt.setItemid(item);
					setYt.setVal(val);
					setYtList.add(setYt);
				}
			}
			if(Func.isNotEmpty(TIMEItem) && Func.isNotEmpty(timeControl.getTime())){
				String time=timeControl.getTime()+":00";
				int times= DateUtil.getminutes(DateUtil.strToDate(time,DateUtil.DATE_FORMAT_FOR_HHmmss) );
				//iDataItemClient.setYk(TIMEItem.getId(),times);
				item=TIMEItem.getId();
				val=Float.valueOf(String.valueOf(times));
				setYt.setItemid(item);
				setYt.setVal(val);
				setYtList.add(setYt);
			}

		}
		iDataItemClient.setYt(setYtList);
	}


	public Map<Integer, DeviceItem> buildMap(List<DeviceItem> deviceItems) {
		if (deviceItems.isEmpty()) {
			return null;
		} else {
			Map<Integer, DeviceItem> idItemMap = new HashMap(deviceItems.size());
			Iterator var3 = deviceItems.iterator();

			while(var3.hasNext()) {
				DeviceItem item = (DeviceItem)var3.next();
				idItemMap.put(item.getSid(), item);
			}

			return idItemMap;
		}
	}
}
