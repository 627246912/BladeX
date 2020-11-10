package org.springblade.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: wangqiaoqing
 * @Date: 2018/7/27
 * @Description:数据项类型
 */
public enum ItemStype {
    TRANSPORTYC(1,"传输遥测"),TRANSPORTYX(2,"传输遥信"),TRANSPORTYT(3,"传输遥调"),TRANSPORTYK(4,"传输遥控"),
    OPERATIONYC(5,"计算遥测"),OPERATIONYX(6,"计算遥信");

    public Integer id;
    public String value;

    ItemStype(Integer id, String value) {
        this.id = id;
        this.value = value;
    }

    /**
     * 获取对应属性值
     * @param id
     * @return
     */
    public static ItemStype getItemType(Integer id){
        ItemStype[] itemStypes = ItemStype.values();
        for (ItemStype itemStype : itemStypes) {
            if(id.intValue() == itemStype.id.intValue()){
                return itemStype;
            }
        }
        return null;
    }

	public static List<ItemStype> ItemStypeList(){
		List<ItemStype> list =	new ArrayList<>();

		list.add(ItemStype.TRANSPORTYC);
		list.add(ItemStype.TRANSPORTYX);
		list.add(ItemStype.TRANSPORTYT);
		list.add(ItemStype.TRANSPORTYK);
		list.add(ItemStype.OPERATIONYC);
		list.add(ItemStype.OPERATIONYX);

		return  list;
	}
}
