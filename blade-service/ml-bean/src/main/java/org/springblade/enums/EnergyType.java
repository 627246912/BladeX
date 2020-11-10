package org.springblade.enums;

/**
 * @author bond
 * @date 2020/7/10 18:47
 * @desc
 */
public enum EnergyType {
	POWER(1,"用电"),  WATER(2,"用水"),GAS(3,"用气"),ENERGY(4,"能耗");

	public Integer id;
	public String value;

	EnergyType(Integer id, String value) {
		this.id = id;
		this.value = value;
	}

	/**
	 * 获取对应属性值
	 * @param id
	 * @return
	 */
	public static String getValue(Integer id){
		EnergyType[] alarmLevels = EnergyType.values();
		for (EnergyType alarmLevel : alarmLevels) {
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
	public static EnergyType getAlarmLevel(Integer id){
		EnergyType[] alarmLevels = EnergyType.values();
		for (EnergyType alarmLevel : alarmLevels) {
			if(id.intValue() == alarmLevel.id.intValue()){
				return alarmLevel;
			}
		}
		return null;
	}
}
