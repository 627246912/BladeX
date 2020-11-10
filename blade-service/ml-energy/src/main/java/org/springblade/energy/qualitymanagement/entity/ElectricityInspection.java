package org.springblade.energy.qualitymanagement.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springblade.core.tenant.mp.TenantEntity;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_sys_ele_ins")
@ApiModel(value = "SystemQualityElectricityInspection对象", description = "质检体系--供电")
public class ElectricityInspection extends TenantEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty("巡检类型")
	//默认巡检，1巡查
	private Integer inspectionType;

	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty("站点")
	private Long stationId;

	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty("区域位置")
	private Long siteId;

	@ApiModelProperty("巡检项目")
	private String inspectionItem;

	@ApiModelProperty("巡检方法")
	/*巡检方法：
	1：目视
	2：目测
	3：目视 & 测量
	4：目视 & 手动
	5：目视 & 测温仪
	6：目视 & 听 & 闻 & 手动 & 测量
	 */
	private Integer inspectionMethod;

	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty("设备名称")
	private String devName;

	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty("设备编号")
	private Long devNumber;

	@ApiModelProperty("巡查内容")
	private String inspectionContent;

	@ApiModelProperty("标准")
	private String normal;

	@ApiModelProperty("异常")
	private String abnormal;

	@ApiModelProperty("是否开启APP拍照")
	//拍照默认不勾选
	private Integer isAppPhoto;

	@ApiModelProperty(value = "站点名称")
	private String stationName;
	@ApiModelProperty(value = "位置名称")
	private String siteName;

}
