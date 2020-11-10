package org.springblade.pms.station.vo;

import lombok.Data;
import org.springblade.pms.station.entity.BaseStation;

import java.util.List;

/**
 * @author bond
 * @date 2020/3/13 16:43
 * @desc ：区域树
 */
@Data
public class StationAreaTree {
	private String areaCode;
	private String areaName;
	private String parentId;
	private List<StationAreaTree> childAreaList;
	private List<BaseStation> stationList;
}
