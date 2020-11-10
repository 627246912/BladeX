package org.springblade.interfaces;


import org.springblade.bean.CommonDateInfo;
import org.springblade.dto.DateReq;

/**
 * @Auther: wangqiaoqing
 * @Date: 2019/4/16
 * @Description:
 */
@FunctionalInterface
public interface DateTypeService {
    /**
     * 设置日期相关信息（自然月）
     * @param dateReq 日期参数
     * @return
     */
    CommonDateInfo fillDateInfo(DateReq dateReq);
}
