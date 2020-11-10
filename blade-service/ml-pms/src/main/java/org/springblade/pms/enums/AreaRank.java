package org.springblade.pms.enums;

/**
 * @Auther: bond
 * @Date: 2020/09/01
 * @Description:
 */
public enum AreaRank {
    PROVINCE(0,"省份"),CITY(1,"城市"),AREA(2,"区域"),STATION(3,"基站");

    public Integer id;
    public String desc;

    AreaRank(Integer id, String desc) {
        this.id = id;
        this.desc = desc;
    }
}
