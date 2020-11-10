package org.springblade.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YkTran implements Serializable {
	private static final long serialVersionUID = 6455612625768285248L;
	private String id;              //数据项id
	private String shortname;       //简称
	private String desc;            //描述
	private String displayname;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getShortname() {
		return shortname;
	}
	public void setShortname(String shortname) {
		this.shortname = shortname;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getDisplayname() {
		return displayname;
	}
	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}

}
