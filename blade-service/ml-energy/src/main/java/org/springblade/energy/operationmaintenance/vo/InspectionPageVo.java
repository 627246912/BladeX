package org.springblade.energy.operationmaintenance.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InspectionPageVo {

	private String responsiblePersonName;

	@JsonSerialize(using = ToStringSerializer.class)
	private Long responsiblePersonId;

	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	private Date date;

	private String status;
}
