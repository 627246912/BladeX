package org.springblade.system.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springblade.core.mp.base.BaseEntity;
import org.springblade.core.tenant.mp.TenantEntity;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_user_shift")
@ApiModel(value = "UserShift对象", description = "用户班次记录表")
public class UserShift extends TenantEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty("主键id")
	@TableId("id")
	private Long id;


	@ApiModelProperty("用户id")
	private Long userId;


	@ApiModelProperty("班次年份")
	private Integer ShiftYear;


	@ApiModelProperty("班次月份")
	private Integer shiftMonth;


	@ApiModelProperty("班次周期")
	private String shiftCycle;

}
