package org.springblade.energy.qualitymanagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springblade.energy.qualitymanagement.entity.EnergyMaintenance;
import org.springblade.energy.qualitymanagement.vo.DevNameVo;

public interface EnergyMaintenanceMapper extends BaseMapper<EnergyMaintenance> {

	@Select("SELECT equ_no AS name  FROM t_eec_meter AS eec " +
		"WHERE eec.id = #{id} AND eec.is_deleted = 0 " +
		"UNION " +
		"SELECT name FROM t_equipment_cabinet AS pro WHERE " +
		"pro.id = #{id}  AND pro.is_deleted = 0")
	DevNameVo selectDevName(@Param("id") Long id);
}
