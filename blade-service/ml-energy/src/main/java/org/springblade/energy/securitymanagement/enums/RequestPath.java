package org.springblade.energy.securitymanagement.enums;

import lombok.Getter;

@Getter
public enum RequestPath {

	ADD("add", "添加"),
	LIST("list", "列表"),
	CONDITION_QUERY("conditionQuery", "条件查询"),
	UPDATE("update", "编辑"),
	BATCH_DELETED("batchDeleted", "批量删除");

	private final String val;
	private final String title;

	RequestPath(String val, String title) {
		this.val = val;
		this.title = title;
	}
}
