package org.springblade.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Alarm implements Serializable {
	private static final long serialVersionUID = 2931012013232985580L;
	private String id;				//具体 如网关，rtu，数据项id
	private String gwid;			//网关
	private Float val;              //数值
	private String stime;             //告警开始时间
	private String etime;             //告警结束时间
	private Integer level;		    //告警等级 1:普通，2:严重

	private Integer alarmtype;//告警类型 1:超压，2:超温度，3过流，4过压，5低电压，6断路，7设备故障，8开关断开，9损失报警，10低压报警，11缺水报警，12，压差报警
	private Integer alarmid;	//告警id
	private String depend;  //告警开始触发值
	private String depend2; //告警结束触发值
	private Map<String,Float> dependMap;
	private Map<String,Float> dependMap2;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGwid() {
		return gwid;
	}
	public void setGwid(String gwid) {
		this.gwid = gwid;
	}
	public Float getVal() {
		return val;
	}
	public void setVal(Float val) {
		this.val = val;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public String getStime() {
		return stime;
	}
	public void setStime(String stime) {
		this.stime = stime;
	}
	public String getEtime() {
		return etime;
	}
	public void setEtime(String etime) {
		this.etime = etime;
	}

	public Integer getAlarmid() {
		return alarmid;
	}

	public void setAlarmid(Integer alarmid) {
		this.alarmid = alarmid;
	}

	public String getDepend() {
		return depend;
	}

	public void setDepend(String depend) {
		if(StringUtils.isNotEmpty(depend)) {
			dependMap = JSON.parseObject(depend,new TypeReference<Map<String,Float>>() {});
		}
		this.depend = depend;
	}

	public String getDepend2() {
		return depend2;
	}

	public void setDepend2(String depend2) {
		if(StringUtils.isNotEmpty(depend2)) {
			dependMap2 = JSON.parseObject(depend2,new TypeReference<Map<String,Float>>() {});
		}
		this.depend2 = depend2;
	}

	public Map<String, Float> getDependMap() {
		return dependMap;
	}

	public void setDependMap(Map<String, Float> dependMap) {
		this.dependMap = dependMap;
	}

	public Map<String, Float> getDependMap2() {
		return dependMap2;
	}

	public void setDependMap2(Map<String, Float> dependMap2) {
		this.dependMap2 = dependMap2;
	}

	public Integer getHashCode() {
		StringBuffer sbf = new StringBuffer();
		sbf.append(id);
		sbf.append("&");
		sbf.append(gwid);
		sbf.append("&");
		sbf.append(val);
		sbf.append("&");
		sbf.append(stime);
		sbf.append("&");
		sbf.append(etime);
		sbf.append("&");
		sbf.append(level);
		sbf.append("&");
		sbf.append(alarmid);
		return sbf.toString().hashCode();
	}

	public Integer getAlarmtype() {
		return alarmtype;
	}

	public void setAlarmtype(Integer alarmtype) {
		this.alarmtype = alarmtype;
	}
}
