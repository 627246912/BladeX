package org.springblade.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceCommunicationStatus implements Serializable{

	private static final long serialVersionUID = -1790847567318296303L;
	private String gwid;
	private Integer status;
	private String lasttime;

	public String getGwid() {
		return gwid;
	}
	public void setGwid(String gwid) {
		this.gwid = gwid;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getLasttime() {
		return lasttime;
	}
	public void setLasttime(String lasttime) {
		this.lasttime = lasttime;
	}

}
