package org.springblade.enums;

/**
 * @author bini
 * @date 2020/8/7 17:47
 * @desc
 */
public enum SourceType {
	POWER_SUPPLY(1,"供电"),  WATER_SUPPLY(2,"供水"),GAS_SUPPLY(3,"供气"),KEY_POWER_SUPPLY(4,"重点能耗");

	public Integer id;
	public String value;

	SourceType(Integer id, String value) {
		this.id = id;
		this.value = value;
	}

	/**
	 * 获取对应属性值
	 * @param id
	 * @return
	 */
	public static String getValue(Integer id){
		SourceType[] sourceTypes = SourceType.values();
		for (SourceType sourceType : sourceTypes) {
			if(id.intValue() == sourceType.id.intValue()){
				return sourceType.value;
			}
		}
		return "";
	}

	/**
	 * 获取对应属性值
	 * @param id
	 * @return
	 */
	public static SourceType getSourceType(Integer id){
		SourceType[] sourceTypes = SourceType.values();
		for (SourceType sourceType : sourceTypes) {
			if(id.intValue() == sourceType.id.intValue()){
				return sourceType;
			}
		}
		return null;
	}
}
