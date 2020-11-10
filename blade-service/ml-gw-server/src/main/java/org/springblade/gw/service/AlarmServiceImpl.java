package org.springblade.gw.service;

import org.springblade.bean.Alarm;
import org.springblade.energy.feign.IAlarmClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author bond
 * @date 2020/5/19 11:02
 * @desc
 */
@Service
public class AlarmServiceImpl {
	@Autowired
	private IAlarmClient iAlarmClient;

	public void saveAlarm(List<Alarm> alarmList){
		iAlarmClient.saveAlarm(alarmList);
	}

}
