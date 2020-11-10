package org.springblade.pms.enums;

import org.springblade.core.tool.utils.Func;

/**
 * @author bond
 * @date 2020/10/20 11:06
 * @desc
 */
public enum PmsAlarmType {

	SBGZ(7,"设备故障"),
	TXZD(14,"通讯中断"),
	TDGJ(1,"停电告警"),
	PSID1062(1062,"过电流"),
	PSID1107(1107,"短路保护"),
	PSID1109(1109,"过载保护"),
	PSID1116(1116,"采集模块故障") ,
	PSID1117(1117,"控制输出故障"),
	PSID1118(1118,"时钟模块故障"),
	PSID1119(1119,"存储器1异常(铁电)"),
	PSID1120(1120,"存储器2异常(flash)"),
	PSID1121(1121,"4G模块异常"),
	PSID1122(1122,"交流停电告警"),
	PSID1123(1123,"交流过压"),
	PSID1124(1124,"交流欠压"),
	PSID1125(1125,"直流低压"),
	PSID1126(1126,"直流高压"),
	PSID1127(1127,"温度高"),
	PSID1134(1134,"控制故障"),
	PSID1135(1135,"备电时长下电"),
	PSID1136(1136,"备电电压下电");



		public int id;
		public String value;

	PmsAlarmType(int id, String value) {
			this.id = id;
			this.value = value;
		}

		/**
		 * 获取对应属性值
		 * @param id
		 * @return
		 */
		public static String getValue(int id){
			if(Func.isEmpty(id)){
				return "";
			}
			PmsAlarmType[] alarmTypes = PmsAlarmType.values();
			for (PmsAlarmType alarmType : alarmTypes) {
				if(Func.equals(id,alarmType.id)){
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
		public static PmsAlarmType getAlarmType(int id){
			PmsAlarmType[] alarmTypes = PmsAlarmType.values();
			for (PmsAlarmType alarmType : alarmTypes) {
				if(Func.equals(id,alarmType.id)){
					return alarmType;
				}
			}
			return null;
		}
}
