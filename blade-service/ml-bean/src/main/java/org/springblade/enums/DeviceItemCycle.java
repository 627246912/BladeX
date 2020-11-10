package org.springblade.enums;

/**
 * @Auther: wangqiaoqing
 * @Date: 2018/10/30
 * @Description:1：按小时 2：按天 3：按月 4：按年
 */
public enum DeviceItemCycle {
    RECORD(0,"录波"),HOURE(1,"按小时"),DAY(2,"按天"),MONTH(3,"按月"),YEAR(4,"按年"),
	FIVEMINUTE(5,"按5分钟");

    public Integer id;
    public String desc;

    DeviceItemCycle(Integer id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    /**
     * 获取对应属性值
     * @param id
     * @return
     */
    public static DeviceItemCycle getDeviceItemCycle(Integer id){
        DeviceItemCycle [] deviceItemCycles = DeviceItemCycle.values();
        for (DeviceItemCycle deviceItemCycle : deviceItemCycles) {
            if(id.intValue() == deviceItemCycle.id.intValue()){
                return deviceItemCycle;
            }
        }
        return null;
    }

    public static DeviceItemCycle getCycleByDateType(CommonDateType commonDateType){
        DeviceItemCycle cycle;
        switch (commonDateType) {
            case WEEK:
                cycle = DeviceItemCycle.DAY;
                break;
            case MONTH:
                cycle = DeviceItemCycle.DAY;
                break;
            case YEAR:
                cycle = DeviceItemCycle.MONTH;
                break;
            case RECENTWEEK:
                cycle = DeviceItemCycle.DAY;
                break;
            case RECENTMONTH:
                cycle = DeviceItemCycle.DAY;
                break;
            case RECENTYEAR:
                cycle = DeviceItemCycle.MONTH;
                break;
			case FIVEMINUTE:
				cycle = DeviceItemCycle.FIVEMINUTE;
				break;
            default:
                cycle = DeviceItemCycle.HOURE;
                break;
        }
        return cycle;
    }

}
