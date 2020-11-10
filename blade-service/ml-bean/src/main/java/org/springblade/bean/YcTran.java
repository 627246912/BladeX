package org.springblade.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YcTran implements Serializable {
	private static final long serialVersionUID = 8497778456763556106L;
	private String id;              //数据项id
	private String shortname;       //简称
	private Float basic;            //基值
	private Integer palarm;      	//是否告警
	private Integer store;          //是否存储
	private Integer btype;          //业务类型
	private Float ctratio;          //CT变比
	private String displayname;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Float getBasic() {
		return basic;
	}
	public void setBasic(Float basic) {
		this.basic = basic;
	}
	public Integer getPalarm() {
		return palarm;
	}
	public void setPalarm(Integer palarm) {
		this.palarm = palarm;
	}
	public Integer getStore() {
		return store;
	}
	public void setStore(Integer store) {
		this.store = store;
	}
	public Integer getBtype() {
		return btype;
	}
	public void setBtype(Integer btype) {
		this.btype = btype;
	}
	public Float getCtratio() {
		return ctratio;
	}
	public void setCtratio(Float ctratio) {
		this.ctratio = ctratio;
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

}
