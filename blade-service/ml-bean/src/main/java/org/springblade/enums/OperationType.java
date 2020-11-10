package org.springblade.enums;

/**运维类型
 * @author bini
 * @date 2020/8/03 16:25
 * @desc
 */
public enum OperationType {

	INSPECTION(0,"巡检"),  MAINTAIN(1,"保养"),REPAIR(2,"维修"),SAFETY(3,"安全巡视");

	public Integer id;
	public String value;

	OperationType(Integer id, String value) {
		this.id = id;
		this.value = value;
	}

	/**
	 * 获取对应属性值
	 * @param id
	 * @return
	 */
	public static String getValue(Integer id){
		OperationType[] operations = OperationType.values();
		for (OperationType operation : operations) {
			if(id.intValue() == operation.id.intValue()){
				return operation.value;
			}
		}
		return "";
	}

	/**
	 * 获取对应属性值
	 * @param id
	 * @return
	 */
	public static OperationType getOperationType(Integer id){
		OperationType[] operations = OperationType.values();
		for (OperationType operation : operations) {
			if(id.intValue() == operation.id.intValue()){
				return operation;
			}
		}
		return null;
	}

}
