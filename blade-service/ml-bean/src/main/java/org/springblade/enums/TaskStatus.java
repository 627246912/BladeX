package org.springblade.enums;

/**
 * 任务类型
 *
 * @author bini
 * @date 2020/8/03 16:25
 * @desc
 */
public enum TaskStatus {

	NO_CHECK(0, "未检"),
	CHECKING(1, "进行中"),
	SUSPEND(2, "暂停"),
	COMPLETE(3, "已检"),
	CANCELED(4, "已取消"),
	TIMOUT(5, "已超时"),
	Re(6, "重检");

	public Integer id;
	public String value;

	TaskStatus(Integer id, String value) {
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
		TaskStatus[] taskTypes = TaskStatus.values();
		for (TaskStatus taskType : taskTypes) {
			if (id.intValue() == taskType.id.intValue()) {
				return taskType.value;
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
	public static TaskStatus getTaskType(Integer id) {
		TaskStatus[] taskTypes = TaskStatus.values();
		for (TaskStatus taskType : taskTypes) {
			if (id.intValue() == taskType.id.intValue()) {
				return taskType;
			}
		}
		return null;
	}

}
