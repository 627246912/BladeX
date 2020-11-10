package org.springblade.constants;

public abstract class WebsocketConstant {

	/**
	 * 数据缓存失效时间
	 */
	public static final long EXPIRE_TIME = 24*3600;

	public static final String websocket_kek = "energy:websocket:";
	public static final String websocket_station= "energy:websocket:station:";

	/**
	 * websocket 参数类型
	 */
	public static final String METHOD = "method";

	/**
	 * websocket type参数：获取系统图实时数据
	 */
	public static final String TYPE_CHART = "getChartData";

	/**
	 * websocket type参数：获取系统图 电能质量 实时数据
	 */
	public static final String POWER_QUALITY = "getPowerQuality";


	/**
	 * websocket type参数：获取管理端首页实时数据
	 */
	public static final String getBigScreenData = "getBigScreenData";


	/**
	 * websocket type参数：租户站点最新告警数据
	 */
	public static final String getRealAlarmData = "getRealAlarmData";

}
