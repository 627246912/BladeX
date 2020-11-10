package org.springblade.enums;

import org.springblade.core.tool.utils.Func;

/**
 * @Auther: bond
 * @Date: 2020/5/21
 * @Description:产品类别
 */
public enum ProductDtype {
	BIANYAQU("1","变压器"),
	JINXIANKAIGUAN("2","进线开关"),
	DIDO("3","DIDO"),
	RANQIBIAO("4","燃气表"),
	SHUIBIAO("5","水表"),
	SHUIYABIAO("6","水压表"),
	HUANJIANG("7","环境"),
	KUIXIANKAIGUAN("8","馈线开关"),
	JINXIANGUI("1000","进线柜"),
	KUIXIANGUI("1001","馈线柜"),
	DIANRONGGUI("1002","电容柜"),
	MUXIANGUI("1003","母排柜"),
	ZHILIUGUI("1004","直流柜"),

	LENGSHUIJIZU("2000","冷水机组"),
	LENGQUESHUIBENG("2001","冷却水泵"),
	LENGDONGSHUIBENG("2002","冷冻水泵"),
	LENGQUETA("2003","冷却塔"),
	DUANKOUDUANYUAN("10","端口检测单元")
;

    public String id;
    public String value;

    ProductDtype(String id, String value) {
        this.id = id;
        this.value = value;
    }

    /**
     * 获取对应属性值
     * @param id
     * @return
     */
    public static String getValue(String id){
        ProductDtype[] alarmLevels = ProductDtype.values();
        for (ProductDtype alarmLevel : alarmLevels) {
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
    public static ProductDtype getProductDtype(String id){
        ProductDtype[] alarmLevels = ProductDtype.values();
        for (ProductDtype alarmLevel : alarmLevels) {
			if(Func.equals(id,alarmLevel.id)){
                return alarmLevel;
            }
        }
        return null;
    }
}
