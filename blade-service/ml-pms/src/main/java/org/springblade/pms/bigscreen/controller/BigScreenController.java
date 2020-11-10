package org.springblade.pms.bigscreen.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.constants.CoreCommonConstant;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.dto.NameValue;
import org.springblade.pms.alarmmanagement.service.IEquipmentAlarmService;
import org.springblade.pms.enums.AreaRank;
import org.springblade.pms.station.dto.AreaResp;
import org.springblade.pms.station.dto.BaseStationDTO;
import org.springblade.pms.station.entity.BaseStation;
import org.springblade.pms.station.entity.SysArea;
import org.springblade.pms.station.service.IBaseStationService;
import org.springblade.pms.station.service.IRtuSetService;
import org.springblade.pms.station.service.ISysAreaService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author bond
 * @date 2020/8/3 10:12
 * @desc
 */
@RestController
@AllArgsConstructor
@RequestMapping("/bigScreen")
@Api(value = "大屏端", tags = "大屏端接口")
public class BigScreenController {
	@Autowired
	private BladeRedisCache redisCache;
	private IBaseStationService iBaseStationService;
	private ISysAreaService iSysAreaService;
	private IEquipmentAlarmService iEquipmentAlarmService;

	private IRtuSetService iGwcomSetService ;

	@GetMapping("/getAreaStationTree")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "根据区域编码获取区域树", notes = "传入areaCode")
	public R<List<AreaResp>> getAreaStationTree(@RequestParam(value = "areaCode", required = false) String areaCode)
	{
		List<AreaResp> list=iBaseStationService.getAreaStationTree(areaCode);
		return R.data(list);
	}

	@GetMapping("/getAreaStationData")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "根据区域编码获取区分布数据", notes = "传入areaCode")
	public R<Map<String,Object>> getAreaStationData( @RequestParam(value = "areaCode", required = false) String areaCode)
	{
		Map<String,Object> resMap=new HashMap<>();
		//站点列表
		List<BaseStation> stationList = iBaseStationService.selectStationsByAreaCode(areaCode);

		if(Func.isEmpty(stationList)){
			return R.fail("没有站点");
		}

		List<BaseStationDTO> stationDTOList=new ArrayList<>();
		for(BaseStation station: stationList){
			BaseStationDTO baseStationDTO=new BaseStationDTO();
			BeanUtils.copyProperties(station,baseStationDTO);

			Set<String> gwIds =new HashSet<>();
			gwIds.add(station.getGwId());
			List<NameValue> userlist=iGwcomSetService.selectStationUserGroupCount(gwIds);
			List<String> userIds=new ArrayList<>();
			for(NameValue nameValue:userlist){
				userIds.add(nameValue.getCode());
			}
			baseStationDTO.setUserGroup( CollectionUtil.join(userIds, CoreCommonConstant.SPLIT_COMMA_KEY));
			stationDTOList.add(baseStationDTO);
		}
		resMap.put("stationList",stationDTOList);
		Integer areaRank=0;
		if(Func.isEmpty(areaCode)){
			areaCode="0";
		}else{
			SysArea sysArea= iSysAreaService.getSysArea(areaCode);
			//如果为空则是站点
			if(Func.isEmpty(sysArea)){
				areaRank=3;
			}else{
				areaRank=sysArea.getAreaRank();
			}

		}

		//区域列表
		List<SysArea> sysAreaList=new ArrayList<>();

		//站点分布
		List<NameValue> zhandianquyufenbu= new ArrayList<>();

		if(!Func.equals(AreaRank.AREA.id,areaRank)){
			sysAreaList= iBaseStationService.getChildAreaStationList(areaCode);
			Set<String> areaCodes=new HashSet<>();
			for(SysArea area:sysAreaList){
				areaCodes.add(area.getAreaCode());
			}
			zhandianquyufenbu= iBaseStationService.getDistribution(areaCodes);
		}
		//如果code是区县时，站点分布变成监测路
		if(Func.equals(AreaRank.AREA.id,areaRank)){
			Set<String> gwIds= new HashSet<>();
			for (BaseStation station : stationList) {
				gwIds.add(station.getGwId());
			}
			//监测分路
			zhandianquyufenbu= iGwcomSetService.selectGwcomUserGroupCount(gwIds);
		}
		resMap.put("childAreaList",sysAreaList);
		resMap.put("zhandianquyufenbu",zhandianquyufenbu);


		return R.data(resMap);
	}

}
