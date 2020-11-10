package org.springblade.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YcAlarmConfig implements Serializable {
	private static final long serialVersionUID = -6286269279399210249L;
	private String id;              //数据项id
	private Float ulimit;			//数据上限
	private Float llimit;           //数据下限
	private Float uulimit;          //数据上上限
	private Float lllimit;          //数据下下限
	private Integer duration;       //异常持续时间，单位秒，超过这个时间触发告警
	private Integer alarmsms;       //告警是否发短信，0,不发短信；1，发送短信
	private Integer alarmemail;    //告警是否发送邮件，0,不发邮件;1,发送邮件
	private String alarmurl;        //告警推送地址
	private String alarmperiod;     //告警时间段
	private Integer alarmtype;      //告警类型


	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Float getUlimit() {
		return ulimit;
	}
	public void setUlimit(Float ulimit) {
		this.ulimit = ulimit;
	}
	public Float getLlimit() {
		return llimit;
	}
	public void setLlimit(Float llimit) {
		this.llimit = llimit;
	}
	public Float getUulimit() {
		return uulimit;
	}
	public void setUulimit(Float uulimit) {
		this.uulimit = uulimit;
	}
	public Float getLllimit() {
		return lllimit;
	}
	public void setLllimit(Float lllimit) {
		this.lllimit = lllimit;
	}
	public Integer getDuration() {
		return duration;
	}
	public void setDuration(Integer duration) {
		this.duration = duration;
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
