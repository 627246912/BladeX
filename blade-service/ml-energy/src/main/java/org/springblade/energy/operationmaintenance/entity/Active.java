package org.springblade.energy.operationmaintenance.entity;

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
@TableName("t_active")
@ApiModel(value = "Active", description = "Active对象")
public class Active extends TenantEntity implements Serializable {

	private static final Long serialVersionUID = 1L;

	@ApiModelProperty("站点id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long stationId;

	@ApiModelProperty("活跃人id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long activePersonId;

	@ApiModelProperty("通知类型 1:告警通知 2:任务通知 3:变更通知 4:报修通知 5:审核通知")
	private Integer noticeType;

	@ApiModelProperty("是否活跃 0:休息 1:活跃")
	private Integer isActive;

	@ApiModelProperty("活跃开始时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date activeStartTime;

	@ApiModelProperty("活跃结束时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date activeEndsTime;
}
