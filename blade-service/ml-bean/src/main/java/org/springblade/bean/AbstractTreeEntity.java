package org.springblade.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springblade.util.FilterUtil;

import java.util.List;

/**
 * @Auther: wangqiaoqing
 * @Date: 2019/3/11
 * @Description:
 */
public abstract class AbstractTreeEntity<T> implements TreeEntity<T> {

    @JsonIgnore
    @Override
    public String getIdStr() {
        return "";
    }

    @JsonIgnore
    @Override
    public String getParentIdStr() {
        return "0";
    }

    @JsonIgnore
    @Override
    public Integer getSortNo() {
        return 0;
    }

    @JsonIgnore
    @Override
    public String getNameStr() {
        return "-";
    }

    @JsonIgnore
    @Override
    public Integer getRank() {
        return 0;
    }

    @JsonIgnore
    @Override
    public void setChildList(List<T> childList) {

    }

    @JsonIgnore
    @Override
    public boolean filterByParam(String params) {
		String nameStr=getNameStr();
       return FilterUtil.likeStr(nameStr,params);
    }

    @JsonIgnore
    @Override
    public void setChildBizCount(Integer bizCount) {

    }

    @JsonIgnore
    @Override
    public Integer getBizCount() {
        return 0;
    }
}
