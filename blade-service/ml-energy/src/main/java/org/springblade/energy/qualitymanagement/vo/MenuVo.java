package org.springblade.energy.qualitymanagement.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import org.springblade.core.tenant.mp.TenantEntity;

import javax.validation.constraints.NotNull;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MenuVo extends TenantEntity {

	@NotNull(message = "角色id不能为空")
	@JsonSerialize(
		using = ToStringSerializer.class
	)
	private Long roleId;

	@NotNull(message = "菜单权限id不能为空")
	private List<Long> ml;
}
