package org.springblade.gw.handler;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ZipUtil;
import org.springblade.gw.adapter.MqttMessageHandlerAdapter;
import org.springblade.util.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: bond
 * @Date: 2020/03/10
 * @Description:适配器分发类
 */
@Component
public class MessageBaseHandler {
    private static List<MqttMessageHandlerAdapter> handlerAdapters = new ArrayList<>();

    public MessageBaseHandler(){
        handlerAdapters.clear();
        handlerAdapters.add(new AlarmMessageHandler());
        handlerAdapters.add(new DataitemMessageHandler());
        handlerAdapters.add(new GwinfoMessageHandler());
        handlerAdapters.add(new RtuinfoMessageHandler());
        handlerAdapters.add(new SignalMessageHandler());
        handlerAdapters.add(new GwstateMessageHandler());
    }

    public MessageBaseHandler(List<MqttMessageHandlerAdapter> handlers){
        if(StringUtils.isNotEmpty(handlers)){
            handlerAdapters.clear();
            handlerAdapters.addAll(handlers);
        }
    }

	public int doDispatch(String topic,byte[] message){
		int result = 0;
		if(StringUtils.isEmpty(topic)){
			return result;
		}

		//得到对应适配器
		MqttMessageHandlerAdapter adapter = getHandler(topic);
		if(StringUtils.isNotNull(adapter) && StringUtils.isNotNull(message)) {
			String messageStr = ZipUtil.unGzip(message,CharsetUtil.UTF_8);
			//通过适配器执行对应的处理方法
			result = adapter.handle(messageStr);
		}
		return result;
	}

    public MqttMessageHandlerAdapter getHandler(String topic){
        for(MqttMessageHandlerAdapter adapter: handlerAdapters){
            if(adapter.supports(topic)){
                return adapter;
            }
        }
        return null;
    }
}
