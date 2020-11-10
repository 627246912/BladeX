package org.springblade.pms.station.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.bean.DeviceItem;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.secure.annotation.PreAuth;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.constant.RoleConstant;
import org.springblade.core.tool.utils.DigestUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.enums.ProductSid;
import org.springblade.pms.gw.feign.IDataItemClient;
import org.springblade.pms.gw.feign.IDeviceClient;
import org.springblade.pms.station.dto.RtuSetResp;
import org.springblade.pms.station.entity.RtuSet;
import org.springblade.pms.station.entity.TimeControl;
import org.springblade.pms.station.service.IRtuSetService;
import org.springblade.pms.station.service.ITimeControlService;
import org.springblade.system.user.entity.User;
import org.springblade.system.user.feign.IUserClient;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author bond
 * @date 2020/6/9 11:33
 * @desc 实时监测 辅助类
 */
@RestController
@AllArgsConstructor
@RequestMapping("/control")
@Api(value = "遥控接口", tags = "遥控接口")
public class YaoKController extends BladeController {
	private IDataItemClient iDataItemClient;
	private IUserClient userClient;
	private IRtuSetService iGwcomSetService;
	private ITimeControlService iTimeControlService;
	private IDeviceClient iDeviceClient;
	private YaoKControllerFactory yaoKControllerFactory;
//	public List<NameValue<Integer>> getYaokongEnumList(String yaokongItemId) {
//		DeviceItem yaokongItem = iDeviceClient.getDeviceItemInfosByItemid(yaokongItemId);
//		String ykDesc = StringUtils.isNull(yaokongItem)?"":yaokongItem.getDesc();
//		String splitStr = CoreCommonConstant.SPLIT_SEMICOLON_KEY;
//		if(ykDesc.indexOf(CoreCommonConstant.SPLIT_COMMA_KEY)!=-1){
//			splitStr = CoreCommonConstant.SPLIT_COMMA_KEY;
//		}
//		String[] allType = ykDesc.split(splitStr);
//		List<NameValue<Integer>> data = new ArrayList<>();
//
//		for(int i = 0; i<allType.length; i++) {
//			NameValue<Integer> nameValue = new NameValue<Integer>();
//			String[] type = allType[i].split(GwsubscribeConstant.SPLIT_COLON_KEY);
//			if(type.length == 2) {
//				nameValue.setValue(Integer.valueOf(type[0]));
//				nameValue.setName(type[1]);
//			}else if(type.length > 2) {
//				nameValue.setName(type[3]);
//				nameValue.setValue(Integer.valueOf(type[1]));
//			}
//			data.add(nameValue);
//		}
//		return data;
//	}
//
//	@ApiOperation(value = "获取遥控选项", httpMethod = "GET")
//	@RequestMapping(value = "/getYkSelect",method = RequestMethod.GET)
//	public R<List<NameValue<Integer>>> getYkSelect(@ApiParam(value = "遥控数据项") @RequestParam(value = "itemId") String itemId) {
//		return R.data(getYaokongEnumList(itemId));
//	}
//
//	@ApiOperation(value = "遥控密码验证", httpMethod = "GET")
//	@GetMapping(value = "/getVerificationResByPassword")
//	@ApiOperationSupport(order = 1)
//	public R getVerificationResByPassword(@ApiParam(value = "密码",required = true)@RequestParam(value = "password") String password)
//	{
//		Long userId=AuthUtil.getUser().getUserId();
//		R<User> dat=userClient.userInfoById(userId);
//		User user=dat.getData();
//		if(Func.isNotEmpty(user)){
//
//			String upassword=user.getPassword();
//			String md5Password =DigestUtil.encrypt(password);
//			if(Func.equals(upassword,md5Password)){
//				return R.success("验证通过");
//			}
//		}
//		return R.fail("密码不正确");
//	}
	@ApiLog("遥控密码验证")
	@ApiOperation(value = "遥控密码验证", httpMethod = "GET")
	@GetMapping(value = "/getVerificationResByPassword")
	@ApiOperationSupport(order = 1)
	@PreAuth(RoleConstant.HAS_ROLE_ADMIN)//接口权限控制
	public R getVerificationResByPassword(@ApiParam(value = "密码",required = true)@RequestParam(value = "password") String password)
	{
		Long userId= AuthUtil.getUser().getUserId();
		R<User> dat=userClient.userInfoById(userId);
		User user=dat.getData();
		if(Func.isNotEmpty(user)){

			String upassword=user.getPassword();
			String md5Password = DigestUtil.encrypt(password);
			if(Func.equals(upassword,md5Password)){
				return R.success("验证通过");
			}
		}
		return R.fail("密码不正确");
	}

