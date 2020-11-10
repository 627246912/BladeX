/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package org.springblade.energy.equipmentmanagement.eec.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springblade.energy.equipmentmanagement.eec.entity.EecMeter;
import org.springblade.energy.equipmentmanagement.eec.vo.EVO;
import org.springblade.energy.equipmentmanagement.eec.vo.EecMeterVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 重点能耗设备-》仪表配置 Mapper 接口
 *
 * @author bond
 * @since 2020-05-06
 */
public interface EecMeterMapper extends BaseMapper<EecMeter> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param eecMeter
	 * @return
	 */
	List<EecMeterVO> selectEecMeterPage(IPage page, EecMeterVO eecMeter);


	@Select("SELECT id, equ_no AS name ,5 as type FROM t_eec_meter AS eec \n" +
		"\t\tWHERE eec.site_id = #{sid} AND eec.is_deleted = 0 \n" +
		"\t\tUNION \n" +
		"\t\tSELECT id, name,1 as type FROM t_equipment_cabinet AS pro WHERE \n" +
		"\t\tpro.site_id = #{sid}  AND pro.is_deleted = 0")
	List<EVO> selectEVO(@Param("sid") Long sid);

}
