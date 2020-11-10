package org.springblade.energy.operationmaintenance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 巡检实体类
 *
 * @author bini
 * @since 2020-08-05
 */
@Data
@TableName("t_check_item_record")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "CheckItemRecord", description = "CheckItemRecord检查标准结果对象")
public class CheckItemRecord extends TenantEntity {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "位置区域id")
	private Long siteId;

	@ApiModelProperty(value = "站点id")
	private Long stationId;

	@ApiModelProperty(value = "项目id  包括:巡检，巡查，保养")
	private Long taskId;

	@ApiModelProperty(value = "设备标准项id")
	private Long itemId;

	@ApiModelProperty("标准")
	private String normal;

	@ApiModelProperty("异常")
	private String abnormal;

	@ApiModelProperty("是否开启APP拍照")
	private Integer isAppPhoto;

	@ApiModelProperty("APP拍照地址")
	//拍照默认不勾选
	private String photoUrl;

	@ApiModelProperty("任务类型")
	private Integer taskType;

	@ApiModelProperty("检查结果状态")
	private Integer checkResultStatus;


}
