package org.springblade.enums;


import org.springblade.util.StringUtils;

/**
 * @Auther: bond
 * @Date: 2020/8/11
 * @Description:
 */
public enum WaterGasPriceType {
    WATER(0,"水"),GAS_PRICE_TYPE(1,"气");

    public Integer id;
    public String desc;

    WaterGasPriceType(Integer id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public static WaterGasPriceType getById(Integer id){
        if(StringUtils.isNull(id)){
            return null;
        }
        WaterGasPriceType[] booleanEnums = WaterGasPriceType.values();
        for (WaterGasPriceType booleanEnum : booleanEnums) {
            if(booleanEnum.id.equals(id)){
                return booleanEnum;
            }
        }
        return null;
    }
}
