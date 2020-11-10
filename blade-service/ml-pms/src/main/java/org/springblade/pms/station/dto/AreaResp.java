package org.springblade.pms.station.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import org.springblade.bean.AbstractTreeEntity;
import org.springblade.util.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * @author bond
 * @date 2020/9/1 16:57
 * @desc
 */
public class AreaResp extends AbstractTreeEntity<AreaResp> implements Serializable {
	@ApiModelProperty(value = "区域名")
	private String areaName;
	@ApiModelProperty(value = "区域编码")
	private String areaCode;
	@ApiModelProperty(value = "父级id")
	private String parentId;
	@ApiModelProperty(value = "级别 0:省,1:市,2:区域,3:基站")
	private Integer areaRank;
	@ApiModelProperty(value = "排序")
	private Integer orderNum;
	@ApiModelProperty(value = "子集区域")
	private List<AreaResp> childAreaList;
	@ApiModelProperty(value = "区分id用")
	private String disStr;
	@ApiModelProperty(value = "区分父级id用",hidden = true)
	private String parentDisStr;



	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public Integer getAreaRank() {
		return areaRank;
	}

	public void setAreaRank(Integer areaRank) {
		this.areaRank = areaRank;
	}

	public Integer getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}

	public List<AreaResp> getChildAreaList() {
		return childAreaList;
	}

	public void setChildAreaList(List<AreaResp> childAreaList) {
		this.childAreaList = childAreaList;
	}

	@JsonIgnore
	public String getDisStr() {
		return disStr;
	}

	public void setDisStr(String disStr) {
		this.disStr = disStr;
	}

	@JsonIgnore
	public String getParentDisStr() {
		return parentDisStr;
	}

	public void setParentDisStr(String parentDisStr) {
		this.parentDisStr = parentDisStr;
	}

	@Override
	public String getIdStr() {
		return areaCode+StringUtils.nvl(disStr,"");
	}

	@Override
	public String getParentIdStr() {
		return parentId+ StringUtils.nvl(parentDisStr,"");
	}

	@Override
	public Integer getSortNo() {
		return orderNum;
	}

	@Override
	public String getNameStr() {
		return areaName;
	}

	@Override
	public Integer getRank() {
		return this.areaRank;
	}

	@Override
	public void setChildList(List<AreaResp> childList) {
		this.childAreaList = childList;
	}
}
