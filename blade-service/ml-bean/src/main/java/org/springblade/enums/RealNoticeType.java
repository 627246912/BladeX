package org.springblade.enums;

/**
 * @Auther: wangqiaoqing
 * @Date: 2018/9/10
 * @Description: 通知具体类型
 */
public enum RealNoticeType {
    SEND_USER_REPAIR(0,"用户报修派发"),COMPLETE_USER_REPAIR(1,"报修完成"),SEND_ALARM(2,"告警派发"),TASK_DEAL(3,"任务处理");

    public Integer id;
    public String desc;

    RealNoticeType(Integer id, String desc) {
        this.id = id;
        this.desc = desc;
    }
}
