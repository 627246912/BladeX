package org.springblade.pms.enums;

/**
 * @author bond
 * @date 2020/7/14 15:01
 * @desc
 */
public enum TimeContorlEnu {



//1528	定时控制使能
//1529	时段1时间		S
//1530	时段1控制状态		S
//1531	时段2时间		S
//1532	时段2控制状态		S
//1533	时段3时间		S
//1534	时段3控制状态		S
//1535	时段4时间		S
//1536	时段4控制状态		S
//1537	时段5时间		S
//1538	时段5控制状态		S
//1539	时段6时间		S
//1540	时段6控制状态		S

	//六组定时开关
	ONOFF1(1,"1529","1530"),
	ONOFF2(2,"1531","1532"),
	ONOFF3(3,"1533","1534"),
	ONOFF4(4,"1535","1536"),
	ONOFF5(5,"1537","1538"),
	ONOFF6(6,"1539","1540");


	public Integer id;
	public String statusSid;
	public String timeSid;

	TimeContorlEnu(Integer id, String timeSid, String statusSid) {
		this.id = id;
		this.statusSid = statusSid;
		this.timeSid = timeSid;
	}


	}
