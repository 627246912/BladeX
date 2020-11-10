package org.springblade.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YxTran implements Serializable {
	private static final long serialVersionUID = 6655132727064956802L;
	private String id;              //数据项id
	private String shortname;       //简称
	private Integer palarm;      	//是否告警
	private Integer btype;          //业务类型
	private String displayname;
	private String desc0;           //遥信描述0
	private String desc1;			//遥信描述1

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getPalarm() {
		return palarm;
	}
	public void setPalarm(Integer palarm) {
		this.palarm = palarm;
	}
	public Integer getBtype() {
		return btype;
	}
	public void setBtype(Integer btype) {
		this.btype = btype;
	}
	public String getShortname() {
		return shortname;
	}
	public void setShortname(String shortname) {
		this.shortname = shortname;
	}
	public String getDisplayname() {
		return displayname;
	}
	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}
	public String getDesc0() {
		return desc0;
	}
	public void setDesc0(String desc0) {
		this.desc0 = desc0;
	}
	public String getDesc1() {
		return desc1;
	}
	public void setDesc1(String desc1) {
		this.desc1 = desc1;
	}

}
