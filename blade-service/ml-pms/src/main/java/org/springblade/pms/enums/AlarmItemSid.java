package org.springblade.pms.enums;

import org.springblade.core.tool.utils.Func;

/**
 * @author bond
 * @date 2020/7/14 15:01
 * @desc
 */
public enum AlarmItemSid {

	//告警数据项


	SID1062("1062","0"),
	SID1134("1134","0"),
	SID1109("1109","0"),
	SID1107("1107","0"),
	SID1135("1135","0"),
	SID1136("1136","0");









	public String id;
	public String value;

	AlarmItemSid(String id, String value) {
		this.id = id;
		this.value = value;
	}

	/**
	 * 获取对应属性值
	 * @param id
	 * @return
	 */
	public static String getValue(String id){
		AlarmItemSid[] alarmLevels = AlarmItemSid.values();
		for (AlarmItemSid alarmLevel : alarmLevels) {
			if(Func.equals(id,alarmLevel.id)){
				return alarmLevel.value;
			}
		}
		return "";
	}

	/**
	 * 获取对应属性值
	 * @param id
	 * @return
	 */
	public static AlarmItemSid getProductSid(String id){
		AlarmItemSid[] alarmLevels = AlarmItemSid.values();
		for (AlarmItemSid alarmLevel : alarmLevels) {
			if(Func.equals(id,alarmLevel.id)){
				return alarmLevel;
			}
		}
		return null;
	}

	}
