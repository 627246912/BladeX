package org.springblade.enums;

import org.springblade.core.tool.utils.Func;

/**
 * @Auther: wangqiaoqing
 * @Date: 2018/7/27
 * @Description:告警等级
 */
public enum AlarmLevel {
	EARLY(0,"一般"),  ORDINARY(1,"故障"),SERIOUS(2,"事故");

    public Integer id;
    public String value;

    AlarmLevel(Integer id, String value) {
        this.id = id;
        this.value = value;
    }

    /**
     * 获取对应属性值
     * @param id
     * @return
     */
    public static String getValue(Integer id){
    	if(Func.isEmpty(id)){
			return "";
		}
        AlarmLevel [] alarmLevels = AlarmLevel.values();
        for (AlarmLevel alarmLevel : alarmLevels) {
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
    public static AlarmLevel getAlarmLevel(Integer id){
        AlarmLevel [] alarmLevels = AlarmLevel.values();
        for (AlarmLevel alarmLevel : alarmLevels) {
            if(id.intValue() == alarmLevel.id.intValue()){
                return alarmLevel;
            }
        }
        return null;
    }
}
