package org.springblade.enums;
/**
 * @Auther: 查找告警类型
 * @Date: 2018/10/30
 * @Description: 0:所有告警,1:未结束告警
 */
public enum SelectAlarmType {
    ALL(0,"所有告警"),NOEND(1,"未结束告警");

    public Integer id;
    public String value;

    SelectAlarmType(Integer id, String value) {
        this.id = id;
        this.value = value;
    }

}
