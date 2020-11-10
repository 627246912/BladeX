package org.springblade.energy.qualitymanagement.entity;

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
@TableName("t_sys_manual")
@ApiModel(value = "MalfunctionStandardManual对象", description = "标准手册")
public class StandardManual extends TenantEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty("手册类型")
	//{"1":"故障标准手册","2":"操作标准手册","3":"安全检查标准手册"}
	private Integer manualType;

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

	@ApiModelProperty("手册名称")
	private String name;

	@ApiModelProperty("标准版本")
	private String standardVersion;

	@ApiModelProperty("文件")
	private String file;

	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty("上传人编号")
	private Long uploadPersonId;

	@ApiModelProperty("上传人名称")
	private String uploadPersonName;

	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@ApiModelProperty("上传时间")
	private Date uploadTime;

	@ApiModelProperty("APP拍照")
	private Integer isAppPhoto;
}
