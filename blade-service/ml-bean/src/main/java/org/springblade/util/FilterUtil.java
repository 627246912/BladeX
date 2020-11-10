package org.springblade.util;

/**
 * @Auther: wangqiaoqing
 * @Date: 2018/7/28
 * @Description: 过滤工具类
 */
public class FilterUtil {

    /**
     * 判断是否包含
     * @param sourceStr
     * @param searchStr
     * @return
     */
    public static boolean likeStr(String sourceStr,String searchStr){
        if(StringUtils.isEmpty(searchStr)){
            return true;
        }
        if(StringUtils.isEmpty(sourceStr)){
            return true;
        }
        return  sourceStr.toLowerCase().indexOf(searchStr.toLowerCase()) != -1;
    }

    /**
     * 判断是否包含
     * @param sourceStr
     * @param search
     * @return
     */
    public static boolean likeStr(String sourceStr,Object search){
        if(StringUtils.isNull(search)){
            return true;
        }
        String searchStr = search.toString();
        return likeStr(sourceStr,searchStr);
    }
}
