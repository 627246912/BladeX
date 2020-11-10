package org.springblade.system.user.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserJoinUserShiftVo {

	@JsonSerialize(
		using = ToStringSerializer.class
	)
	private Long id;

	private String realName;

	private String shiftCycle;
}
