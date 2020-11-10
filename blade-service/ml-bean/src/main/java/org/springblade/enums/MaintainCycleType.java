package org.springblade.enums;

/**保养周期类型
 * @author bini
 * @date 2020/8/03 16:25
 * @desc
 */
public enum MaintainCycleType {

	MONTH(1,"月"),  QUARTER(2,"季"),HALF_YEAR(3,"半年"),YEAR(4,"年");

	public Integer id;
	public String value;

	MaintainCycleType(Integer id, String value) {
		this.id = id;
		this.value = value;
	}

	/**
	 * 获取对应属性值
	 * @param id
	 * @return
	 */
	public static String getValue(Integer id){
		MaintainCycleType[] maintainCycleTypes = MaintainCycleType.values();
		for (MaintainCycleType maintainCycleType : maintainCycleTypes) {
			if(id.intValue() == maintainCycleType.id.intValue()){
				return maintainCycleType.value;
			}
		}
		return "";
	}

	/**
	 * 获取对应属性值
	 * @param id
	 * @return
	 */
	public static MaintainCycleType getMaintainCycleType(Integer id){
		MaintainCycleType[] maintainCycleTypes = MaintainCycleType.values();
		for (MaintainCycleType maintainCycleType : maintainCycleTypes) {
			if(id.intValue() == maintainCycleType.id.intValue()){
				return maintainCycleType;
			}
		}
		return null;
	}

}
