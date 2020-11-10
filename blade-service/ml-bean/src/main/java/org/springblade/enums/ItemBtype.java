package org.springblade.enums;

/**
 * @Auther: wangqiaoqing
 * @Date: 2018/7/27
 * @Description:数据项业务类型
 */
public enum ItemBtype {
    NOATTRIBUTE(0,"无"),ELECTRICITY(1,"电量"),WATERVOLUME(2,"水量"),AIRVOLUME(3,"气量"),
    VOLTAGE(4,"电压"),ELECTRICCURRENT(5,"电流"),ACCESSCONTROL(6,"门禁"),COALGAS(7,"燃气"),
    SMOKE(8,"烟感"),SWITCH(9,"开关"),COMMUNICATIONS(10,"通信"),TRIPPING(11,"跳闸"),
    TEMPERATURE(12,"温度"),HUMIDITY(13,"湿度"),FLOODING(14,"水浸"),INFRARED(15,"红外"),
    PARTIALDISCHARGE(16,"局放"),SULFURHEXAFLUORIDE(17,"六氟化硫"),POWERFACTOR(18,"功率因数"),LIGHTING(19,"照明"),
    SHUTTERDOOR(20,"卷闸门"),POWERSUPPLY(21,"电源"),PM(22,"PM2.5"),CARBONDIOXIDE(23,"CO2"),
    FORMALDEHYDE(24,"甲醛"),VOC(25,"VOC"),PRESSURE(26,"气压"),OXYGEN(27,"氧气"),
    SUNSHINE(28,"光照"),PM10(29,"PM10"),POWER(30,"功率"),LEAKAGE(31,"漏电"),
    SOCKET(32,"插座"),THREEPHASEUNBALANCERATE(33,"三相不平衡率"),LOAD(34,"负荷"),
    TRANSFORMERTEMPERATURE(35,"变压器温度"),DEMAND(36,"需量"), APPARENT(37,"视在"),
    VOLTAGE_HARMONIC(38,"电压谐波"),ELECTRICCURRENT_HARMONIC(39,"电流谐波"),
    TOTAL_HARMONIC(40,"总谐波"),EARTHING(42,"接地刀");


    public Integer id;
    public String value;

    ItemBtype(Integer id, String value) {
        this.id = id;
        this.value = value;
    }

    /**
     * 获取对应属性值
     * @param id
     * @return
     */
    public static String getValue(Integer id){
        ItemBtype[] itemBtypes = ItemBtype.values();
        for (ItemBtype itemBtype : itemBtypes) {
            if(id.intValue() == itemBtype.id.intValue()){
                return itemBtype.value;
            }
        }
        return "";
    }
}
