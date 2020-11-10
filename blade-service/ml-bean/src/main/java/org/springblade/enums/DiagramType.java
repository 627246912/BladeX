package org.springblade.enums;

import org.springblade.core.tool.utils.Func;

/**
 * @Auther: bond
 * @Date: 2020/5/21
 * @Description:图形类别
 */
public enum DiagramType {
	ZHONGYA("0","中压"),DIYA("1","低压"),ZHILIU("2","直流"),
	GONGSHUI("3","供水"),GONGQI("4","供气"),KONGTIAN("5","空调"),XIAOFANG("6","消防系统"),
	UPS("7","UPS"),GAOYA("8","高压");

    public String id;
    public String value;

    DiagramType(String id, String value) {
        this.id = id;
        this.value = value;
    }

    /**
     * 获取对应属性值
     * @param id
     * @return
     */
    public static String getValue(String id){
        DiagramType[] alarmLevels = DiagramType.values();
        for (DiagramType alarmLevel : alarmLevels) {
            if(Func.equals(id,alarmLevel.id)){
                return alarmLevel.value;
            }
        }
        return "";
    }

    /**
     * 获取对应属性值
     * @param id
     * @return
     */
    public static DiagramType getProductDtype(String id){
        DiagramType[] alarmLevels = DiagramType.values();
        for (DiagramType alarmLevel : alarmLevels) {
			if(Func.equals(id,alarmLevel.id)){
                return alarmLevel;
            }
        }
        return null;
    }
}
