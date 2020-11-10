package org.springblade.enums;

/**
 * 任务类型
 *
 * @author bini
 * @date 2020/8/11 11:25
 * @desc
 */
public enum  TaskCompleteStatus {

	NO_COMPLETE(0, "未完成"),
	COMPLETE(1, "已完成");

	public Integer id;
	public String value;

	TaskCompleteStatus(Integer id, String value) {
		this.id = id;
		this.value = value;
	}

	/**
	 * 获取对应属性值
	 *
	 * @param id
	 * @return
	 */
	public static String getValue(Integer id) {
		TaskCompleteStatus[] taskCompleteStatuses = TaskCompleteStatus.values();
		for (TaskCompleteStatus taskCompleteStatuse : taskCompleteStatuses) {
			if (id.intValue() == taskCompleteStatuse.id.intValue()) {
				return taskCompleteStatuse.value;
			}
		}
		return "";
	}

	/**
	 * 获取对应属性值
	 *
	 * @param id
	 * @return
	 */
	public static TaskCompleteStatus getTaskCompleteStatus(Integer id) {
		TaskCompleteStatus[] taskCompleteStatuses = TaskCompleteStatus.values();
		for (TaskCompleteStatus taskCompleteStatuse : taskCompleteStatuses) {
			if (id.intValue() == taskCompleteStatuse.id.intValue()) {
				return taskCompleteStatuse;
			}
		}
		return null;
	}

}
