package org.springblade.energy.securitymanagement.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springblade.core.tenant.mp.TenantEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_rectify")
@ApiModel(value = "Rectify对象", description = "安全管理--整改计划")
public class Rectify extends TenantEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty("站点编号")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long stationId;

	@ApiModelProperty("站点名称")
	private String stationName;

	@ApiModelProperty("位置编号")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long siteId;

	@ApiModelProperty("位置名称")
	private String siteName;

	@ApiModelProperty("整改部位")
	private String problemLocation;

	@ApiModelProperty("整改描述")
	private String problemDescription;

	@ApiModelProperty("建议")
	private String recommend;

	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@ApiModelProperty("整改时间")
	private Date rectifyTime;

	@ApiModelProperty("负责人编号")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long responsiblePersonId;

	@ApiModelProperty("负责人名称")
	private String responsiblePersonName;

	@ApiModelProperty("任务编号")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long taskId;

}
