package org.springblade.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class DevieceJFPGDate implements Serializable {
	private static final long serialVersionUID = -6924027240286246343L;
	private String gwid;             //网关编号
	private String top;              //尖
	private String peak;             //峰
	private String flat;             //平
	private String valley;           //谷

}
