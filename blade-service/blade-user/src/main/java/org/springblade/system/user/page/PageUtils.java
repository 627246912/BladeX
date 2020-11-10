package org.springblade.system.user.page;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class PageUtils implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long total;
	//每页记录数
	private Long size;
	//总页数
	private Long pages;
	//当前页数
	private Long current;
	//列表数据
	private List<?> records;

	private Boolean DIY;

	public PageUtils() {

	}

	/**
	 * 分页
	 *
	 * @param list       列表数据
	 * @param totalCount 总记录数
	 * @param pageSize   每页记录数
	 * @param currPage   当前页数
	 */
	public PageUtils(List<?> list, Long totalCount, Long pageSize, Long currPage) {
		this.records = list;
		this.total = totalCount;
		this.size = pageSize;
		this.current = currPage;
		this.pages = (long) Math.ceil((double) totalCount / pageSize);
	}

	public PageUtils(List<?> list, Long totalCount, Long pageSize, Long currPage, Boolean DIY) {
		this.records = list.stream().skip((currPage - 1) * pageSize).limit(pageSize).collect(Collectors.toList());
		this.total = totalCount;
		this.size = pageSize;
		this.current = currPage;
		this.pages = (long) Math.ceil((double) totalCount / pageSize);
		this.DIY = DIY;

	}

	/**
	 * 分页
	 */
	public PageUtils(Page<?> page) {
		this.records = page.getRecords();
		this.total = page.getTotal();
		this.size = page.getSize();
		this.current = page.getCurrent();
		this.pages = page.getPages();
	}

}
