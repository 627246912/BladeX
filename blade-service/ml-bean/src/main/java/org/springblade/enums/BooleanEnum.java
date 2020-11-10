package org.springblade.enums;


import org.springblade.util.StringUtils;

/**
 * @Auther: wangqiaoqing
 * @Date: 2018/8/11
 * @Description:
 */
public enum BooleanEnum {
    NO(0,"否"),YES(1,"是");

    public Integer id;
    public String desc;

    BooleanEnum(Integer id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public static BooleanEnum getById(Integer id){
        if(StringUtils.isNull(id)){
            return null;
        }
        BooleanEnum [] booleanEnums = BooleanEnum.values();
        for (BooleanEnum booleanEnum : booleanEnums) {
            if(booleanEnum.id.equals(id)){
                return booleanEnum;
            }
        }
        return null;
    }
}
