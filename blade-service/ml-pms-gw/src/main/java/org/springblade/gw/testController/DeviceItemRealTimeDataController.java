package org.springblade.gw.testController;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springblade.bean.DeviceItemRealTimeData;
import org.springblade.gw.repository.DeviceItemDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author ：bond
 * @date ：Created in 2020/3/10 10:08
 * @description：实时数据操作类
 * @modified By：
 * @version: 1.0.0$
 */
@RestController
@AllArgsConstructor
@RequestMapping("/itemrd")
@Api(value = "实时数据", tags = "实时数据接口")
public class DeviceItemRealTimeDataController {
	@Autowired
    private DeviceItemDataRepository deviceItemRealTimeDataRepository;


	/**
	 * 根据数据项id获取实时数据
	 * @param itemId
	 * @return
	 */
	@GetMapping("/getRtDataByItemId")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "据数据项id获取实时数据", notes = "传入itemId")
	public R<DeviceItemRealTimeData> getRtDataByItemId(String itemId) {
		return R.data(deviceItemRealTimeDataRepository.getRtDataByItemId(itemId));
	}
	/**
	 * 根据数据项id列表获取实时数据
	 * @param itemIds
	 * @return
	 */
	@GetMapping("/getRtDataByItemIds")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "根据数据项id列表获取实时数据", notes = "传入itemIds")
	public R<List<DeviceItemRealTimeData>> getRtDataByItemIds(List<String> itemIds){
		return R.data(deviceItemRealTimeDataRepository.getRtDataByItemIds(itemIds));
	}
	/**
	 * 根据数据项id列表获取实时数据
	 * @param itemIds
	 * @return
	 */
	@GetMapping("/getRtDataMapByItemIds")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "根据数据项id列表获取实时数据", notes = "传入itemIds")
	public R<Map<String,DeviceItemRealTimeData>> getRtDataMapByItemIds(List<String> itemIds){
		return R.data(deviceItemRealTimeDataRepository.getRtDataMapByItemIds(itemIds));
	}
    /**
     * 更新实时数据
     * @param rtData
     */
	@GetMapping("/updateRealTime")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "更新实时数据", notes = "传入rtData")
	public void updateRealTime(DeviceItemRealTimeData rtData) {
		deviceItemRealTimeDataRepository.updateRealTime(rtData);
	}

    /**
     * 更新实时数据
     */
	@GetMapping("/updateRealTimeList")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "更新实时数据", notes = "传入rtDataList")
    public void updateRealTimeList(List<DeviceItemRealTimeData> rtDataList) {
		deviceItemRealTimeDataRepository.updateRealTime(rtDataList);
    }

}
