package org.springblade.constants;

public abstract class BigScreenRedis {




	//大屏端
	public static final String websocket_station= "energy:bigscreen:station:";

	public static final String station = "energy:station:";

	//安全运行天数
	public static final String runDay = station+"runDay";
	public static final String siteNum =station+ "siteNum";


	//电能
	public static final String power = "power:";
	//低压
	public static final String lowpower = "lowpower:";
	//中压
	public static final String hgihpower = "hgihpower:";

	//天然气
	public static final String gas = "gas:";
	//水
	public static final String water = "water:";

	//空调
	public static final String airc = "airc:";

	//大屏端站点电量能耗公示
	public static final String stationPowerNenghaogongshi =websocket_station+power+ "nenghaogongshi:";

	//大屏端站点年用电量情况
	public static final String stationYearPowerTotalVal =websocket_station+power+ "yearTotalVal:";
	//大屏端站点月用电量情况
	public static final String stationMonthPowerTotalVal =websocket_station+power+ "monthTotalVal:";

	//大屏端站点年用电量情况--低压
	public static final String stationYearPowerLowTotalVal =websocket_station+lowpower+ "yearTotalVal:";
	//大屏端站点月用电量情况--低压
	public static final String stationMonthPowerLowTotalVal =websocket_station+lowpower+ "monthTotalVal:";

	//大屏端站点年用电量情况--高压
	public static final String stationYearPowerHighTotalVal =websocket_station+hgihpower+ "yearTotalVal:";
	//大屏端站点月用电量情况--高压
	public static final String stationMonthPowerHighTotalVal =websocket_station+hgihpower+ "monthTotalVal:";


	//大屏端站点年用水量情况
	public static final String stationYearWaterTotalVal =websocket_station+water+ "yearTotalVal:";
	//大屏端站点月用水量情况
	public static final String stationMonthWaterTotalVal =websocket_station+water+ "monthTotalVal:";

	//大屏端站点年用气量情况
	public static final String stationYearGasTotalVal =websocket_station+gas+ "yearTotalVal:";
	//大屏端站点月用气量情况
	public static final String stationMonthGasTotalVal =websocket_station+gas+ "monthTotalVal:";



	//大屏端站点月用气量情况---空调
	public static final String stationMonthAircGasTotalVal =websocket_station+airc+ "monthGasTotalVal:";
	//大屏端站点月用气量情况---空调
	public static final String stationMonthAircPowerTotalVal =websocket_station+airc+ "monthPowerTotalVal:";



	//能耗异常统计
	public static final String stationhandlealarmNum =websocket_station+ "handlealarm:";

}
