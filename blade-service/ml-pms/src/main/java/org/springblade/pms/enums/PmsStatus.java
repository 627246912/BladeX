package org.springblade.pms.enums;

/**
 * @Auther: wangqiaoqing
 * @Date: 2019/3/13
 * @Description:
 */
public enum PmsStatus {
	EARLY(0,"一般"),  ORDINARY(1,"故障"),SERIOUS(2,"事故"),
	OFFLINE(3,"离线"),ONLINE(4,"在线");

    public Integer id;
    public String value;

    PmsStatus(Integer id, String value) {
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
		PmsStatus[] itemBtypes = PmsStatus.values();
		for (PmsStatus itemBtype : itemBtypes) {
			if(id.intValue() == itemBtype.id.intValue()){
				return itemBtype.value;
			}
		}
		return "";
	}
}
