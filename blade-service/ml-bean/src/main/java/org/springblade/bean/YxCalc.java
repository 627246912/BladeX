package org.springblade.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YxCalc implements Serializable {
	private static final long serialVersionUID = -2776879761016917569L;
	private String id;              //数据项id
	private String name;            //名称
	private String shortname;       //简称
	private String desc0;           //遥信描述0
	private String desc1;			//遥信描述1
	private String unit;			//单位
	private Integer palarm;			//是否告警
	private String formula;   		//脚本
	private Integer btype;			//业务类型
	private String relid;           //关联主变量
	private String displayname;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public Integer getPalarm() {
		return palarm;
	}
	public void setPalarm(Integer palarm) {
		this.palarm = palarm;
	}
	public String getFormula() {
		return formula;
	}
	public void setFormula(String formula) {
		this.formula = formula;
	}
	public Integer getBtype() {
		return btype;
	}
	public void setBtype(Integer btype) {
		this.btype = btype;
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
	public String getShortname() {
		return shortname;
	}
	public void setShortname(String shortname) {
		this.shortname = shortname;
	}
	public String getRelid() {
		return relid;
	}
	public void setRelid(String relid) {
		this.relid = relid;
	}
	public String getDisplayname() {
		return displayname;
	}
	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}
}
