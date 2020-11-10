package org.springblade.enums;

/**
 * @Auther: wangqiaoqing
 * @Date: 2018/8/20
 * @Description: 报修图片类型
 */
public enum RepairWorkPicType {
    FAULT_IMG(0,"故障图片"),HANDLE_IMG(1,"处理图片");

    public Integer id;
    public String desc;

    RepairWorkPicType(Integer id, String desc) {
        this.id = id;
        this.desc = desc;
    }
}
