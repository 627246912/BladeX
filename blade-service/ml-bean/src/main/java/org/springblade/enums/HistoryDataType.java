package org.springblade.enums;

/**
 * @Auther: wangqiaoqing
 * @Date: 2018/7/27
 * @Description:历史数据返回类型
 */
public enum HistoryDataType {
    TOTAL(1,"总"),TOP(2,"尖"),PEAK(3,"峰"),FLAT(4,"平"),VALLEY(5,"谷");

    public Integer id;
    public String value;

    HistoryDataType(Integer id, String value) {
        this.id = id;
        this.value = value;
    }

    /**
     * 获取对应属性值
     * @param id
     * @return
     */
    public static String getValue(Integer id){
        HistoryDataType[] historyDataTypes = HistoryDataType.values();
        for (HistoryDataType historyDataType : historyDataTypes) {
            if(id.intValue() == historyDataType.id.intValue()){
                return historyDataType.value;
            }
        }
        return "";
    }


}
