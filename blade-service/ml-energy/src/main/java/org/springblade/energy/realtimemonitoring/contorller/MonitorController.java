package org.springblade.energy.realtimemonitoring.contorller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.bean.DeviceItem;
import org.springblade.constants.CoreCommonConstant;
import org.springblade.constants.GwsubscribeConstant;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.DigestUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.dto.DiagramTypeDto;
import org.springblade.dto.NameValue;
import org.springblade.energy.diagram.entity.Diagram;
import org.springblade.energy.diagram.entity.DiagramItem;
import org.springblade.energy.diagram.service.IDiagramItemService;
import org.springblade.energy.diagram.service.IDiagramService;
import org.springblade.enums.DiagramType;
import org.springblade.enums.ItemStype;
import org.springblade.gw.feign.IDataItemClient;
import org.springblade.gw.feign.IDeviceClient;
import org.springblade.system.user.entity.User;
import org.springblade.system.user.feign.IUserClient;
import org.springblade.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author bond
 * @date 2020/6/9 11:33
 * @desc 实时监测 辅助类
 */
@RestController
@AllArgsConstructor
@RequestMapping("/monitor")
@Api(value = "实时监测", tags = "实时监测辅助接口")
public class MonitorController {
	@Autowired
	private IUserClient userClient;
	private IDataItemClient iDataItemClient;
	private IDiagramService iDiagramService;
	private IDeviceClient  iDeviceClient;
	@Autowired
	private IDiagramItemService iDiagramItemService;


	@GetMapping("/getDiagramType")
	@ApiOperationSupport(order = 0)
	@ApiOperation(value = "根据站点ID，位置ID 查询系统图类别", notes = "传入id")
	public R<Set<DiagramTypeDto>> getDiagram(@ApiParam(value = "stationId", required = true) @RequestParam Long stationId,
											  @ApiParam(value = "siteId", required = true) @RequestParam Long siteId) {
		Map<String,Object> map =new HashMap<>();
		map.put("stationId",stationId);
		map.put("siteId",siteId);
		List<Diagram> list=iDiagramService.selectDiagramByMap(map);
		Set<DiagramTypeDto> resList= new HashSet<>();
		for(Diagram diagram :list){
			DiagramTypeDto diagramTypeDto=new DiagramTypeDto();
			diagramTypeDto.setDiagramType(diagram.getDiagramType());
			diagramTypeDto.setName(DiagramType.getValue(diagram.getDiagramType()));
			resList.add(diagramTypeDto);
		}
		return R.data(resList);

	}




	public List<NameValue<Integer>> getYaokongEnumList(String yaokongItemId) {
		DeviceItem yaokongItem = iDeviceClient.getDeviceItemInfosByItemid(yaokongItemId);
		String ykDesc = StringUtils.isNull(yaokongItem)?"":yaokongItem.getDesc();
		String splitStr = CoreCommonConstant.SPLIT_SEMICOLON_KEY;
		if(ykDesc.indexOf(CoreCommonConstant.SPLIT_COMMA_KEY)!=-1){
			splitStr = CoreCommonConstant.SPLIT_COMMA_KEY;
		}
		String[] allType = ykDesc.split(splitStr);
		List<NameValue<Integer>> data = new ArrayList<>();

		for(int i = 0; i<allType.length; i++) {
			NameValue<Integer> nameValue = new NameValue<Integer>();
			String[] type = allType[i].split(GwsubscribeConstant.SPLIT_COLON_KEY);
			if(type.length == 2) {
				nameValue.setValue(Integer.valueOf(type[0]));
				nameValue.setName(type[1]);
			}else if(type.length > 2) {
				nameValue.setName(type[3]);
				nameValue.setValue(Integer.valueOf(type[1]));
			}
			data.add(nameValue);
		}
		return data;
	}
	//
	// 验证遥控测试，用工具订阅这两个主题，mailian/setting/control/{gwid}
	// mailian/ctrlack
	@ApiOperation(value = "获取遥控选项", httpMethod = "GET")
	@RequestMapping(value = "/getYkSelect",method = RequestMethod.GET)
	public R<List<NameValue<Integer>>> getYkSelect(@ApiParam(value = "遥控数据项") @RequestParam(value = "itemId") String itemId) {
		return R.data(getYaokongEnumList(itemId));
	}

	@ApiOperation(value = "获取遥控数据项列表", httpMethod = "GET")
	@RequestMapping(value = "/getYkItems",method = RequestMethod.GET)
	public R<List<DiagramItem>> getYkItems(@ApiParam(value = "pindex") @RequestParam(value = "pindex") String pindex) {
		Map<String,Object> map =new HashMap<>();
		map.put("pindex",pindex);
		map.put("ftype", ItemStype.TRANSPORTYK.id);
		List<DiagramItem> list=iDiagramItemService.selectDiagramItemByMap(map);
		return R.data(list);
	}

	@ApiLog("实时监测 控密码验证")
	@ApiOperation(value = "遥控密码验证", httpMethod = "GET")
	@GetMapping(value = "/getVerificationResByPassword")
	@ApiOperationSupport(order = 1)
	public R getVerificationResByPassword(@ApiParam(value = "密码",required = true)@RequestParam(value = "password") String password)
	{
		Long userId=AuthUtil.getUser().getUserId();
		R<User> dat=userClient.userInfoById(userId);
		User user=dat.getData();
		if(Func.isNotEmpty(user)){

			String upassword=user.getPassword();
			String md5Password =DigestUtil.encrypt(password);
			if(Func.equals(upassword,md5Password)){
				return R.success("验证通过");
			}
		}
		return R.fail("密码不正确");
	}


	@ApiLog("系统图 设置遥控值")
	@GetMapping("/setYk")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "设置遥控值", notes = "传入List")
	public R setYk(@ApiParam(value = "数据项id",required = true)@RequestParam(value = "itemId") String itemId,
				   @ApiParam(value = "遥控值",required = true)@RequestParam(value = "val") Integer val) {
		Boolean updateRes = iDataItemClient.setYk(itemId,val);
		return R.data(updateRes);
	}


}
