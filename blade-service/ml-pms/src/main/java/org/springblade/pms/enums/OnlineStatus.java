package org.springblade.pms.enums;

/**
 * @Auther: wangqiaoqing
 * @Date: 2019/3/13
 * @Description:
 */
public enum OnlineStatus {
    ONLINE(0,"在线"),UNLINE(1,"离线");

    public Integer id;
    public String value;

    OnlineStatus(Integer id, String value) {
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
		OnlineStatus[] itemBtypes = OnlineStatus.values();
		for (OnlineStatus itemBtype : itemBtypes) {
			if(id.intValue() == itemBtype.id.intValue()){
				return itemBtype.value;
			}
		}
		return "";
	}
}
