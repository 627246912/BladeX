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
@TableName("t_notice")
@ApiModel(value = "Notice", description = "Notice对象")
public class Notice extends TenantEntity implements Serializable {

	private static final Long serialVersionUID = 1L;

	@ApiModelProperty("站点id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long stationId;

	@ApiModelProperty("位置id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long siteId;

	@ApiModelProperty("任务id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long taskId;

	@ApiModelProperty("责任人id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long responsibleId;

	@ApiModelProperty("领导id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long leaderId;

	@ApiModelProperty("责任人名称")
	private String responsibleName;

	@ApiModelProperty("位置名称")
	private String siteName;

	@ApiModelProperty("通知类型 1:告警通知 2:任务通知 3:变更通知 4:报修通知 5:审核通知")
	private Integer noticeType;

	@ApiModelProperty("通知名称")
	private String noticeName;

	@ApiModelProperty("通知时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date noticeTime;

	@ApiModelProperty("通知内容")
	private String noticeContent;

	@ApiModelProperty("通知时任务状态")
	private String noticeStatus;

	@ApiModelProperty("消息数")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long messageNumber;

	@ApiModelProperty("任务类型：0:巡检 1:保养 2:维修 3:安全检查 4:整改计划 5:倒闸票 6:工作票 7:停送电 8:换班 9:转单 10：人工报修")
	private Integer taskType;

	@ApiModelProperty("转单任务类型 0：巡检 1：保养 2：维修 3：安全检查")
	private Integer transferOrderTaskType;

	@ApiModelProperty("巡检类型：1:巡检 2:巡查")
	private Integer inspectionType;

	@ApiModelProperty("巡检资源类型 1:电 2:水 3:气 4:重点能耗 5:安全巡视")
	private Integer inspectionSourceType;

	@ApiModelProperty("维修类型：1:报修 2:告警")
	private Integer repairType;

	@ApiModelProperty("变更类型：1:转单 2:换班")
	private Integer changeType;

	@ApiModelProperty("巡视类型：1:安全检查 2:整改计划")
	private Integer safetyInspectionType;

	@ApiModelProperty("处理状态 0:未处理 1:已处理")
	private Integer processStatus;

	@ApiModelProperty("是否查看：0:未查看 1:已查看")
	private Integer isLookOver;

	@ApiModelProperty("是否是新消息 0:否 1:是 默认否")
	private Integer isNew;
}
