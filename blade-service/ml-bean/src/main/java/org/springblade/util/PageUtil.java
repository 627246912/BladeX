package org.springblade.util;

import java.util.Collections;
import java.util.List;

/**
 * @Auther: wangqiaoqing
 * @Date: 2018/10/22
 * @Description:
 */
public class PageUtil {
    /**
     * 得到分页后的数据
     *
     * @return 分页后结果
     */
    public static <T> List<T> pagedList(Integer currentPage,Integer pageSize,List<T> items) {
        int fromIndex = (currentPage - 1) * pageSize;
        if (fromIndex >= items.size()) {
            return Collections.emptyList();
        }

        int toIndex = currentPage * pageSize;
        if (toIndex >= items.size()) {
            toIndex = items.size();
        }

        List<T> newList = items.subList(fromIndex, toIndex);
        return newList;
    }


    /**
     * 获取总页数
     * @param pageSize
     * @param totalNum
     * @param <T>
     * @return
     */
    public static <T> Integer getTotalPage(Integer pageSize,Integer totalNum){
        return (totalNum+pageSize-1)/pageSize;
    }
}
