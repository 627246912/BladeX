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
 * 维修记录实体类
 *
 * @author CYL
 * @since 2020-08-05
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_repair_task_record")
@ApiModel(value = "Repair记录对象", description = "维修记录对象")
public class RepairRecord extends TenantEntity {

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

}
