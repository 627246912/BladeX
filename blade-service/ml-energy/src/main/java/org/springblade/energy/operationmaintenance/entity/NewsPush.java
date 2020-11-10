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
@TableName("t_news_push")
@ApiModel(value = "NewsPush对象", description = "消息推送")
public class NewsPush extends TenantEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty("运维类型")
	private Integer operationType;

	@ApiModelProperty("责任人编号")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long responsibleId;

	@ApiModelProperty("任务编号")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long taskId;

	@ApiModelProperty("触发时间戳")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long triggerTime;

	@ApiModelProperty("提前时间戳")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long advanceTime;

	@ApiModelProperty("轮询时间戳")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long pollingTime;

	@ApiModelProperty("推送时间戳")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long scheduleTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty("推送时间")
	private Date time;

	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@ApiModelProperty("任务时间")
	private Date taskTime;

	@ApiModelProperty("是否轮询")
	private Integer isPolling;

	@ApiModelProperty("发送短信")
	private Integer sendSms;

	@ApiModelProperty("手机号码")
	private String phone;

	@ApiModelProperty("发送邮件")
	private Integer sendEmail;

	@ApiModelProperty("邮件")
	private String email;

	@ApiModelProperty("发送站内")
	private Integer sendApp;

	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@ApiModelProperty("上次推送时间")
	private Date lastPushTime;

	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty("标准工时")
	private Long expectTime;

	@ApiModelProperty("自动分配责任人")
	private Integer autoAssign;

	@ApiModelProperty("上级领导id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long superiorsId;

	@ApiModelProperty("位置id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long siteId;

	@ApiModelProperty("站点id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long stationId;

	@ApiModelProperty("上班时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date workTime;

	@ApiModelProperty("下班时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date offWorkTime;

	@ApiModelProperty("上班状态 0:休息 1:空闲 2:忙碌 3:未上班 4:已下班 默认休息")
	private Integer workStatus;

	@ApiModelProperty("上班班次 0:休息 1:早班 2:中班 3:晚班 4:全天 默认休息")
	private Integer workShift;

}
