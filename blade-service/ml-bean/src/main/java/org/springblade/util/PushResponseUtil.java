package org.springblade.util;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.springblade.constants.GwsubscribeConstant;
import org.springblade.core.tool.utils.Func;

import java.util.List;
import java.util.Map;

/**
 * @Auther: wangqiaoqing
 * @Date: 2018/10/29
 * @Description: 调用 接口响应处理工具类
 */
public class PushResponseUtil {

    /**
     * 处理响应结果 返回list
     * @param respContent
     * @param <T>
     * @return
     */
    public static <T> List<T> processResponseListData(String respContent, Class<T> clazz)  {
        if(StringUtils.isEmpty(respContent)){
            return null;
        }

        JSONObject jsonObject = JSONObject.parseObject(respContent);
        if(jsonObject.containsKey(GwsubscribeConstant.PUSH_RESPONSE_DATA)){
            return JsonUtils.getObjectFromJsonString(jsonObject.getString(GwsubscribeConstant.PUSH_RESPONSE_DATA),new TypeReference<List<T>>(clazz){});
        }
        return null;
    }

    /**
     * 处理相应结果 返回对象
     * @param respContent
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T processResponseData(String respContent, Class<T> clazz){
        if(StringUtils.isEmpty(respContent)){
            return null;
        }
        if(respContent.contains("没有数据")) {
        	return null;
        }

        JSONObject jsonObject = JSONObject.parseObject(respContent);
        if(jsonObject.containsKey(GwsubscribeConstant.PUSH_RESPONSE_DATA) && Func.isNotEmpty(jsonObject.getString(GwsubscribeConstant.PUSH_RESPONSE_DATA))){
            //return JsonUtils.getObjectFromJsonString(jsonObject.getString(GwsubscribeConstant.PUSH_RESPONSE_DATA),clazz);
			List<T> list =JsonUtils.getObjectFromJsonString(jsonObject.getString(GwsubscribeConstant.PUSH_RESPONSE_DATA),new TypeReference<List<T>>(clazz){});
			if(Func.isNotEmpty(list)){
				return list.get(0);

			}
			return null;


		}
		return null;
    }

    /**
     * 处理响应结果 返回map
     * @param respContent
     * @return
     */
    public static Map processResponseMapData(String respContent)  {
        if(StringUtils.isEmpty(respContent)){
            return null;
        }

        JSONObject jsonObject = JSONObject.parseObject(respContent);
        if(jsonObject.containsKey(GwsubscribeConstant.PUSH_RESPONSE_DATA)){
            return JsonUtils.getMapFromJsonString(jsonObject.getString(GwsubscribeConstant.PUSH_RESPONSE_DATA));
        }
        return null;
    }

    /**
     * 判断响应结果是否
     * @param respContent
     * @return
     */
    public static boolean processSuccess(String respContent){
        JSONObject jsonObject = JSONObject.parseObject(respContent);
        if(jsonObject.containsKey(GwsubscribeConstant.PUSH_RESPONSE_CODE)){
            String code = jsonObject.getString(GwsubscribeConstant.PUSH_RESPONSE_CODE);
            if(GwsubscribeConstant.PUSH_RESPONSE_SUCCESS.equals(code)) {
                return true;
            }
        }
        return false;
    }

}
