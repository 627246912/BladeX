package org.springblade.gw.testController;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springblade.bean.Device;
import org.springblade.bean.DeviceItem;
import org.springblade.bean.DevieceJFPGDate;
import org.springblade.constants.CoreCommonConstant;
import org.springblade.core.tool.api.R;
import org.springblade.gw.config.PushConfig;
import org.springblade.gw.restTemplate.DeviceItemRepository;
import org.springblade.util.PushResponseUtil;
import org.springblade.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：bond
 * @date ：Created in 2020/3/19 10:08
 * @description：网关操作类
 * @modified By：
 * @version: 1.0.0$
 */
@RestController
@AllArgsConstructor
@RequestMapping("/tool")
@Api(value = "网关工具类", tags = "网关数据接口")
public class DeviceController {

	@Autowired
	private DeviceItemRepository deviceItemRepository;
	@Autowired
	private PushConfig pushConfig;
	@Autowired
	private RestTemplate restTemplate;






	@PostMapping("/getItemInfosByItemid")
	@ApiOperationSupport(order = 4)
	public R<DeviceItem> getItemInfosByItemid(String itemid) {
		DeviceItem ta=deviceItemRepository.getItemInfosByItemid(itemid);
		return R.data(ta);
	}
	/**
	 * 根据通讯机编号获取通讯机
	 * @param codes
	 * @return
	 */
	@RequestMapping("/getDevicesByCodes")
	public List<Device> getDevicesByCodes(String baseUrl, List<String> codes){
		return getDevicesByCodes(codes,baseUrl,pushConfig.SYSTEM_ID);
	}
	/**
	 * 根据通讯机编号及运营商id获取通讯机
	 * @param codes
	 * @param baseUrl 接口地址
	 * @param appid 运营商id
	 * @return
	 */
	@RequestMapping("/getDevicesByCodesAndAppId")
	public List<Device> getDevicesByCodes(List<String> codes,String baseUrl,String appid){
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("appid", appid);
		String deviceUrl = pushConfig.getUrl(baseUrl,pushConfig.GET_DEVICE_URI)+"?appid={appid}";
		if(StringUtils.isNotEmpty(codes)) {
			params.put("gwids", CollectionUtil.join(codes, CoreCommonConstant.SPLIT_COMMA_KEY));
			deviceUrl += "&gwids={gwids}";
		}
		String res = restTemplate.getForEntity(deviceUrl,String.class,params).getBody();
		return PushResponseUtil.processResponseListData(res, Device.class);
	}

	/**
	 * 设置通信机峰平谷时间段
	 * @param jfpgDates
	 * @return
	 */
	@Async
	@RequestMapping("/setjfpgDate")
	public Boolean setjfpgDate(String baseUrl, List<DevieceJFPGDate> jfpgDates) {
		final Map<String,Object> params = new HashMap<String,Object>(2);
		params.put("appid", pushConfig.SYSTEM_ID);
		params.put("paras", jfpgDates);

		String data = JSON.toJSONString(params);
		String res = restTemplate.postForEntity(pushConfig.getUrl(baseUrl,pushConfig.SET_JFPGDATE_URI),data, String.class).getBody();
		return PushResponseUtil.processSuccess(res);
	}

}
