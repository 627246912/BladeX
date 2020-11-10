package org.springblade.pms.websocket.constant;

/**
 * @author bond
 * @date 2020/9/15 18:28
 * @desc
 */
public class SocketConstant {
	/**
	 * 数据缓存失效时间
	 */
	public static final long EXPIRE_TIME = 24*3600;

	public static final String websocket_kek = "pms:websocket:";
	public static final String websocket_station= "pms:websocket:station:";

	/**
	 * websocket 参数类型
	 */
	public static final String METHOD = "method";

	/**
	 * websocket type参数：大屏端首页实时数据
	 */
	public static final String getBigScreenData = "getBigScreenData";

	/**
	 * websocket type参数：大屏端站点页面实时数据
	 */
	public static final String getBigScreenStationData = "getBigScreenStationData";
}
