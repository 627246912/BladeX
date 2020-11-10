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
@TableName("t_sys_water_ins")
@ApiModel(value = "SystemQualityWaterInspection对象", description = "质量体系--供水")
public class WaterInspection extends TenantEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty("站点")
	private Long stationId;

	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty("位置区域")
	private Long siteId;

	@ApiModelProperty("巡检项目")
	private String inspectionItem;

	@ApiModelProperty("机组工况")
	private String unitStatus;

	@ApiModelProperty("标准范围")
	private String standardRange;

	@ApiModelProperty("是否开启APP拍照")
	private Integer isAppPhoto;

	@ApiModelProperty(value = "站点名称")
	private String stationName;

	@ApiModelProperty(value = "位置名称")
	private String siteName;

	@ApiModelProperty(value = "检查结果")
	private String checkResult;
}
