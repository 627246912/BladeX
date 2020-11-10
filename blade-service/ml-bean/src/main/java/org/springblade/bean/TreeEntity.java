package org.springblade.bean;

import java.util.List;

/**
 * @Auther: wangqiaoqing
 * @Date: 2018/7/27
 * @Description: 树形实体
 */
public interface TreeEntity<T> {
    public String getIdStr();
    public String getParentIdStr();
    public Integer getSortNo();
    public String getNameStr();
    public Integer getRank();
    public void setChildList(List<T> childList);

    /**
     * 用于过滤
     * @param params
     * @return
     */
    public boolean filterByParam(String params);

    /**
     * 用户统计子节点业务数量
     */
    public void setChildBizCount(Integer bizCount);
    public Integer getBizCount();
}
