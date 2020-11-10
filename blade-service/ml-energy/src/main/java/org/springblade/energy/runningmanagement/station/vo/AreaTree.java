package org.springblade.energy.runningmanagement.station.vo;

import lombok.Data;

import java.util.List;

/**
 * @author bond
 * @date 2020/3/13 16:43
 * @desc ：区域树
 */
@Data
public class AreaTree {
	private String areaCode;
	private String areaName;
	private String parentId;
	private List<AreaTree> childAreaList;
}
