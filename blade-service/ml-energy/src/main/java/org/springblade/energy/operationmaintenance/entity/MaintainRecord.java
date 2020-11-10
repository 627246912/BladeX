package org.springblade.energy.operationmaintenance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springblade.core.tenant.mp.TenantEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 保养记录实体类
 *
 * @author CYL
 * @since 2020-08-05
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_maintain_task_record")
@ApiModel(value = "保养记录实体类", description = "保养记录对象")
public class MaintainRecord extends TenantEntity {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "任务编号")
	private Long taskId;

	@ApiModelProperty(value = "任务状态")
	private Integer taskStatus;

	@ApiModelProperty(value = "期望开始时间")
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	private Date expectStartTime;

	@ApiModelProperty(value = "实际开始时间")
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	private Date actualStartTime;

	@ApiModelProperty(value = "实际完成时间")
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	private Date actualEndTime;

	@ApiModelProperty("过期时间")
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	private Date expiredTime;

	@ApiModelProperty("记录过期状态 0:未过期,1:已过期 默认0")
	private Integer recordStatus;

	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	//@ApiModelProperty("记录过期时间")
	private Date abnormalTime;

}
