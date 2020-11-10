package org.springblade.pms.enums;

import org.springblade.core.tool.utils.Func;

/**
 * @Auther: wangqiaoqing
 * @Date: 2019/3/13
 * @Description:
 */
public enum UserGroup {
    TIETA("4","铁塔"),YIDONG("1","移动"),LIANTONG("2","联通"),DIANXIN("3","电信"),
	GUANGDIAN("5","广电"),BEIYONG("0","备用");;

    public String id;
    public String value;

    UserGroup(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

	public static String getValue(String id){
		UserGroup[] itemBtypes = UserGroup.values();
		for (UserGroup itemBtype : itemBtypes) {
			if(Func.equals(id ,itemBtype.id)){
				return itemBtype.value;
			}
		}
		return "";
	}
}
