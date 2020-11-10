package org.springblade.enums;

import org.springblade.core.tool.utils.Func;

/**
 * @Auther: bond
 * @Date: 2020-05-07
 * @Description:告警类别
 */
public enum AlarmType {
	//GY(0,"过压"),QY(1,"欠压"),GL(2,"过流"),GW(3,"过温"),GZ(4,"故障");
	CY(1,"超压"),CWD(2,"超温度"),GL(3,"过流"),
	GY(4,"过压"),DDY(5,"低电压"),DL(6,"断路"),
	SBGZ(7,"设备故障"),KGDK(8,"开关断开"),SSBJ(9,"损失报警"),
	DYBJ(10,"低压报警"),QSBJ(11,"缺水报警"),YCBJ(12,"压差报警"),QTBJ(13,"其他报警"),
	TXZD(14,"通讯中断");
	//alarmtype;//告警类型 1:超压，2:超温度，3过流，4过压，5低电压，6断路，7设备故障，8开关断开，9损失报警，10低压报警，11缺水报警，12，压差报警

    public Integer id;
    public String value;

    AlarmType(Integer id, String value) {
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
        AlarmType[] alarmTypes = AlarmType.values();
        for (AlarmType alarmType : alarmTypes) {
            if(id.intValue() == alarmType.id.intValue()){
                return alarmType.value;
            }
        }
        return "";
    }

    /**
     * 获取对应属性值
     * @param id
     * @return
     */
    public static AlarmType getAlarmType(Integer id){
        AlarmType[] alarmTypes = AlarmType.values();
        for (AlarmType alarmType : alarmTypes) {
            if(id.intValue() == alarmType.id.intValue()){
                return alarmType;
            }
        }
        return null;
    }

}
