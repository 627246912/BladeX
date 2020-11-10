package org.springblade.energy.equipmentmanagement.eec.vo;

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
public class EVO {

	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;

	private String name;

	private Integer type;
}
