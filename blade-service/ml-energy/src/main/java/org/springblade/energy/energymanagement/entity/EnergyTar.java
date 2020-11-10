package org.springblade.energy.energymanagement.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * @author bond
 * @date 2020/6/29 10:48
 * @desc
 */
@Data
@TableName("t_energy_tar")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "EnergyTar对象", description = "能源分析-能源指标")
public class EnergyTar extends TenantEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 站点ID
	 */
	@ApiModelProperty(value = "站点ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long stationId;
	/**
	 * 位置ID
	 */
	@ApiModelProperty(value = "位置ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long siteId;

	@ApiModelProperty(value = "部门")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long deptId;

	@ApiModelProperty(value = "能源类型 1供电，2供水，3供气")
	private Integer energyType;

	@ApiModelProperty(value = "指标")
	private Double tar;

	@ApiModelProperty(value = "指标时间类型 2:月3:年8:季度")
	private Integer type;

}
