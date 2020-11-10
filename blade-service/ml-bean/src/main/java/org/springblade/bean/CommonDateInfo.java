package org.springblade.bean;

import cn.hutool.core.util.StrUtil;
import org.springblade.enums.CommonDateType;
import org.springblade.util.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Auther: wangqiaoqing
 * @Date: 2019/5/27
 * @Description:
 */
public class CommonDateInfo implements Serializable {
    private static final long serialVersionUID = 2467337437556123605L;
    /**
     * 日期列表
     */
    private List<String> dasys;
    /**
     * 显示
     */
    private List<String> showRows;
    /**
     * 开始时间
     */
    private Date startDate;
    /**
     * 结束时间
     */
    private Date endDate;
    /**
     * 日期类型
     */
    private CommonDateType dateType;

    public CommonDateInfo() {
    }

    public CommonDateInfo(List<String> dasys,List<String> showRows, Date startDate, Date endDate, CommonDateType dateType) {
        this.dasys = dasys;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dateType = dateType;
		this.showRows = showRows;
    }

    public List<String> getDasys() {
        return dasys;
    }

    public List<String> getShowDays() {return showRows; }



    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public CommonDateType getDateType() {
        return dateType;
    }

    public String getCompareDayStr(String dayStr,Integer length) {
        if(StringUtils.isNull(length)){
            length = dateType.getLength();
        }
        return StrUtil.sub(dayStr, 0, dateType.getStartIndex() + length);
    }
}
