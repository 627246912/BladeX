package org.springblade.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YxAlarmConfig implements Serializable {
	private static final long serialVersionUID = -3937544570531872648L;
	private String id;              //数据项id
	private Integer yxalarmval;     //遥信告警值
	private Integer yxalarmlevel;   //遥信告警等级
	private Integer alarmsms;       //告警是否发短信，0,不发短信；1，发送短信
	private Integer alarmemail;     //告警是否发送邮件，0,不发邮件;1,发送邮件
	private String alarmurl;        //告警推送地址
	private String alarmperiod;     //告警时间段
	private Integer alarmtype;		//告警类型

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getYxalarmval() {
		return yxalarmval;
	}
	public void setYxalarmval(Integer yxalarmval) {
		this.yxalarmval = yxalarmval;
	}
	public Integer getYxalarmlevel() {
		return yxalarmlevel;
	}
	public void setYxalarmlevel(Integer yxalarmlevel) {
		this.yxalarmlevel = yxalarmlevel;
	}
	public Integer getAlarmsms() {
		return alarmsms;
	}
	public void setAlarmsms(Integer alarmsms) {
		this.alarmsms = alarmsms;
	}
	public String getAlarmurl() {
		return alarmurl;
	}
	public void setAlarmurl(String alarmurl) {
		this.alarmurl = alarmurl;
	}
	public Integer getAlarmemail() {
		return alarmemail;
	}
	public void setAlarmemail(Integer alarmemail) {
		this.alarmemail = alarmemail;
	}

	public String getAlarmperiod() {
		return alarmperiod;
	}

	public void setAlarmperiod(String alarmperiod) {
		this.alarmperiod = alarmperiod;
	}

	public Integer getAlarmtype() {
		return alarmtype;
	}

	public void setAlarmtype(Integer alarmtype) {
		this.alarmtype = alarmtype;
	}
}
