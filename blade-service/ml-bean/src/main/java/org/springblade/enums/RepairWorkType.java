package org.springblade.enums;

/**
 * @Auther: wangqiaoqing
 * @Date: 2018/8/20
 * @Description:
 */
public enum  RepairWorkType {
    REPAIR(0,"用户报修工单"),ALARM(1,"告警工单"),INSPECTION(2,"巡检"),MAINTAIN(3,"保养");

    public Integer id;
    public String desc;

    RepairWorkType(Integer id, String desc) {
        this.id = id;
        this.desc = desc;
    }
}
