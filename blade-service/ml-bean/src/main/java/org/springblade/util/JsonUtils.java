package org.springblade.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 查询工具类
 *
 */
public class JsonUtils {

    public static <T> T getObjectFromJsonString(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    public static <T> T getObjectFromJsonString(String json, TypeReference<T> type) {
        return JSON.parseObject(json, type);
    }

    public static String getJsonStringFromObject(Object obj) {
        return JSON.toJSONString(obj);
    }

    public static String getJsonStringFromList(List<?> list) {
        return JSON.toJSONString(list);
    }

    /**
     * 从json HASH表达式中获取一个map
     *
     * @param jsonString
     * @return
     */
	public static Map<String, Object> getMapFromJsonString(String jsonString) {
        return getMapFromJsonObject(JSON.parseObject(jsonString));
    }

    @SuppressWarnings("unchecked")
	public static Map<String, Object> getMapFromJsonObject(JSONObject jsonObject) {
        if(jsonObject == null){
        	return null;
        }
        Set<Map.Entry<String, Object>> keySet = jsonObject.entrySet();

        Map<String, Object> valueMap = new HashMap<String, Object>();
        String key;
        Object value;
        for (Map.Entry<String, Object> keyEntry : keySet) {
            key = keyEntry.getKey();
            value = keyEntry.getValue();
            valueMap.put(key, value);
        }
        return valueMap;
    }

    /**
     * 从Map对象得到Json字串
     *
     * @param map
     * @return
     */
    public static String getJsonStringFromMap(Map<?, ?> map) {
        return JSON.toJSONString(map);
    }

    /**
     * 从json字串中得到相应java数组
     *
     * @param jsonString
     *            like "[\"李斯\",100]"
     * @return
     */
    public static Object[] getObjectArrayFromJsonString(String jsonString) {
        JSONArray jsonArray = JSONArray.parseArray(jsonString);
        return jsonArray.toArray();
    }

    /**
     * 将list转换成Array
     *
     * @param list
     * @return
     */
    public static Object[] getObjectArrayFromList(List<?> list) {
        JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(list));
        return jsonArray.toArray();
    }
}

