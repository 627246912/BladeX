package org.springblade.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class DeviceItemHistoryData implements Serializable {
	private static final long serialVersionUID = 3850856713341991918L;
	private String appid;					//网关所属公司
	private String stationId;					//站点ID

	private String id;
	private String tm;              //时间
	private Integer ctg;            //该条数据的类别，1-总、2-尖、3-峰、4-平、5-谷
	private Float val;              //数据值

}
