package org.springblade.gw.testController;

import com.alibaba.fastjson.TypeReference;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springblade.bean.Alarm;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.energy.feign.IAlarmClient;
import org.springblade.gw.restTemplate.AlarmRepository;
import org.springblade.gw.service.AlarmServiceImpl;
import org.springblade.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author bond
 * @date 2020/4/8 17:24
 * @desc
 */
@RestController
@AllArgsConstructor
@RequestMapping("/alarm")
@Api(value = "测试类", tags = "测试类")
public class AlarmContorller extends BladeController {

	private IAlarmClient iAlarmClient;

	private AlarmRepository  alarmRepository;

	@Autowired
	private AlarmServiceImpl alarmServiceImpl;


	@PostMapping("/save")
	public void save() {
		String message="[{\"alarmid\":4074,\"id\":\"BH4GC79EA09C_1_3\",\"gwid\":\"BH4GC79EA09C\",\"stime\":\"2020-06-03 16:57:01\",\"etime\":\"0001-01-01 00:00:00\",\"val\":0,\"depend\":\"{}\",\"val2\":0,\"depend2\":\"{}\",\"level\":2}]";

		//[{"alarmid":4074,"id":"BH4GC79EA09C_1_3","gwid":"BH4GC79EA09C","stime":"2020-06-03 16:57:01","etime":"0001-01-01 00:00:00","val":0,"depend":"{}","val2":0,"depend2":"{}","level":2}]

		List<Alarm> alarmList = JsonUtils.getObjectFromJsonString(message,new TypeReference<List<Alarm>>(){});

//		List<Alarm> alarmList = new ArrayList<Alarm>();
//		Alarm alarm=new Alarm();
//		alarm.setAlarmid(1098298);
//		alarm.setEtime("2020-04-08 12:00:00");
//		alarm.setStime("2020-04-08 11:00:00");
//		alarm.setGwid("BH4GC79EA09C");
//		alarm.setLevel(1);
//		alarmList.add(alarm);
		//IAlarmClient aa= alarmServiceImpl.a();
	}

	@PostMapping("/getAlarm")
	public void getAlarm() {
		List<Alarm> alarmList=alarmRepository.getAlarmInfoByDiviceCodesAndType("mailian",
			"1808AB90B296",null,null,null,null);
	}
}
