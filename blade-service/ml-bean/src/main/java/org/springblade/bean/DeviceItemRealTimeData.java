package org.springblade.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Auther: bond
 * @Date: 2020/03/10
 * @Description:网关数据项实时数据
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceItemRealTimeData implements Serializable {
	private static final long serialVersionUID = -6104004374053581084L;
	private String id;
	private String time;              //
	private String gwid;          //设备外键，t_device表fcode
	private Double val;               //当前值
	private Date lastUpdateTime; //最后更新时间

}
