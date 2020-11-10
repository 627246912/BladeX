package org.springblade.enums;


import java.util.Arrays;
import java.util.Optional;

/**
 * @Auther: wangqiaoqing
 * @Date: 2019/4/16
 * @Description:
 */
public enum CommonDateType {
    /**
     * 按天
     */
    DAY(0,"点",11,2),
    /**
     * 按星期
     */
    WEEK(1,"日",8,2),
    /**
     * 按月
     */
    MONTH(2,"日",8,2),
    /**
     * 按年
     */
    YEAR(3,"月",5,2),
    /**
     * 近24小时
     */
    RECENTDAY(4,"近24小时",8,5),
    /**
     * 近7天
     */
    RECENTWEEK(5,"近7天",5,5),
    /**
     * 近30天
     */
    RECENTMONTH(6,"近30天",5,5),
    /**
     * 近12月
     */
    RECENTYEAR(7,"近12月",0,7),

	QUARTER(8,"季度",0,8),
	/**
	 * 按天 每5分钟
	 */
	FIVEMINUTE(9,"5分钟",11,9);

    private Integer id;
    private String suffix;
    private Integer startIndex;
    private Integer length;

    public Integer getId() {
        return id;
    }

    public String getSuffix() {
        return suffix;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public Integer getLength() {
        return length;
    }

    CommonDateType(Integer id, String suffix, Integer startIndex, Integer length) {
        this.id = id;
        this.suffix = suffix;
        this.startIndex = startIndex;
        this.length = length;
    }

    /**
     * 根据id 获取日期类型
     * @param id
     * @return
     */
    public static CommonDateType getTypeById(Integer id){
        Optional<CommonDateType> optional = Arrays.stream(CommonDateType.values())
                .filter(dateType -> dateType.getId().equals(id))
                .findFirst();
        return optional.isPresent()?optional.get():null;
    }
}
