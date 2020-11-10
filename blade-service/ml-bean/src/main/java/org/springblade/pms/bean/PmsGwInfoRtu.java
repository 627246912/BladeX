package org.springblade.pms.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class PmsGwInfoRtu implements Serializable {
	private static final long serialVersionUID = 6797089314338525478L;
	private Integer rtuid;
	private String devid;
	private String devname; //0未分配，1铁塔，2移动，3联通，4电信

}
