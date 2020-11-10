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
@TableName("t_sys_water_main")
@ApiModel
public class WaterMaintenance extends TenantEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty("站点")
	private Long stationId;

	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty("位置区域")
	private Long siteId;

	@ApiModelProperty("设备名称")
	private Integer devName;

	@ApiModelProperty("类别")
	private Integer type;

	@ApiModelProperty("部位")
	private String location;

	@ApiModelProperty("活动项目")
	private String activityItem;

	@ApiModelProperty("注意事项")
	private String precautions;

	@ApiModelProperty("工具与技术")
	private String utilAndSkill;

	@ApiModelProperty("判定基准")
	private String judgementStandard;

	@ApiModelProperty("是否开启APP拍照")
	private Integer isAppPhoto;

	@ApiModelProperty(value = "站点名称")
	private String stationName;
	@ApiModelProperty(value = "位置名称")
	private String siteName;
}
