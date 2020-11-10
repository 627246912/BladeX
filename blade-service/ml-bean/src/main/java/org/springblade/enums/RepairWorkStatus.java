package org.springblade.enums;

/**
 * @Auther: wangqiaoqing
 * @Date: 2018/8/17
 * @Description: 工单状态
 */
public enum  RepairWorkStatus {
    UNTREATED(0,"未处理"), UNDER_WAY(1,"进行中"),COMPLETED(2,"已完成"),PAUSE(3,"已暂停");

    public Integer id;
    public String desc;

    RepairWorkStatus(Integer id, String desc) {
        this.id = id;
        this.desc = desc;
    }
}
