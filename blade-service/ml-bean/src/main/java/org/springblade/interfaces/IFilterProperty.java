package org.springblade.interfaces;

/**
 * @Auther: wangqiaoqing
 * @Date: 2019/3/20
 * @Description:
 */
public interface IFilterProperty<T> {
    /**
     * 过滤实体属性值
     * @param t
     * @return
     */
    boolean filter(T t);
}
