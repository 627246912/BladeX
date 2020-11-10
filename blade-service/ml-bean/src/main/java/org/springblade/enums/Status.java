package org.springblade.enums;

/**
 * @Auther: wangqiaoqing
 * @Date: 2018/7/30
 * @Description:
 */
public enum Status {
    NORMAL(0,"正常"),DISABLE(1,"停用");

    public Integer id;
    public String value;

    Status(Integer id, String value) {
        this.id = id;
        this.value = value;
    }
}
