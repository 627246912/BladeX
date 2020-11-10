package org.springblade.system.user.page;

import lombok.Data;

@Data
public class PageQuery {
	private int current = 1;
	private int size = 10;
}
