package org.springblade.energy.securitymanagement.enums;

import lombok.Getter;

@Getter
public enum Status {
	ADD_SUCCESS("添加成功"),
	ADD_ERROR("添加失败"),
	UPDATE_SUCCESS("编辑成功"),
	UPDATE_ERROR("编辑失败"),
	DELETE_SUCCESS("删除成功"),
	DELETE_ERROR("删除失败");

	private final String val;

	Status(String val) {
		this.val = val;
	}
}