	@ApiLog("大屏端遥控摇调（开关设置/下电）设置")
	@PostMapping("/setYk/pms/{rtuidcb}")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "大屏端遥控摇调（开关设置/下电）设置", notes = "传入List")
	@PreAuth(RoleConstant.HAS_ROLE_ADMIN)//接口权限控制
	public R setYk(@ApiParam(value = "数据项id",required = true)@RequestParam(value = "itemId") String itemId,
				   @ApiParam(value = "遥控值",required = true)@RequestParam(value = "val") Integer val) {
		Boolean updateRes = iDataItemClient.setYk(itemId,val);
		if(updateRes){
			DeviceItem deviceItem=iDeviceClient.getDeviceItemInfosByItemid(itemId);
			if(Func.isNotEmpty(deviceItem)){
				RtuSet rtuSet=new RtuSet();
				rtuSet.setRtuidcb(deviceItem.getRtuidcb());
				RtuSet rtu=iGwcomSetService.getOne(Condition.getQueryWrapper(rtuSet));
				Integer sid= deviceItem.getSid();
				//判断开关//摇控
				if (Func.equals(String.valueOf(sid), ProductSid.SID2003.id)) {
					if(Func.isNotEmpty(rtu)){
						rtu.setSwitchStatus(val);
						iGwcomSetService.updateById(rtu);
					}
				}
				//下电时长//摇调
				if (Func.equals(String.valueOf(sid), ProductSid.SID1526.id)) {
					if(Func.isNotEmpty(rtu)){
						rtu.setDischargeTimes(val);
						iGwcomSetService.updateById(rtu);
					}
				}
				//下电电压//摇调
				if (Func.equals(String.valueOf(sid), ProductSid.SID1527.id)) {
					if(Func.isNotEmpty(rtu)){
						rtu.setDischargeVoltage(val);
						iGwcomSetService.updateById(rtu);
					}
				}
			}
		}
		return R.data(updateRes);
	}

	@GetMapping("/getPortInfo")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "查询端口定时控制信息", notes = "")
	public R<RtuSetResp> getPortInfo(@ApiParam(value = "rtuidcb",required = true)@RequestParam(value = "rtuidcb") String rtuidcb) {
		RtuSet rtuSet =new RtuSet();
		rtuSet.setRtuidcb(rtuidcb);
		RtuSet rtuSetEntity=iGwcomSetService.getOne(Condition.getQueryWrapper(rtuSet));
		RtuSetResp rtuSetResp =new RtuSetResp();
		if(Func.isNotEmpty(rtuSetEntity)){
			BeanUtils.copyProperties(rtuSetEntity,rtuSetResp);
			TimeControl entity = new TimeControl();
			entity.setRtuSetId(rtuSetEntity.getId());
			List<TimeControl> timeControlList = iTimeControlService.list(Condition.getQueryWrapper(entity));
			rtuSetResp.setTimeControlList(timeControlList);
		}

		return R.data(rtuSetResp);
	}

	@ApiLog("大屏端设置定时开关启用")
	@PostMapping("/updateTimeContorStatus/pms/{rtuidcb}")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "端口定时开关启用关闭接口", notes = "")
	@PreAuth(RoleConstant.HAS_ROLE_ADMIN)//接口权限控制
	public R updateTimeContorStatus(@RequestParam(value = "id", required = true)String id,@RequestParam(value = "timeContorStatus", required = true)int timeContorStatus) {
		return yaoKControllerFactory.updateTimeContorStatus(id,timeContorStatus);
	}
	@ApiLog("大屏端设置遥控定时控制")
	@PostMapping("/submitTimeControl/pms/{rtuidcb}")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "提交端口定时控制", notes = "")
	@PreAuth(RoleConstant.HAS_ROLE_ADMIN)//接口权限控制
	public R submitTimeControl(@Valid @RequestBody List<TimeControl> timeControls) {
		return yaoKControllerFactory.submitTimeControl(timeControls);
	}

//
//
//	@PostMapping("/testSetYt")
//	public R testSetYt(@Valid @RequestBody List<SetYt> setYts) {
//		return R.status(iDataItemClient.setYt(setYts));
//	}


}
