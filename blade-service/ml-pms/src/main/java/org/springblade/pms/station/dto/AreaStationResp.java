package org.springblade.pms.station.dto;

import lombok.Data;
import org.springblade.pms.station.entity.BaseStation;

import java.util.List;

/**
 * @author bond
 * @date 2020/3/13 16:43
 * @desc ：区域树
 */
@Data
public class AreaStationResp {
	private String areaCode;
	private String areaName;
	private String parentId;
	private List<BaseStation> stationList;
}
