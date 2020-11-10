package org.springblade.enums;

/**
 * @Auther: wangqiaoqing
 * @Date: 2018/7/27
 * @Description:是否告警
 */
public enum AlarmStatus {
    ALARMING(0,"产生"),ALARMED(1,"消除");

    public Integer id;
    public String value;

    AlarmStatus(Integer id, String value) {
        this.id = id;
        this.value = value;
    }

    /**
     * 获取对应属性值
     * @param id
     * @return
     */
    public static String getValue(Integer id){
        AlarmStatus[] alarmStatuses = AlarmStatus.values();
        for (AlarmStatus alarmStatus : alarmStatuses) {
            if(id.intValue() == alarmStatus.id.intValue()){
                return alarmStatus.value;
            }
        }
        return "";
    }
}
