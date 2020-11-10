package org.springblade.enums;

/**
 * @author bond
 * @date 2020/7/10 18:47
 * @desc
 */
public enum AirConditionerDataType {
	//1耗电量，2耗气量，3耗水量，4单位面积空调能耗，5单位面积空调耗冷量，6空调系统能效比，7制冷系统能效比，8冷水机组运行效率")
	WQ(1,"耗电量"),  GQ(2,"耗气量"),SQ(3,"耗水量"),
	ECA(4,"单位面积空调能耗"),  CCA(5,"单位面积空调耗冷量"),EERs(6,"空调系统能效比"),
	EERr(7,"制冷系统能效比"),  COP(8,"冷水机组运行效率");

	public Integer id;
	public String value;

	AirConditionerDataType(Integer id, String value) {
		this.id = id;
		this.value = value;
	}

	/**
	 * 获取对应属性值
	 * @param id
	 * @return
	 */
	public static String getValue(Integer id){
		AirConditionerDataType [] alarmLevels = AirConditionerDataType.values();
		for (AirConditionerDataType alarmLevel : alarmLevels) {
			if(id.intValue() == alarmLevel.id.intValue()){
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
	public static AirConditionerDataType getAlarmLevel(Integer id){
		AirConditionerDataType [] alarmLevels = AirConditionerDataType.values();
		for (AirConditionerDataType alarmLevel : alarmLevels) {
			if(id.intValue() == alarmLevel.id.intValue()){
				return alarmLevel;
			}
		}
		return null;
	}
}
