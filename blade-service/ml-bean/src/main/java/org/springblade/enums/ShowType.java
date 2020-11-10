package org.springblade.enums;

/**
 * @Auther: wangqiaoqing
 * @Date: 2019/6/25
 * @Description:
 */
public enum  ShowType {
    /**
     * 显示
     */
    SHOW(0),
    /**
     * 隐藏
     */
    HIDE(1),
    /**
     * 告警
     */
    ALARM(2),
    /**
     * 能耗
     */
    ENERGY(3),
    /**
     * 开关状态
     */
    SWITCH(4),
    /**
     * 遥控
     */
    YK(5),
    /**
     * 接地刀
     */
    EARTHING(6),
    /**
     * 历史曲线
     */
    HISTORY_CURVE(7);

    private Integer id;

    ShowType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
