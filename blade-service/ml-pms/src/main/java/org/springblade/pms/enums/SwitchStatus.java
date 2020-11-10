package org.springblade.pms.enums;

/**
 * @Auther: wangqiaoqing
 * @Date: 2019/3/13
 * @Description:
 */
public enum SwitchStatus {
    OFF(0,"断开"),ON(1,"合并");

    public Integer id;
    public String value;

    SwitchStatus(Integer id, String value) {
        this.id = id;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

	public static String getValue(Integer id){
		SwitchStatus[] itemBtypes = SwitchStatus.values();
		for (SwitchStatus itemBtype : itemBtypes) {
			if(id.intValue() == itemBtype.id.intValue()){
				return itemBtype.value;
			}
		}
		return "";
	}
}
