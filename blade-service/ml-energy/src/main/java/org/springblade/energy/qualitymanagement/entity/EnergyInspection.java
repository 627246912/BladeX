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
@TableName("t_sys_energy_ins")
@ApiModel(value = "SystemQualityEnergyInspection对象", description = "质量体系--重点能耗设备")
public class EnergyInspection extends TenantEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty("站点")
	private Long stationId;

	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty("位置区域")
	private Long siteId;

	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty("运行设备")
	private Long runDevice;

	@ApiModelProperty("设备名称")
	private String devName;

	@ApiModelProperty("巡查项目")
	private String inspectionItem;

	@ApiModelProperty("机组工况")
	@JsonSerialize(using = ToStringSerializer.class)
	// 0：制冷 1：制热
	private Long unitStatus;

	@ApiModelProperty("检查类型")
	private String checkType;

	@ApiModelProperty("检查结果")
	private String checkResult;

	@ApiModelProperty("是否开启APP拍照")
	private Integer isAppPhoto;

	@ApiModelProperty(value = "站点名称")
	private String stationName;
	@ApiModelProperty(value = "位置名称")
	private String siteName;
}
