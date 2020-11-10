package org.springblade.gw.adapter;

/**
 * @Auther: bond
 * @Date: 2020/03/10
 * @Description:适配器校验接口类
 */
public interface MqttMessageHandlerAdapter {
    /**
     * 校验处理器
     * @param topic
     * @return
     */
    boolean supports(String topic);

    /**
     * 具体处理
     */
    int handle(String message);
}
